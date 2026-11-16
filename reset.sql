USE roomdb;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reservation;
TRUNCATE TABLE room;
DROP TABLE IF EXISTS app_user;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uq_app_user_username UNIQUE (username)
);

INSERT INTO room (name, capacity) VALUES ('Seminar A', 8);
INSERT INTO room (name, capacity) VALUES ('Study Pod', 4);
INSERT INTO room (name, capacity) VALUES ('Rooftop Room', 12);

INSERT INTO reservation (room_id, reserved_by, start_time, end_time)
VALUES (1, 'Mina', '2026-10-06 10:00:00', '2026-10-06 11:00:00');

INSERT INTO reservation (room_id, reserved_by, start_time, end_time)
VALUES (1, 'Omar', '2026-10-06 13:00:00', '2026-10-06 15:00:00');

INSERT INTO reservation (room_id, reserved_by, start_time, end_time)
VALUES (2, 'Mina', '2026-10-06 09:00:00', '2026-10-06 10:00:00');

INSERT INTO reservation (room_id, reserved_by, start_time, end_time)
VALUES (1, 'Lucas', '2026-10-07 10:00:00', '2026-10-07 12:00:00');

INSERT INTO reservation (room_id, reserved_by, start_time, end_time)
VALUES (2, 'Aiko', '2026-10-07 14:00:00', '2026-10-07 15:00:00');

-- Sample accounts (passwords: mina1234, omar1234, admin1234)
INSERT INTO app_user (username, password_hash, role) VALUES
('mina', '$2a$10$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'USER'),
('omar', '$2a$10$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'USER'),
('admin', '$2a$10$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'ADMIN');