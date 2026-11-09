//package com.dsu.room_api;
//
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class RoomService {
//
//    private final RoomRepository repository;
//
//    public RoomService(RoomRepository repository) {
//        this.repository = repository;
//    }
//
//    public List<Room> getAllRooms(String keyword) {
//        List<Room> all = repository.findAll();
//        if (keyword == null || keyword.isBlank()) {
//            return all;
//        }
//        return all.stream()
//                .filter(r -> r.getName().toLowerCase().contains(keyword.toLowerCase()))
//                .toList();
//    }
//
//    public Optional<Room> getRoomById(Long id) {
//        return repository.findById(id);
//    }
//
//    public Room createRoom(Room room) {
//        return repository.save(room);
//    }
//
//    public Optional<Room> updateRoom(Long id, Room updatedData) {
//        Optional<Room> existing = repository.findById(id);
//        if (existing.isEmpty()) {
//            return Optional.empty();
//        }
//        updatedData.setId(id);
//        return Optional.of(repository.save(updatedData));
//    }
//
//    public boolean deleteRoom(Long id) {
//        return repository.deleteById(id);
//    }
//}

/// /////////////////////////////////////////////
//
//package com.dsu.room_api;
//
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class RoomService {
//
//    private final RoomRepository repository;
//
//    // Constructor injection - no 'new'
//    public RoomService(RoomRepository repository) {
//        this.repository = repository;
//    }
//
//    public List<Room> getAllRooms(Integer minCapacity, String keyword) {
//        return repository.findAll().stream()
//                .filter(r -> minCapacity == null || r.getCapacity() >= minCapacity)
//                .filter(r -> keyword == null || keyword.isBlank() ||
//                        r.getName().toLowerCase().contains(keyword.toLowerCase()))
//                .toList();
//    }
//
//    public Optional<Room> getRoomById(Long id) {
//        return repository.findById(id);
//    }
//
//    public Room createRoom(Room room) {
//        validateCapacity(room.getCapacity());
//        return repository.save(room);
//    }
//
//    public Optional<Room> updateRoom(Long id, Room updatedData) {
//        validateCapacity(updatedData.getCapacity());
//        Optional<Room> existing = repository.findById(id);
//        if (existing.isEmpty()) {
//            return Optional.empty();
//        }
//        updatedData.setId(id);
//        return Optional.of(repository.save(updatedData));
//    }
//
//    public boolean deleteRoom(Long id) {
//        return repository.deleteById(id);
//    }
//
//    // Capacity must be between 1 and 20
//    private void validateCapacity(Integer capacity) {
//        if (capacity == null || capacity < 1 || capacity > 20) {
//            throw new IllegalArgumentException("Capacity must be between 1 and 20");
//        }
//    }
//}

///////////////////////////////////
package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repository;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public List<Room> search(Integer minCapacity, String keyword) {
        boolean hasMin = (minCapacity != null);
        boolean hasKey = (keyword != null && !keyword.isBlank());

        if (hasMin && hasKey) {
            return repository.findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(minCapacity, keyword);
        } else if (hasMin) {
            return repository.findByCapacityGreaterThanEqual(minCapacity);
        } else if (hasKey) {
            return repository.findByNameContainingIgnoreCase(keyword);
        } else {
            return repository.findAll();
        }
    }

    // Helper method: gets the managed entity or throws NotFoundException (404)
    public Room get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
    }

    public Room create(String name, int capacity) {
        return repository.save(new Room(null, name, capacity));
    }

    // Requirement 2: Dirty checking with @Transactional and NO save() call
    @Transactional
    public Room update(Long id, String name, int capacity) {
        Room room = get(id);
        room.update(name, capacity);
        return room;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("room " + id + " not found");
        }
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }
}