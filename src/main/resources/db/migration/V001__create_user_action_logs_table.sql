-- Migration script to create user_action_logs table
-- This replaces the admin_action_logs table with a more flexible and dynamic approach

CREATE TABLE user_action_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(28) NOT NULL,
    role_name VARCHAR(255),
    action_type ENUM('CREATE', 'UPDATE', 'DELETE', 'CHANGE_ROLE', 'LOGIN', 'LOGOUT', 'VIEW', 'EXPORT', 'IMPORT', 'OTHER') NOT NULL,
    target_type VARCHAR(100),
    target_id VARCHAR(100),
    target_name VARCHAR(255),
    description TEXT,
    reason TEXT,
    change_summary TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_username (username),
    INDEX idx_action_type (action_type),
    INDEX idx_target_type (target_type),
    INDEX idx_action_time (action_time)
);

-- Optional: Migrate existing admin_action_logs data
-- Uncomment the following if you want to preserve historical data
/*
INSERT INTO user_action_logs (
    user_id, 
    username, 
    role_name, 
    action_type, 
    target_type, 
    target_id, 
    reason, 
    change_summary, 
    action_time
)
SELECT 
    aal.admin_id as user_id,
    'MIGRATED' as username,
    'ADMIN' as role_name,
    aal.action_type,
    aal.target_type,
    aal.target_id,
    aal.reason,
    aal.change_summary,
    aal.action_time
FROM admin_action_logs aal;
*/
