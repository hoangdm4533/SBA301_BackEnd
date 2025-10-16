package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    @SecuredEndpoint("QUESTION_VIEW")
    public ResponseEntity<ResponseObject> list(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        Page<QuestionResponse> data = questionService.list(page, size);
        return ResponseEntity.ok(new ResponseObject(200, "Questions retrieved successfully", data));
    }

    @GetMapping("/{id}")
    @SecuredEndpoint("QUESTION_VIEW")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        QuestionResponse data = questionService.get(id);
        return ResponseEntity.ok(new ResponseObject(200, "Question retrieved successfully", data));
    }

    @PostMapping
    @SecuredEndpoint("QUESTION_CREATE")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody QuestionCreateRequest req) {
        QuestionResponse data = questionService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Question created successfully", data));
    }

    @PatchMapping("/{id}") // partial update
    @SecuredEndpoint("QUESTION_UPDATE")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id,
                                                 @RequestBody QuestionUpdateRequest req) {
        QuestionResponse data = questionService.update(id, req);
        return ResponseEntity.ok(new ResponseObject(200, "Question updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @   SecuredEndpoint("QUESTION_DELETE")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        questionService.delete(id);
        return ResponseEntity.ok(new ResponseObject(200, "Question deleted successfully", id));
    }

}
