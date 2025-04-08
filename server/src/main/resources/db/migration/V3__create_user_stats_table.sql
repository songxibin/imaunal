CREATE TABLE user_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    total_word_count BIGINT DEFAULT 0,
    language_count INTEGER DEFAULT 0,
    storage_used BIGINT DEFAULT 0,
    storage_limit BIGINT DEFAULT 5368709120, -- 5GB default
    storage_used_formatted VARCHAR(20),
    storage_limit_formatted VARCHAR(20),
    storage_usage_percent DOUBLE PRECISION DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);