package com.dsu.room_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private AppUserRepository userRepository;
    private ReservationService service;

    private final Room room = new Room(1L, "Seminar A", 8);
    private final AppUser mina = new AppUser("mina", "hash", "USER");
    private final LocalDateTime ten = LocalDateTime.of(2026, 12, 1, 10, 0);
    private final LocalDateTime eleven = LocalDateTime.of(2026, 12, 1, 11, 0);

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(mina, "id", 1L);

        roomRepository = mock(RoomRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        userRepository = mock(AppUserRepository.class);
        service = new ReservationService(reservationRepository, roomRepository, userRepository);
    }

    // --- Steps 3-4: 4 Core Refusal Tests ---

    @Test
    void createRefusesEndBeforeStart() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(1L, "mina", eleven, ten));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createRefusesMissingRoom() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.create(99L, "mina", ten, eleven));
    }

    @Test
    void createRefusesOverlap() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(eq(1L), any(), any())).thenReturn(1L);

        assertThrows(ConflictException.class,
                () -> service.create(1L, "mina", ten, eleven));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void deleteRefusesAnotherUser() {
        Reservation minas = new Reservation(room, mina, ten, eleven);
        when(reservationRepository.findDetailById(1L)).thenReturn(Optional.of(minas));

        assertThrows(ForbiddenException.class,
                () -> service.delete(1L, "omar", false));
        verify(reservationRepository, never()).delete(any());
    }

    // --- Lab H1: 4 Ownership & Rescheduling Tests ---

    @Test
    void createSavesWithTheLoggedInOwner() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.countOverlapping(eq(1L), any(), any())).thenReturn(0L);
        when(userRepository.findByUsername("mina")).thenReturn(Optional.of(mina));
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(call -> call.getArgument(0));

        Reservation created = service.create(1L, "mina", ten, eleven);

        assertEquals(mina, created.getOwner());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void ownerMayDelete() {
        Reservation minas = new Reservation(room, mina, ten, eleven);
        when(reservationRepository.findDetailById(1L)).thenReturn(Optional.of(minas));

        service.delete(1L, "mina", false);

        verify(reservationRepository).delete(minas);
    }

    @Test
    void adminMayDeleteAnotherUsersReservation() {
        Reservation minas = new Reservation(room, mina, ten, eleven);
        when(reservationRepository.findDetailById(1L)).thenReturn(Optional.of(minas));

        service.delete(1L, "admin", true);

        verify(reservationRepository).delete(minas);
    }

    @Test
    void adminMayNotReschedule() {
        Reservation minas = new Reservation(room, mina, ten, eleven);
        when(reservationRepository.findDetailById(1L)).thenReturn(Optional.of(minas));

        LocalDateTime noon = LocalDateTime.of(2026, 12, 1, 12, 0);
        LocalDateTime onePm = LocalDateTime.of(2026, 12, 1, 13, 0);

        assertThrows(ForbiddenException.class,
                () -> service.update(1L, "admin", noon, onePm));
    }
}