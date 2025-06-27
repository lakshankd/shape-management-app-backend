package com.spillabs.shape_management_appliaction_api.job;

import com.spillabs.shape_management_appliaction_api.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class TokenCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupScheduler.class);

    @Autowired
    private RefreshTokenService refreshTokenService;

    // Clean up expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired refresh tokens");
        try {
            refreshTokenService.cleanupExpiredTokens();
            logger.info("Completed cleanup of expired refresh tokens");
        } catch (Exception e) {
            logger.error("Error during token cleanup: {}", e.getMessage(), e);
        }
    }
}
