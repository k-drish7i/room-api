package com.dsu.room_api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.room JOIN FETCH r.owner WHERE r.room.id = :roomId")
    List<Reservation> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.room JOIN FETCH r.owner WHERE r.owner.username = :username")
    List<Reservation> findMine(@Param("username") String username);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.room JOIN FETCH r.owner WHERE r.id = :id")
    Optional<Reservation> findDetailById(@Param("id") Long id);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId " +
            "AND r.startTime < :endTime AND r.endTime > :startTime")
    long countOverlapping(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Lab G Task 1: Exclude the reservation being moved so it never overlaps itself
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId AND r.id <> :id " +
            "AND r.startTime < :endTime AND r.endTime > :startTime")
    long countOverlappingExcept(
            @Param("roomId") Long roomId,
            @Param("id") Long id,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    boolean existsByRoomId(Long roomId);
}