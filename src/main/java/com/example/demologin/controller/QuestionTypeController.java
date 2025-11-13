package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.questiontype.QuestionTypeCreateRequest;
import com.example.demologin.dto.request.questiontype.QuestionTypeUpdateRequest;
import com.example.demologin.dto.response.QuestionTypeResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.QuestionTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-types")
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class QuestionTypeController {

    private final QuestionTypeService questionTypeService;

    @GetMapping
    @PageResponse
    @ApiResponse(message = "Question types retrieved successfully")
    public ResponseEntity<ResponseObject> list(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final Page<QuestionTypeResponse> data = questionTypeService.list(page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question types retrieved successfully",
                data
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Question type retrieved successfully")
    public ResponseEntity<ResponseObject> get(@PathVariable final Long id) {
        final QuestionTypeResponse data = questionTypeService.get(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question type retrieved successfully",
                data
        ));
    }

    @GetMapping("description")
    @ApiResponse(message = "Question type retrieved with description successfully")
    public ResponseEntity<ResponseObject> get(@RequestParam("description") String description) {
        var data = questionTypeService.findByDescription(description);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question type retrieved successfully",
                data
        ));
    }

    @PostMapping
    @ApiResponse(message = "Question type created successfully")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody final QuestionTypeCreateRequest req) {
        final QuestionTypeResponse data = questionTypeService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Question type created successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Question type updated successfully")
    public ResponseEntity<ResponseObject> update(
            @PathVariable final Long id,
            @Valid @RequestBody final QuestionTypeUpdateRequest req
    ) {
        final QuestionTypeResponse data = questionTypeService.update(id, req);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question type updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Question type deleted successfully")
    public ResponseEntity<ResponseObject> delete(@PathVariable final Long id) {
        questionTypeService.delete(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question type deleted successfully",
                id
        ));
    }
}

