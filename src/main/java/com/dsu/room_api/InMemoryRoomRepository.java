package com.dsu.room_api;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRoomRepository implements RoomRepository {

    private final List<Room> rooms = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public InMemoryRoomRepository() {
        rooms.add(new Room(nextId.getAndIncrement(), "Seminar A", 8));
        rooms.add(new Room(nextId.getAndIncrement(), "Study Pod", 4));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return rooms.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    @Override
    public Room save(Room room) {
        if (room.getId() == null) {
            room.setId(nextId.getAndIncrement());
            rooms.add(room);
        } else {
            deleteById(room.getId());
            rooms.add(room);
        }
        return room;
    }

    @Override
    public boolean deleteById(Long id) {
        return rooms.removeIf(r -> r.getId().equals(id));
    }
}