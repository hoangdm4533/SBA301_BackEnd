package com.example.demologin.service;

import com.example.demologin.dto.request.transaction.TransactionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.TransactionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    TransactionResponse update(Long id, TransactionRequest request);
    void delete(Long id);
    TransactionResponse getById(Long id);
    List<TransactionResponse> getAll();
    PageResponse<TransactionResponse> getAllPaged(Pageable pageable);
}
