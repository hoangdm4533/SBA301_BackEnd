package com.example.demologin.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.example.demologin.dto.request.lesson_plan.LessonPlanEditRequest;
import com.example.demologin.dto.response.LessonPlanEditResponse;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.LessonPlanEdit;
import com.example.demologin.repository.LessonPlanEditRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.service.LessonPlanCompactionService;
import com.example.demologin.service.LessonPlanEditService;
import com.example.demologin.service.ObjectStorageService;
import com.example.demologin.utils.lessonPlanEdit.ILessonPlanEditUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Component
@Slf4j
public class LessonPlanWebSocketHandler {

    private final SocketIOServer server;
    private final LessonPlanEditService editService;
    private final LessonPlanCompactionService compactionService;
    private final ObjectStorageService objectStorageService;
    private final ILessonPlanEditUtil lessonPlanEditUtil;


    // Mỗi lessonPlanId có 1 hàng đợi edit riêng
    private final Map<Long, BlockingQueue<LessonPlanEditRequest>> editQueues = new ConcurrentHashMap<>();

    // Mỗi lessonPlanId có 1 thread xử lý riêng
    private final Map<Long, ExecutorService> executorsByLessonPlan = new ConcurrentHashMap<>();

    // Lưu client theo lessonPlanId
    private final Map<Long, Set<SocketIOClient>> clientsByLessonPlan = new ConcurrentHashMap<>();
    private final LessonPlanRepository lessonPlanRepository;
    private final LessonPlanEditRepository lessonPlanEditRepository;

    public LessonPlanWebSocketHandler(SocketIOServer server,
                                      LessonPlanEditService editService,
                                      LessonPlanCompactionService compactionService,
                                      LessonPlanRepository lessonPlanRepository,
                                      LessonPlanEditRepository lessonPlanEditRepository,
                                      ObjectStorageService objectStorageService,
                                      ILessonPlanEditUtil lessonPlanEditUtil) {
        this.server = server;
        this.editService = editService;
        this.compactionService = compactionService;
        initListeners();
        this.server.start();
        this.lessonPlanRepository = lessonPlanRepository;
        this.lessonPlanEditRepository = lessonPlanEditRepository;
        this.objectStorageService = objectStorageService;
        this.lessonPlanEditUtil = lessonPlanEditUtil;
    }

    // =============================
    // 1️⃣ Khởi tạo socket listeners
    // =============================
    private void initListeners() {
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("editLessonPlan", LessonPlanEditRequest.class, onEditLessonPlan());
    }

    private ConnectListener onConnected() {
        return client -> {
            String lessonPlanIdStr = client.getHandshakeData().getSingleUrlParam("lessonPlanId");
            if (lessonPlanIdStr == null) {
                client.disconnect();
                return;
            }
            Long lessonPlanId = Long.valueOf(lessonPlanIdStr);

            clientsByLessonPlan.computeIfAbsent(lessonPlanId, k -> ConcurrentHashMap.newKeySet()).add(client);

            // Khởi tạo queue & worker nếu chưa có
            initLessonPlanQueueIfAbsent(lessonPlanId);

            System.out.println("Client joined lessonPlanId=" + lessonPlanId);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String lessonPlanIdStr = client.getHandshakeData().getSingleUrlParam("lessonPlanId");
            if (lessonPlanIdStr == null) return;

            Long lessonPlanId = Long.valueOf(lessonPlanIdStr);

            Set<SocketIOClient> clients = clientsByLessonPlan.get(lessonPlanId);
            if (clients == null) {
                log.warn("Disconnect event for lessonPlanId={} but clients set is null", lessonPlanId);
                return;
            }

            clients.remove(client);
            System.out.println("Client left lessonPlanId=" + lessonPlanId + ", remaining=" + clients.size());

            // ⚠️ Kiểm tra lại để tránh race condition (có thể thread khác đã cleanup)
            if (clients.isEmpty()) {
                synchronized (this) {
                    // double-check sau khi lock
                    Set<SocketIOClient> latestClients = clientsByLessonPlan.get(lessonPlanId);
                    if (latestClients == null || latestClients.isEmpty()) {
                        System.out.println("Trigger compaction for lessonPlanId=" + lessonPlanId);
                        compactionService.compactLessonPlan(lessonPlanId);
                        cleanupLessonPlanResources(lessonPlanId);
                    }
                }
            }
        };
    }


