package com.dsu.room_api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    private static boolean isAdmin(Authentication a) {
        return a.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping("/rooms/{roomId}/reservations")
    public List<ReservationResponse> listByRoom(@PathVariable Long roomId) {
        return service.findByRoom(roomId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<ReservationResponse> create(
            @PathVariable Long roomId,
            @Valid @RequestBody ReservationCreateRequest request,
            Authentication authentication) {
        Reservation created = service.create(
                roomId,
                authentication.getName(),
                request.startTime(),
                request.endTime()
        );
        URI location = URI.create("/api/reservations/" + created.getId());
        return ResponseEntity.created(location).body(ReservationResponse.from(created));
    }

    @GetMapping("/reservations/mine")
    public List<ReservationResponse> mine(Authentication authentication) {
        return service.findMine(authentication.getName()).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public ReservationResponse findOne(@PathVariable Long id, Authentication authentication) {
        return ReservationResponse.from(
                service.get(id, authentication.getName(), isAdmin(authentication))
        );
    }

    @PutMapping("/reservations/{id}")
    public ReservationResponse reschedule(
            @PathVariable Long id,
            @Valid @RequestBody ReservationCreateRequest request,
            Authentication authentication) {
        return ReservationResponse.from(
                service.update(id, authentication.getName(), request.startTime(), request.endTime())
        );
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, authentication.getName(), isAdmin(authentication));
    }
}