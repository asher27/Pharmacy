package com.example.project.direction.repository;

import com.example.project.direction.entiry.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectionRepository extends JpaRepository<Direction, Long> {
}