    // =============================
    // 2️⃣ Khi client gửi edit event
    // =============================
    private DataListener<LessonPlanEditRequest> onEditLessonPlan() {
        return (client, data, ackSender) -> {
            Long lessonPlanId = data.getLessonPlanId();
            initLessonPlanQueueIfAbsent(lessonPlanId);
            System.out.println("tôi đang ở onedit lessonplan");
            // Đưa request vào queue của lessonPlan tương ứng
            editQueues.get(lessonPlanId).offer(data);
        };
    }

    // =============================
    // 3️⃣ Worker xử lý queue
    // =============================
    private void initLessonPlanQueueIfAbsent(Long lessonPlanId) {
        if (lessonPlanId == null) {
            log.error("lessonPlanId is null when initializing queue");
            return;
        }
        editQueues.computeIfAbsent(lessonPlanId, id -> new LinkedBlockingQueue<>());
        executorsByLessonPlan.computeIfAbsent(lessonPlanId, id -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> processLessonPlanQueue(id));
            return executor;
        });
    }

    private void processLessonPlanQueue(Long lessonPlanId) {
        BlockingQueue<LessonPlanEditRequest> queue = editQueues.get(lessonPlanId);
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Chờ đến khi có edit mới, không dừng sớm
                LessonPlanEditRequest req = queue.take();

                // Lưu edit vào DB
                LessonPlanEditResponse saved = editService.saveEdit(req);

                // Merge edits vào LessonPlan
//                updateLessonPlanTemp(lessonPlanId);

                // Broadcast tới tất cả client đang kết nối
                broadcastToLessonPlan(lessonPlanId, saved, null);

            } catch (InterruptedException e) {
                // Nếu thread bị interrupt, thoát vòng lặp
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Worker stopped for lessonPlanId=" + lessonPlanId);
    }

    // =============================
    // 4️⃣ Broadcast cho các client
    // =============================
    private void broadcastToLessonPlan(Long lessonPlanId, LessonPlanEditResponse saved, SocketIOClient excludeClient) {
        Set<SocketIOClient> clients = clientsByLessonPlan.getOrDefault(lessonPlanId, Set.of());
        for (SocketIOClient c : clients) {
            if (excludeClient == null || !c.getSessionId().equals(excludeClient.getSessionId())) {
                c.sendEvent("lessonPlanUpdated", saved);
            }
        }
    }

    // =============================
    // 5️⃣ Cleanup khi lesson đóng hết
    // =============================
    private void cleanupLessonPlanResources(Long lessonPlanId) {
        editQueues.remove(lessonPlanId);
        ExecutorService ex = executorsByLessonPlan.remove(lessonPlanId);
        if (ex != null) {
            ex.shutdownNow();
        }
        clientsByLessonPlan.remove(lessonPlanId);
    }

    // =============================
    // update khi lesson plan edit lên minio
    // =============================
    private void updateLessonPlanTemp(Long lessonPlanId) {
        try {
            // 1️⃣ Lấy LessonPlan hiện tại
            LessonPlan lessonPlan = lessonPlanRepository.findById(lessonPlanId)
                    .orElseThrow(() -> new IllegalArgumentException("LessonPlan not found"));

            // 2️⃣ Lấy base content từ DB
            String baseContent = lessonPlan.getContent();
            if (baseContent == null) {
                baseContent = "";
            }

            // 3️⃣ Lấy toàn bộ edits chưa compact
            List<LessonPlanEdit> edits = lessonPlanEditRepository
                    .findByLessonPlanIdOrderByCreatedAtAsc(lessonPlanId);

            if (edits.isEmpty()) {
                System.out.println("[DB] No edits to apply for lessonPlanId=" + lessonPlanId);
                return;
            }

            // 4️⃣ Merge edits vào base content
            String merged = lessonPlanEditUtil.applyEdits(baseContent, edits);

            // 5️⃣ Update lại LessonPlan trong DB
            lessonPlan.setContent(merged);
            lessonPlan.setUpdatedAt(LocalDateTime.now());
            lessonPlanRepository.save(lessonPlan);

            System.out.println("[DB] ✅ Updated lessonPlan content for lessonPlanId=" + lessonPlanId);

        } catch (Exception e) {
            System.err.println("[DB] ❌ Exception while updating content for lessonPlanId="
                    + lessonPlanId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
