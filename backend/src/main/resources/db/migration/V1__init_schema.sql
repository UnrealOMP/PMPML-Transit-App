CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE kyc_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    provider_reference VARCHAR(200),
    provider_name VARCHAR(100),
    verification_timestamp TIMESTAMPTZ,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE buses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bus_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    license_plate VARCHAR(50),
    manufacturer VARCHAR(100),
    manufacturing_year INTEGER,
    has_gps BOOLEAN NOT NULL DEFAULT TRUE,
    has_cctv BOOLEAN NOT NULL DEFAULT FALSE,
    accessible BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE stops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stop_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    latitude NUMERIC(10,7) NOT NULL,
    longitude NUMERIC(10,7) NOT NULL,
    address VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE routes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE route_stops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route_id UUID NOT NULL REFERENCES routes(id) ON DELETE CASCADE,
    stop_id UUID NOT NULL REFERENCES stops(id),
    sequence_order INTEGER NOT NULL,
    distance_from_start_km NUMERIC(10,2),
    distance_to_next_stop_km NUMERIC(10,2),
    is_first_stop BOOLEAN NOT NULL DEFAULT FALSE,
    is_last_stop BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    UNIQUE(route_id, stop_id),
    UNIQUE(route_id, sequence_order)
);

CREATE TABLE trips (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bus_id UUID NOT NULL REFERENCES buses(id),
    route_id UUID NOT NULL REFERENCES routes(id),
    scheduled_departure TIMESTAMPTZ NOT NULL,
    scheduled_arrival TIMESTAMPTZ NOT NULL,
    actual_departure TIMESTAMPTZ,
    actual_arrival TIMESTAMPTZ,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    available_seats INTEGER NOT NULL DEFAULT 0,
    delayed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE fare_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vehicle_type VARCHAR(20) NOT NULL,
    minimum_fare NUMERIC(10,2) NOT NULL,
    distance_from_km NUMERIC(10,2) NOT NULL,
    distance_to_km NUMERIC(10,2) NOT NULL,
    fare NUMERIC(10,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    effective_from TIMESTAMPTZ,
    effective_to TIMESTAMPTZ,
    version_number INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_reference VARCHAR(50) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id),
    trip_id UUID NOT NULL REFERENCES trips(id),
    source_stop_id UUID NOT NULL REFERENCES stops(id),
    destination_stop_id UUID NOT NULL REFERENCES stops(id),
    fare_amount NUMERIC(10,2) NOT NULL,
    idempotency_key VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    seat_number INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_reference VARCHAR(50) NOT NULL UNIQUE,
    booking_id UUID NOT NULL REFERENCES bookings(id),
    amount NUMERIC(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    provider_transaction_id VARCHAR(200),
    provider_name VARCHAR(100),
    paid_at TIMESTAMPTZ,
    refunded_at TIMESTAMPTZ,
    failure_reason VARCHAR(500),
    idempotency_key VARCHAR(50) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE tickets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    booking_id UUID NOT NULL REFERENCES bookings(id),
    user_id UUID NOT NULL REFERENCES users(id),
    trip_id UUID NOT NULL REFERENCES trips(id),
    bus_id UUID NOT NULL REFERENCES buses(id),
    route_id UUID NOT NULL REFERENCES routes(id),
    source_stop_id UUID NOT NULL REFERENCES stops(id),
    destination_stop_id UUID NOT NULL REFERENCES stops(id),
    fare NUMERIC(10,2) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    verification_token VARCHAR(256) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE ticket_verifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_id UUID NOT NULL REFERENCES tickets(id),
    verifier_id UUID NOT NULL REFERENCES users(id),
    result VARCHAR(20) NOT NULL,
    notes VARCHAR(500),
    verified_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE bus_locations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bus_id UUID NOT NULL REFERENCES buses(id),
    trip_id UUID REFERENCES trips(id),
    latitude NUMERIC(10,7) NOT NULL,
    longitude NUMERIC(10,7) NOT NULL,
    speed NUMERIC(6,2),
    heading NUMERIC(5,2),
    recorded_at TIMESTAMPTZ NOT NULL,
    stale BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(30) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    "read" BOOLEAN NOT NULL DEFAULT FALSE,
    metadata_json VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    details VARCHAR(2000),
    ip_address VARCHAR(50),
    timestamp TIMESTAMPTZ NOT NULL,
    correlation_id VARCHAR(200),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_buses_bus_number ON buses(bus_number);
CREATE INDEX idx_stops_stop_code ON stops(stop_code);
CREATE INDEX idx_routes_route_number ON routes(route_number);
CREATE INDEX idx_trips_bus_id ON trips(bus_id);
CREATE INDEX idx_trips_route_id ON trips(route_id);
CREATE INDEX idx_trips_scheduled_departure ON trips(scheduled_departure);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_booking_reference ON bookings(booking_reference);
CREATE INDEX idx_bookings_idempotency_key ON bookings(idempotency_key);
CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_idempotency_key ON payments(idempotency_key);
CREATE INDEX idx_tickets_booking_id ON tickets(booking_id);
CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_expires_at ON tickets(expires_at);
CREATE INDEX idx_bus_locations_bus_id ON bus_locations(bus_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
