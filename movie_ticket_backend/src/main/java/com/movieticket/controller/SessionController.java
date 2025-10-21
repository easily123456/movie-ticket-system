package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.response.session.SessionResponse;
import com.movieticket.entity.Session;
import com.movieticket.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessionsByMovie(@PathVariable Long movieId) {
        List<Session> sessions = sessionService.getUpcomingSessionsByMovie(movieId);
        List<SessionResponse> response = sessions.stream()
                .map(this::convertToSessionResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getSessionsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)表示转换为yyyy-MM-dd，注入到参数date中
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionResponse>> getSession(@PathVariable Long id) {
        Optional<Session> sessionOpt = sessionService.getSessionById(id);
        if (sessionOpt.isPresent()) {
            SessionResponse response = convertToSessionResponse(sessionOpt.get());
            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("场次不存在"));
        }
    }

    private SessionResponse convertToSessionResponse(Session session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setMovieId(session.getMovie().getId());
        response.setMovieTitle(session.getMovie().getTitle());
        response.setHallId(session.getHall().getId());
        response.setHallName(session.getHall().getName());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setPrice(session.getPrice());
        response.setAvailableSeats(session.getAvailableSeats());
        response.setBookedSeats(session.getBookedSeats());
        response.setStatus(session.getStatus());
        return response;
    }
}