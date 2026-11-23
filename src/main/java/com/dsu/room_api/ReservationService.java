package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final AppUserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            RoomRepository roomRepository,
            AppUserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    private Reservation find(Long id) {
        return reservationRepository.findDetailById(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    public Reservation get(Long id, String username, boolean admin) {
        Reservation r = find(id);
        if (!admin && !r.isOwnedBy(username)) {
            throw new ForbiddenException("reservation " + id + " belongs to another user");
        }
        return r;
    }

    public List<Reservation> findByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("room " + roomId + " not found");
        }
        return reservationRepository.findByRoomId(roomId);
    }

    public List<Reservation> findMine(String username) {
        return reservationRepository.findMine(username);
    }

    @Transactional
    public Reservation create(Long roomId, String username, LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("room " + roomId + " not found"));

        if (reservationRepository.countOverlapping(roomId, start, end) > 0) {
            throw new ConflictException("already booked");
        }

        AppUser owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user " + username + " not found"));

        return reservationRepository.save(new Reservation(room, owner, start, end));
    }

    // Lab G Task 2: Owner ONLY (not even admin), dirty checking without save()
    @Transactional
    public Reservation update(Long id, String username, LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        Reservation r = find(id);
        if (!r.isOwnedBy(username)) {
            throw new ForbiddenException("reservation " + id + " belongs to another user");
        }

        if (reservationRepository.countOverlappingExcept(r.getRoom().getId(), id, start, end) > 0) {
            throw new ConflictException("already booked");
        }

        r.reschedule(start, end);
        return r;
    }

    @Transactional
    public void delete(Long id, String username, boolean admin) {
        Reservation r = find(id);
        if (!admin && !r.isOwnedBy(username)) {
            throw new ForbiddenException("reservation " + id + " belongs to another user");
        }
        reservationRepository.delete(r);
    }
}