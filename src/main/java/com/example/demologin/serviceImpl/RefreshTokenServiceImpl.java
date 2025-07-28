package com.example.demologin.serviceImpl;

import com.example.demologin.dto.response.TokenRefreshResponse;
import com.example.demologin.entity.RefreshToken;
import com.example.demologin.entity.User;
import com.example.demologin.exception.exceptions.TokenRefreshException;
import com.example.demologin.repository.RefreshTokenRepository;
import com.example.demologin.service.RefreshTokenService;
import com.example.demologin.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${jwt.refresh.expiration.ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshToken ->
                refreshTokenRepository.delete(refreshToken)
        );
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByAccount(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    public TokenRefreshResponse refreshToken(String requestRefreshToken) {
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = tokenService.generateToken(user);
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
} 