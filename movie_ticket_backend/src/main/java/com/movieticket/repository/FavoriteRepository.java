package com.movieticket.repository;

import com.movieticket.entity.Favorite;
import com.movieticket.entity.Movie;
import com.movieticket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndMovie(User user, Movie movie);

    List<Favorite> findByUserOrderByCreateTimeDesc(User user);

    Page<Favorite> findByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    boolean existsByUserAndMovie(User user, Movie movie);

    long countByMovie(Movie movie);

    long countByUser(User user);
}