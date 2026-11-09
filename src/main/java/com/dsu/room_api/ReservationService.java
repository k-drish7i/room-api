package com.dsu.room_api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public Reservation get(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    public List<Reservation> findByRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("room " + roomId + " not found");
        }
        return reservationRepository.findByRoomId(roomId);
    }

    public List<Reservation> findByReservedBy(String reservedBy) {
        return reservationRepository.findByReservedBy(reservedBy);
    }

    @Transactional
    public Reservation create(ReservationCreateRequest req) {
        if (!req.startTime().isBefore(req.endTime())) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        Room room = roomRepository.findById(req.roomId())
                .orElseThrow(() -> new NotFoundException("room " + req.roomId() + " not found"));

        List<Reservation> overlaps = reservationRepository.findOverlapping(
                req.roomId(), req.startTime(), req.endTime());
        if (!overlaps.isEmpty()) {
            throw new ConflictException("already booked");
        }

        Reservation res = new Reservation(null, room, req.reservedBy(), req.startTime(), req.endTime());
        return reservationRepository.save(res);
    }

    public void delete(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("reservation " + id + " not found");
        }
        reservationRepository.deleteById(id);
    }
}