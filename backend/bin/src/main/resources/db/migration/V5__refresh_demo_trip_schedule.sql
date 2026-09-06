-- Demo schedules must remain bookable when the project is started after its original seed date.
WITH numbered_trips AS (
    SELECT id, row_number() OVER (ORDER BY id) AS position
    FROM trips
)
UPDATE trips t
SET scheduled_departure = NOW() + (n.position * INTERVAL '30 minutes'),
    scheduled_arrival = NOW() + (n.position * INTERVAL '30 minutes') + INTERVAL '50 minutes',
    status = 'SCHEDULED'
FROM numbered_trips n
WHERE t.id = n.id;
