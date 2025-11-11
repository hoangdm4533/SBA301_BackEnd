package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.transaction.TransactionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.TransactionResponse;
import com.example.demologin.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "CRUD API for Transaction management")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ApiResponse(message = "Transaction created successfully")
    @Operation(summary = "Create new transaction", description = "Create a new transaction (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> create(@RequestBody TransactionRequest request) {
        final TransactionResponse data = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Transaction created successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Transaction updated successfully")
    @Operation(summary = "Update transaction by ID", description = "Update transaction details (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody TransactionRequest request) {
        final TransactionResponse data = transactionService.update(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Transaction updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Transaction deleted successfully")
    @Operation(summary = "Delete transaction by ID", description = "Delete a transaction (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Transaction deleted successfully",
                id
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Transaction retrieved successfully")
    @Operation(summary = "Get transaction by ID", description = "Retrieve transaction details")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        final TransactionResponse data = transactionService.getById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Transaction retrieved successfully",
                data
        ));
    }

    @GetMapping
    @ApiResponse(message = "Transactions retrieved successfully")
    @PageResponse
    @Operation(summary = "Get paginated transactions", description = "Retrieve all transactions with pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        final com.example.demologin.dto.response.PageResponse<TransactionResponse> data =
                transactionService.getAllPaged(pageable);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Transactions retrieved successfully",
                data
        ));
    }
}
