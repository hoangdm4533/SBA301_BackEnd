package com.example.demologin.service;


import com.example.demologin.dto.request.matrix.MatrixRequest;
import com.example.demologin.dto.response.matrix.MatrixResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatrixService {
    MatrixResponse createMatrix(MatrixRequest request);
    Page<MatrixResponse> getAllMatrices(Pageable pageable);
    MatrixResponse getMatrixById(Long id);
    MatrixResponse updateMatrix(Long id, MatrixRequest request);
    void deleteMatrix(Long id);
}
