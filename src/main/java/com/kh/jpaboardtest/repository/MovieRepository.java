package com.kh.jpaboardtest.repository;

import com.kh.jpaboardtest.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}