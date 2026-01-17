-- Add soft delete column to stocks table
ALTER TABLE stocks ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Create index for faster filtering
CREATE INDEX idx_stocks_deleted ON stocks(deleted);