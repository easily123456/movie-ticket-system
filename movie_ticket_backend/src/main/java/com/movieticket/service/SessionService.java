package com.movieticket.service;

import com.movieticket.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionService {
    Session createSession(Session session);
    Session updateSession(Session session);
    void deleteSession(Long id);
    Optional<Session> getSessionById(Long id);
    List<Session> getSessionsByMovie(Long movieId);
    List<Session> getUpcomingSessionsByMovie(Long movieId);
    Page<Session> getAllSessions(Pageable pageable);
    Page<Session> searchSessions(String keyword, Pageable pageable);
    Session changeSessionStatus(Long id, Boolean status);
    boolean checkSessionConflict(Long hallId, LocalDateTime startTime, LocalDateTime endTime);
    void updateBookedSeats(Long sessionId, int seatCount);
    long getUpcomingSessionCount();
}