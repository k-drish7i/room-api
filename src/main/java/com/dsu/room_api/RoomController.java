//package com.dsu.room_api;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicLong;
//
//@RestController
//@RequestMapping("/api/rooms")
//public class RoomController {
//
//    // In-memory list to store rooms (Week 2/3 style)
//    private final List<Room> rooms = new ArrayList<>();
//    private final AtomicLong nextId = new AtomicLong(1);
//
//    public RoomController() {
//        // Seed sample data as shown in slides (e.g. Seminar A, Study Pod)
//        rooms.add(new Room(nextId.getAndIncrement(), "Seminar A", 8));
//        rooms.add(new Room(nextId.getAndIncrement(), "Study Pod", 4));
//    }
//
////    // 1. GET /api/rooms -> 200 OK
////    @GetMapping
////    public ResponseEntity<List<Room>> getAllRooms() {
////        return ResponseEntity.ok(rooms);
////    }
//
//    @GetMapping
//    public ResponseEntity<List<Room>> getAllRooms(@RequestParam(required = false) String keyword) {
//        if (keyword == null || keyword.isEmpty()) {
//            return ResponseEntity.ok(rooms);
//        }
//
//        List<Room> matchingRooms = new ArrayList<>();
//        for (Room room : rooms) {
//            if (room.getName().toLowerCase().contains(keyword.toLowerCase())) {
//                matchingRooms.add(room);
//            }
//        }
//        return ResponseEntity.ok(matchingRooms);
//    }
//
//    // 2. GET /api/rooms/{id} -> 200 OK or 404 Not Found
//    @GetMapping("/{id}")
//    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
//        for (Room room : rooms) {
//            if (room.getId().equals(id)) {
//                return ResponseEntity.ok(room);
//            }
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    // 3. POST /api/rooms -> 201 Created + Location header
//    @PostMapping
//    public ResponseEntity<Room> createRoom(@RequestBody Room newRoom) {
//        Long generatedId = nextId.getAndIncrement();
//        newRoom.setId(generatedId);
//        rooms.add(newRoom);
//
//        URI location = URI.create("/api/rooms/" + generatedId);
//        return ResponseEntity.created(location).body(newRoom);
//    }
//
//    // 4. PUT /api/rooms/{id} -> 200 OK or 404 Not Found
//    @PutMapping("/{id}")
//    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room updatedData) {
//        for (Room room : rooms) {
//            if (room.getId().equals(id)) {
//                room.setName(updatedData.getName());
//                room.setCapacity(updatedData.getCapacity());
//                return ResponseEntity.ok(room);
//            }
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    // 5. DELETE /api/rooms/{id} -> 204 No Content or 404 Not Found
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
//        boolean removed = rooms.removeIf(room -> room.getId().equals(id));
//        if (removed) {
//            return ResponseEntity.noContent().build(); // 204
//        }
//        return ResponseEntity.notFound().build(); // 404
//    }
//}

package com.dsu.room_api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService service;

    // Constructor injection
    public RoomController(RoomService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(service.getAllRooms(minCapacity, keyword));
    }

    @Operation(summary = "Find a room by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return service.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new room")
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room newRoom) {
        try {
            Room created = service.createRoom(newRoom);
            URI location = URI.create("/api/rooms/" + created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody Room updatedData) {
        try {
            return service.updateRoom(id, updatedData)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (service.deleteRoom(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
