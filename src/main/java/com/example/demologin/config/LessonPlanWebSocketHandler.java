package com.example.demologin.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.example.demologin.dto.request.lesson_plan.LessonPlanEditRequest;
import com.example.demologin.dto.response.LessonPlanEditResponse;
import com.example.demologin.service.LessonPlanCompactionService;
import com.example.demologin.service.LessonPlanEditService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LessonPlanWebSocketHandler {

    private final SocketIOServer server;
    private final LessonPlanEditService editService;
    private final LessonPlanCompactionService compactionService;

    // Lưu client theo lessonPlanId
    private final Map<Long, Set<SocketIOClient>> clientsByLessonPlan = new ConcurrentHashMap<>();

    public LessonPlanWebSocketHandler(SocketIOServer server,
                                      LessonPlanEditService editService,
                                      LessonPlanCompactionService compactionService) {
        this.server = server;
        this.editService = editService;
        this.compactionService = compactionService;
        initListeners();
        this.server.start();
    }

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

            clientsByLessonPlan.computeIfAbsent(lessonPlanId, k -> ConcurrentHashMap.newKeySet())
                    .add(client);

            System.out.println("Client joined lessonPlanId=" + lessonPlanId);
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String lessonPlanIdStr = client.getHandshakeData().getSingleUrlParam("lessonPlanId");
            if (lessonPlanIdStr == null) return;

            Long lessonPlanId = Long.valueOf(lessonPlanIdStr);
            Set<SocketIOClient> clients = clientsByLessonPlan.getOrDefault(lessonPlanId, Set.of());
            clients.remove(client);

            System.out.println("Client left lessonPlanId=" + lessonPlanId + ", remaining=" + clients.size());

            if (clients.isEmpty()) {
                System.out.println("Trigger compaction for lessonPlanId=" + lessonPlanId);
                compactionService.compactLessonPlan(lessonPlanId);
                clientsByLessonPlan.remove(lessonPlanId);
            }
        };
    }

    private DataListener<LessonPlanEditRequest> onEditLessonPlan() {
        return (client, data, ackSender) -> {
            LessonPlanEditResponse saved = editService.saveEdit(data);

            Long lessonPlanId = data.getLessonPlanId();
            // Gửi broadcast cho tất cả client khác trong cùng lessonPlan
            for (SocketIOClient c : clientsByLessonPlan.getOrDefault(lessonPlanId, Set.of())) {
                if (!c.getSessionId().equals(client.getSessionId())) {
                    c.sendEvent("lessonPlanUpdated", saved);
                }
            }
        };
    }
}
