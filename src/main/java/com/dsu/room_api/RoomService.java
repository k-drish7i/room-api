package com.dsu.room_api;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public List<Room> getAllRooms(String keyword) {
        List<Room> all = repository.findAll();
        if (keyword == null || keyword.isBlank()) {
            return all;
        }
        return all.stream()
                .filter(r -> r.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public Optional<Room> getRoomById(Long id) {
        return repository.findById(id);
    }

    public Room createRoom(Room room) {
        return repository.save(room);
    }

    public Optional<Room> updateRoom(Long id, Room updatedData) {
        Optional<Room> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        updatedData.setId(id);
        return Optional.of(repository.save(updatedData));
    }

    public boolean deleteRoom(Long id) {
        return repository.deleteById(id);
    }
}