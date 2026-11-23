USE roomdb;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS app_user;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uq_app_user_username UNIQUE (username)
);

CREATE TABLE room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    CONSTRAINT chk_room_capacity CHECK (capacity >= 1 AND capacity <= 20)
);

CREATE TABLE reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_reservation_owner FOREIGN KEY (owner_id) REFERENCES app_user(id)
);

-- Sample accounts (passwords: mina1234, omar1234, admin1234)
INSERT INTO app_user (username, password_hash, role) VALUES
('mina', '\$2a\$10\$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'USER'),
('omar', '\$2a\$10\$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'USER'),
('admin', '\$2a\$10\$ix/.xals1cT5fBy84xYGd./rBgU.YjsOFZ0hS649iIxeUqTiiOLzK', 'ADMIN');

INSERT INTO room (name, capacity) VALUES ('Seminar A', 8);
INSERT INTO room (name, capacity) VALUES ('Study Pod', 4);
INSERT INTO room (name, capacity) VALUES ('Rooftop Room', 12);

-- Sample reservations (mina = user 1, omar = user 2)
INSERT INTO reservation (room_id, owner_id, start_time, end_time)
VALUES (1, 1, '2026-10-06 10:00:00', '2026-10-06 11:00:00');

INSERT INTO reservation (room_id, owner_id, start_time, end_time)
VALUES (1, 2, '2026-10-06 13:00:00', '2026-10-06 15:00:00');

INSERT INTO reservation (room_id, owner_id, start_time, end_time)
VALUES (2, 1, '2026-10-06 09:00:00', '2026-10-06 10:00:00');

INSERT INTO reservation (room_id, owner_id, start_time, end_time)
VALUES (1, 2, '2026-10-07 10:00:00', '2026-10-07 12:00:00');

INSERT INTO reservation (room_id, owner_id, start_time, end_time)
VALUES (2, 2, '2026-10-07 14:00:00', '2026-10-07 15:00:00');