-- Static demo positions keep the map usable before real GPS devices are integrated.
INSERT INTO bus_locations (id, bus_id, latitude, longitude, speed, heading, recorded_at)
VALUES
  (uuid_generate_v4(), 'd0000000-0000-0000-0000-000000000001', 18.5286000, 73.8750000, 22.00, 180.00, NOW()),
  (uuid_generate_v4(), 'd0000000-0000-0000-0000-000000000003', 18.5362000, 73.8391000, 18.00, 90.00, NOW()),
  (uuid_generate_v4(), 'd0000000-0000-0000-0000-000000000005', 18.5169000, 73.8414000, 15.00, 225.00, NOW());
