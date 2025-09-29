package com.example.demologin.controller;

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
@RequestMapping("/api/admin/questions")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public Page<QuestionResponse> list(@RequestParam(defaultValue="0") int page,
                                       @RequestParam(defaultValue="20") int size) {
        return questionService.list(page, size);
    }

    @GetMapping("/{id}")
    public QuestionResponse get(@PathVariable Long id) {
        return questionService.get(id);
    }

    @PostMapping
    public QuestionResponse create(@Valid @RequestBody QuestionCreateRequest req) {
        return questionService.create(req);
    }

    @PatchMapping("/{id}") // partial update
    public QuestionResponse update(@PathVariable Long id,
                                   @RequestBody QuestionUpdateRequest req) {
        return questionService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        try {
            questionService.delete(id);
            return ResponseEntity.ok(
                    new ResponseObject(200, "Deleted question successfully", id)
            );
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(404, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(500, "An unexpected error occurred", null));
        }
    }

}
