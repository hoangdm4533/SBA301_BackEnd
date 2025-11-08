package com.example.demologin.controller;

import com.example.demologin.dto.request.transaction.TransactionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.TransactionResponse;
import com.example.demologin.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "CRUD API for Transaction management")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Create new transaction")
    @PostMapping
    public TransactionResponse create(@RequestBody TransactionRequest request) {
        return transactionService.create(request);
    }

    @Operation(summary = "Update transaction by ID")
    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable Long id, @RequestBody TransactionRequest request) {
        return transactionService.update(id, request);
    }

    @Operation(summary = "Delete transaction by ID")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        transactionService.delete(id);
    }

    @Operation(summary = "Get transaction by ID")
    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @Operation(summary = "Get paginated transactions")
    @GetMapping("")
    public PageResponse<TransactionResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionService.getAllPaged(pageable);
    }
}
