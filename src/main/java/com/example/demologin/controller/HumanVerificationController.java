package com.example.demologin.controller;

import com.example.demologin.annotation.PublicEndpoint;
import com.example.demologin.dto.request.VerifyTokenRequest;
import com.example.demologin.dto.response.VerifyTokenResponse;
import com.example.demologin.utils.HumanVerifyTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HumanVerificationController {

    @Value("${cloudflare.turnstile.secret-key}")
    private String turnstileSecretKey;

    @Value("${verify.token.secret}")
    private String verifyTokenSecret;

    @Value("${verify.token.expiry-ms:86400000}") // mặc định 1 ngày
    private int verifyTokenExpiryMs;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping(value = "/verify-human", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PublicEndpoint
    public ResponseEntity<VerifyTokenResponse> verifyHuman(@RequestBody VerifyTokenRequest request) {
        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

        // Chuẩn bị body form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", turnstileSecretKey);
        body.add("response", request.getToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // Gửi request đến Cloudflare
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        if (response != null && Boolean.TRUE.equals(response.get("success"))) {
            // Xác thực OK => tạo HUMAN_VERIFY_TOKEN
            HumanVerifyTokenUtil util = new HumanVerifyTokenUtil(verifyTokenSecret, verifyTokenExpiryMs);
            String jwt = util.generateToken();
            return ResponseEntity.ok(new VerifyTokenResponse(jwt));
        } else {
            // Fail => trả 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new VerifyTokenResponse(null));
        }
    }
}
