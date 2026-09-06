-- Constraints backing application-level idempotency and ticket verification.
ALTER TABLE bookings ADD CONSTRAINT uk_bookings_idempotency_key UNIQUE (idempotency_key);
ALTER TABLE payments ADD CONSTRAINT uk_payments_idempotency_key UNIQUE (idempotency_key);
ALTER TABLE tickets ADD CONSTRAINT uk_tickets_verification_token UNIQUE (verification_token);

-- Matches the history lookup used by the GPS service and supports retention jobs.
CREATE INDEX idx_bus_locations_bus_recorded_at ON bus_locations(bus_id, recorded_at DESC);
CREATE INDEX idx_bookings_user_created_at ON bookings(user_id, created_at DESC);
