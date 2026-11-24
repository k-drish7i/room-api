package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository repository, ReservationRepository reservationRepository) {
        this.repository = repository;
        this.reservationRepository = reservationRepository;
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

    public Room get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
    }

    public Room create(String name, int capacity) {
        return repository.save(new Room(null, name, capacity));
    }

    @Transactional
    public Room update(Long id, String name, int capacity) {
        Room room = get(id);
        room.update(name, capacity);
        return room;
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("room " + id + " not found");
        }
        if (reservationRepository.existsByRoomId(id)) {
            throw new ConflictException("room " + id + " has reservations");
        }
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }
}