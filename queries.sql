-- Q1. Rooms for 6 or more people, largest first. (Expected: 2 rows)
SELECT * FROM room
WHERE capacity >= 6
ORDER BY capacity DESC;

-- Q2. Every reservation made by Mina. (Expected: 2 rows)
SELECT * FROM reservation
WHERE reserved_by = 'Mina';

-- Q3. Every reservation with the name of its room. (Expected: 5 rows)
SELECT r.id, r.reserved_by, r.start_time, r.end_time, rm.name AS room_name
FROM reservation r
JOIN room rm ON r.room_id = rm.id;

-- Q4. Reservations for Seminar A on 6 October 2026. (Expected: 2 rows)
SELECT r.*
FROM reservation r
JOIN room rm ON r.room_id = rm.id
WHERE rm.name = 'Seminar A'
  AND r.start_time >= '2026-10-06 00:00:00'
  AND r.start_time < '2026-10-07 00:00:00';

-- Q5. Number of reservations per room (JOIN). (Expected: 2 rows)
SELECT rm.id, rm.name, COUNT(r.id) AS reservation_count
FROM room rm
JOIN reservation r ON rm.id = r.room_id
GROUP BY rm.id, rm.name;

-- Q6. Same as Q5, but show 0 for rooms with no reservations. (Expected: 3 rows)
SELECT rm.id, rm.name, COUNT(r.id) AS reservation_count
FROM room rm
LEFT JOIN reservation r ON rm.id = r.room_id
GROUP BY rm.id, rm.name;

-- Q7. Rooms that have never been reserved. (Expected: 1 row)
SELECT rm.*
FROM room rm
LEFT JOIN reservation r ON rm.id = r.room_id
WHERE r.id IS NULL;

-- Q8. Rooms with more than two reservations. (Expected: 1 row)
SELECT rm.id, rm.name, COUNT(r.id) AS reservation_count
FROM room rm
JOIN reservation r ON rm.id = r.room_id
GROUP BY rm.id, rm.name
HAVING COUNT(r.id) > 2;

-- Challenge. Which reservations in room 1 overlap 10:30–11:30 on 6 October 2026?
SELECT * FROM reservation
WHERE room_id = 1
  AND start_time < '2026-10-06 11:30:00'
  AND end_time > '2026-10-06 10:30:00';