-- Migration script to update user_activity_logs table
-- Add new columns for device and location information
-- Change username to fullName and remove editor related columns

-- Rename username column to full_name
ALTER TABLE user_activity_logs 
CHANGE COLUMN username full_name VARCHAR(100);

-- Add new columns for device information
ALTER TABLE user_activity_logs 
ADD COLUMN browser VARCHAR(100),
ADD COLUMN browser_version VARCHAR(50),
ADD COLUMN operating_system VARCHAR(100),
ADD COLUMN device VARCHAR(100),
ADD COLUMN device_type VARCHAR(20);

-- Add new columns for location information
ALTER TABLE user_activity_logs 
ADD COLUMN city VARCHAR(100),
ADD COLUMN region VARCHAR(100),
ADD COLUMN country VARCHAR(100),
ADD COLUMN country_code VARCHAR(10);

-- Drop editor related columns (if they exist)
ALTER TABLE user_activity_logs 
DROP COLUMN IF EXISTS editor_id,
DROP COLUMN IF EXISTS editor_username;

-- Update existing records to parse user_agent and ip_address
-- This would need to be done programmatically or manually
-- You can run this after updating the application code
