package com.example.demologin.serviceImpl;

import com.example.demologin.entity.Plan;
import com.example.demologin.entity.Subscription;
import com.example.demologin.entity.Transaction;
import com.example.demologin.enums.TransactionStatus;
import com.example.demologin.repository.SubscriptionRepository;
import com.example.demologin.repository.TransactionRepository;
import com.example.demologin.repository.PlanRepository;
import com.example.demologin.entity.User;
import com.example.demologin.service.VnPayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class VnPayServiceImpl implements VnPayService {

    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;
    private final PlanRepository planRepository;

    @Value("${payment.vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${payment.vnpay.secret-key}")
    private String vnpSecretKey;

    @Value("${payment.vnpay.url}")
    private String vnpUrl;

    @Value("${frontend.url.payment.return}")
    private String vnpReturnUrl;

    @Value("${payment.vnpay.ip-address}")
    private String vnpIpAddress;

    private static final String VNPAY_SUCCESS_CODE = "00";

    public VnPayServiceImpl(SubscriptionRepository subscriptionRepository, TransactionRepository transactionRepository, PlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.transactionRepository = transactionRepository;
        this.planRepository = planRepository;
    }

    @Override
    public String createVnPayUrl(Long planId) throws Exception {
        var plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // Lấy user đang đăng nhập (principal của bạn là User)
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 1) Tạo Subscription PENDING
        var sub = Subscription.builder()
                .user(user)
                .plan(plan)
                .status("PENDING")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(plan.getDurationDays()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        subscriptionRepository.save(sub);

        // 2) Tạo transactionRef duy nhất và Transaction PENDING
        String txnRef = "TXN" + sub.getId() + "_" + System.currentTimeMillis();

        var tx = Transaction.builder()
                .user(user)
                .subscription(sub)
                .amount(plan.getPrice())
                .paymentMethod("VNPAY")
                .status(TransactionStatus.PENDING)
                .transactionRef(txnRef)         // <— Dùng làm vnp_TxnRef
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(tx);

        // 3) Build URL VNPay (lưu ý: VNPay yêu cầu vnp_Amount *100 đơn vị)
        long amountInDong = (long) (plan.getPrice() * 100);

        Map<String,String> vnp = new TreeMap<>();
        vnp.put("vnp_Version", "2.1.0");
        vnp.put("vnp_Command", "pay");
        vnp.put("vnp_TmnCode", vnpTmnCode);
        vnp.put("vnp_Locale", "vn");
        vnp.put("vnp_CurrCode", "VND");
        vnp.put("vnp_TxnRef", txnRef);                         // <— KHÔNG phải subId
        vnp.put("vnp_OrderInfo", "Pay subscription " + sub.getId());
        vnp.put("vnp_OrderType", "other");
        vnp.put("vnp_Amount", String.valueOf(amountInDong));
        vnp.put("vnp_ReturnUrl", vnpReturnUrl);               // http://localhost:8080/api/payment/vnpay/vnpay-return
        vnp.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        vnp.put("vnp_IpAddr", vnpIpAddress);
        vnp.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        String signData = buildSignData(vnp);
        vnp.put("vnp_SecureHash", generateHMAC(vnpSecretKey, signData));

        return buildPaymentUrl(vnpUrl, vnp);
    }



    private String createVNPayPaymentUrl(Subscription subscription, String transactionRef) throws Exception {
        // Lấy giá trị từ Plan gắn với Subscription
        double amount = subscription.getPlan().getPrice(); // Lấy giá từ Plan

        // Chuyển đổi sang VNĐ (tiền đồng)
        long amountInVND = Math.round(amount * 100); // Làm tròn số sau khi nhân với 100 để đảm bảo VNPay nhận giá trị hợp lệ

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", transactionRef);  // Sử dụng transactionRef duy nhất
        vnpParams.put("vnp_OrderInfo", "Payment for subscription: " + subscription.getId());
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Amount", String.valueOf(amountInVND)); // Sử dụng số tiền đã làm tròn
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_CreateDate", LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        vnpParams.put("vnp_IpAddr", vnpIpAddress);
        vnpParams.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(30).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))); // Thời gian hết hạn 30 phút

        String signData = buildSignData(vnpParams);
        vnpParams.put("vnp_SecureHash", generateHMAC(vnpSecretKey, signData));

        return buildPaymentUrl(vnpUrl, vnpParams);
    }


    private String buildSignData(Map<String, String> params) throws Exception {
        StringBuilder signData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            signData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .append('&');
        }
        return signData.deleteCharAt(signData.length() - 1).toString();
    }

    private String generateHMAC(String secretKey, String signData) throws Exception {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        hmacSha512.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8))) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String buildPaymentUrl(String baseUrl, Map<String, String> params) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(baseUrl).append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .append('&');
        }
        return urlBuilder.deleteCharAt(urlBuilder.length() - 1).toString();
    }

    @Override
    public Map<String, String> handleVnPayReturn(Map<String, String> params) {
        Map<String, String> resp = new HashMap<>();
        try {
            String rsp = params.get("vnp_ResponseCode");               // "00" = success
            String txnRef = params.get("vnp_TxnRef");                  // "TXN10_1762304082259"

            // (Khuyến nghị) verify chữ ký VNPay – có thể bổ sung hàm verify nếu bạn muốn chắc chắn.
            // if (!verifySignature(params)) { ... }

            var tx = transactionRepository.findByTransactionRef(txnRef)
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));
            var sub = tx.getSubscription();

            if ("00".equals(rsp)) {
                // SUCCESS
                tx.setStatus(TransactionStatus.SUCCESS);
                tx.setTransactionRef(txnRef); // giữ nguyên
                transactionRepository.save(tx);

                sub.setStatus("ACTIVE"); // hoặc "PAID" tùy bạn dùng
                sub.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.save(sub);

                resp.put("RspCode", "00");
                resp.put("Message", "Payment successful");
            } else {
                // FAILED
                tx.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(tx);

                sub.setStatus("FAILED");
                sub.setUpdatedAt(LocalDateTime.now());
                subscriptionRepository.save(sub);

                resp.put("RspCode", "99");
                resp.put("Message", "Payment failed with code: " + rsp);
            }
        } catch (Exception e) {
            resp.put("RspCode", "99");
            resp.put("Message", "Error processing payment: " + e.getMessage());
        }
        return resp;
    }

    @Override
    public void processSuccessfulPayment(Subscription subscription, String transactionId) {
        // Tạo giao dịch mới và lưu vào bảng Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(subscription.getPlan().getPrice());
        transaction.setPaymentMethod("VNPAY");
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTransactionRef(transactionId);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSubscription(subscription);
        transactionRepository.save(transaction);

        // Cập nhật trạng thái subscription là "PAID"
        subscription.setStatus("PAID");
        subscriptionRepository.save(subscription);
    }

    @Override
    public void processFailedPayment(Subscription subscription) {
        // Tạo giao dịch mới và lưu vào bảng Transaction với trạng thái thất bại
        Transaction transaction = new Transaction();
        transaction.setAmount(subscription.getPlan().getPrice());
        transaction.setPaymentMethod("VNPAY");
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setTransactionRef("FAILED-" + subscription.getId());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setSubscription(subscription);
        transactionRepository.save(transaction);

        // Cập nhật trạng thái subscription là "FAILED"
        subscription.setStatus("FAILED");
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void cancelBySubscriptionId(Long subscriptionId, Long currentUserId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (sub.getUser() == null || !sub.getUser().getUserId().equals(currentUserId)) {
            throw new RuntimeException("You are not allowed to cancel this subscription");
        }

        // Lấy giao dịch mới nhất thuộc subscription (tuỳ bạn, có thể chỉ lấy PENDING)
        Transaction tx = transactionRepository
                .findTopBySubscriptionIdOrderByCreatedAtDesc(subscriptionId)
                .orElse(null);

        if (tx != null) {
            if (tx.getStatus() == TransactionStatus.SUCCESS) {
                throw new RuntimeException("Cannot cancel a subscription with successful transaction");
            }
            if (tx.getStatus() != TransactionStatus.CANCELLED) {
                tx.setStatus(TransactionStatus.CANCELLED);
                transactionRepository.save(tx);
            }
        }

        sub.setStatus("CANCELLED");
        sub.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.save(sub);
    }
}
