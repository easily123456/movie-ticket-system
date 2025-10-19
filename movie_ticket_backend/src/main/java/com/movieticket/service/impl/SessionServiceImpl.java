package com.movieticket.service.impl;

import com.movieticket.entity.Session;
import com.movieticket.entity.Movie;
import com.movieticket.entity.Hall;
import com.movieticket.repository.SessionRepository;
import com.movieticket.service.SessionService;
import com.movieticket.service.MovieService;
import com.movieticket.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final MovieService movieService;
    private final HallService hallService;

    @Override
    public Session createSession(Session session) {
        // 验证电影和放映厅存在
        Movie movie = movieService.getMovieById(session.getMovie().getId())
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        Hall hall = hallService.getHallById(session.getHall().getId())
                .orElseThrow(() -> new RuntimeException("放映厅不存在"));

        // 检查时间冲突
        if (checkSessionConflict(hall.getId(), session.getStartTime(), session.getEndTime())) {
            throw new RuntimeException("该放映厅在该时间段已有排片");
        }

        session.setMovie(movie);
        session.setHall(hall);
        session.setAvailableSeats(hall.getCapacity());
        session.setBookedSeats(0);
        session.setStatus(true);

        return sessionRepository.save(session);
    }

    @Override
    public Session updateSession(Session session) {
        Session existingSession = sessionRepository.findById(session.getId())
                .orElseThrow(() -> new RuntimeException("场次不存在"));

        // 验证电影和放映厅存在
        Movie movie = movieService.getMovieById(session.getMovie().getId())
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        Hall hall = hallService.getHallById(session.getHall().getId())
                .orElseThrow(() -> new RuntimeException("放映厅不存在"));

        // 检查时间冲突（排除自身）
        List<Session> conflictingSessions = sessionRepository.findConflictingSessions(
                hall.getId(), session.getStartTime(), session.getEndTime());
        boolean hasConflict = conflictingSessions.stream()
                .anyMatch(s -> !s.getId().equals(session.getId()));

        if (hasConflict) {
            throw new RuntimeException("该放映厅在该时间段已有排片");
        }

        existingSession.setMovie(movie);
        existingSession.setHall(hall);
        existingSession.setStartTime(session.getStartTime());
        existingSession.setEndTime(session.getEndTime());
        existingSession.setPrice(session.getPrice());

        return sessionRepository.save(existingSession);
    }

    @Override
    public void deleteSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("场次不存在"));
        sessionRepository.delete(session);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Session> getSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> getSessionsByMovie(Long movieId) {
        Movie movie = movieService.getMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        return sessionRepository.findByMovieAndStatusTrueOrderByStartTime(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Session> getUpcomingSessionsByMovie(Long movieId) {
        return sessionRepository.findUpcomingSessionsByMovie(movieId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Session> getAllSessions(Pageable pageable) {
        return sessionRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Session> searchSessions(String keyword, Pageable pageable) {
        return sessionRepository.searchSessions(keyword, pageable);
    }

    @Override
    public Session changeSessionStatus(Long id, Boolean status) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("场次不存在"));
        session.setStatus(status);
        return sessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkSessionConflict(Long hallId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Session> conflictingSessions = sessionRepository.findConflictingSessions(
                hallId, startTime, endTime);
        return !conflictingSessions.isEmpty();
    }

    @Override
    public void updateBookedSeats(Long sessionId, int seatCount) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("场次不存在"));

        int newBookedSeats = session.getBookedSeats() + seatCount;
        if (newBookedSeats > session.getAvailableSeats()) {
            throw new RuntimeException("座位数量不足");
        }

        session.setBookedSeats(newBookedSeats);
        sessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUpcomingSessionCount() {
        return sessionRepository.countUpcomingSessions(LocalDateTime.now());
    }
}