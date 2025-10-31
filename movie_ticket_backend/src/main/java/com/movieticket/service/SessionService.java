package com.movieticket.service;

import com.movieticket.dto.response.session.SessionDetailResponse;
import com.movieticket.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    Page<Session> searchSessionsByMovie(String movieKeyword, Pageable pageable);
    Page<Session> getSessionsByHall(Long hallId, Pageable pageable);
    Page<Session> getSessionsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Session> getSessionsByStatus(Boolean status, Pageable pageable);
    void batchDeleteSessions(List<Long> sessionIds);
    void batchChangeSessionStatus(List<Long> sessionIds, boolean status);

    SessionDetailResponse getSessionDetail(Long sessionId);
    List<String> getBookedSeatsForSession(Long sessionId);
    List<String> getLockedSeatsForSession(Long sessionId);
    boolean checkSeatAvailability(Long sessionId, List<String> seatNumbers);
    void updateSessionSeats(Long sessionId, int seatCount, boolean isBooking);
}