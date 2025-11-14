package com.example.demologin.repository;

import com.example.demologin.entity.Matrix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatrixRepository extends JpaRepository<Matrix, Long> {
}
