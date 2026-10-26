CREATE TABLE links (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       short_code VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
                       long_url TEXT NOT NULL,
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       expires_at DATETIME(6) NULL,
                       UNIQUE KEY idx_links_short_code (short_code)
);