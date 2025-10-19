package com.movieticket.service;

import com.movieticket.entity.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface HallService {
    Hall createHall(Hall hall);
    Hall updateHall(Hall hall);
    void deleteHall(Long id);
    Optional<Hall> getHallById(Long id);
    List<Hall> getAllActiveHalls();
    Page<Hall> getAllHalls(Pageable pageable);
    Page<Hall> searchHalls(String keyword, Pageable pageable);
    Hall changeHallStatus(Long id, Boolean status);
    boolean existsByName(String name);
}