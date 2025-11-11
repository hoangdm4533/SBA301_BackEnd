package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.ai.QuestionGenerate;
import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @PageResponse
    @ApiResponse(message = "Questions retrieved successfully")
    public ResponseEntity<ResponseObject> list(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final Page<QuestionResponse> data = questionService.list(page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Questions retrieved successfully",
                data
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Question retrieved successfully")
    public ResponseEntity<ResponseObject> get(@PathVariable final Long id) {
        final QuestionResponse data = questionService.get(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question retrieved successfully",
                data
        ));
    }

    @PostMapping
    @ApiResponse(message = "Question created successfully")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody final QuestionCreateRequest req) {
        final QuestionResponse data = questionService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Question created successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Question updated successfully")
    public ResponseEntity<ResponseObject> update(
            @PathVariable final Long id,
            @Valid @RequestBody final QuestionUpdateRequest req
    ) {
        final QuestionResponse data = questionService.update(id, req);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Question deleted successfully")
    public ResponseEntity<ResponseObject> delete(@PathVariable final Long id) {
        questionService.delete(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question deleted successfully",
                id
        ));
    }

    @PostMapping("/generate")
    @ApiResponse(message = "Question generated successfully")
    public ResponseEntity<ResponseObject> generate(@RequestBody QuestionGenerate request) {
        String result = questionService.generateQuestion(request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question generated successfully",
                result
        ));
    }

    @GetMapping("/by-level/{levelId}")
    @PageResponse
    @ApiResponse(message = "Questions retrieved by level successfully")
    public ResponseEntity<ResponseObject> listByLevel(
            @PathVariable final Long levelId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final Page<QuestionResponse> data = questionService.listByLevel(levelId, page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Questions retrieved by level successfully",
                data
        ));
    }

    @GetMapping("/by-type/{typeId}")
    @PageResponse
    @ApiResponse(message = "Questions retrieved by type successfully")
    public ResponseEntity<ResponseObject> listByType(
            @PathVariable final Long typeId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final Page<QuestionResponse> data = questionService.listByType(typeId, page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Questions retrieved by type successfully",
                data
        ));
    }

    @GetMapping("/by-matrix/{matrixId}")
    @PageResponse
    @ApiResponse(message = "Questions retrieved by matrix successfully")
    public ResponseEntity<ResponseObject> listByMatrix(
            @PathVariable final Long matrixId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final Page<QuestionResponse> data = questionService.listByMatrix(matrixId, page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Questions retrieved by matrix successfully",
                data
        ));
    }
}
