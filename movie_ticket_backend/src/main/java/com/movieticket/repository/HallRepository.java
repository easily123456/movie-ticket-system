package com.movieticket.repository;

import com.movieticket.entity.Hall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    Optional<Hall> findByName(String name);

    List<Hall> findByStatusTrue();

    boolean existsByName(String name);

    Page<Hall> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT h FROM Hall h WHERE h.name LIKE %:keyword%")
    Page<Hall> searchHalls(String keyword, Pageable pageable);

    long countByStatus(Boolean status);
}