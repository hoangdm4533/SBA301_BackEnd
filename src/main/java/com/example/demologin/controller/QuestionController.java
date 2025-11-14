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
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class QuestionController {

    private final QuestionService questionService;
    private final ObjectMapper objectMapper;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(message = "Question created successfully")
    public ResponseEntity<ResponseObject> create(
            @RequestPart("data") final String dataJson,
            @RequestPart(value = "image", required = false) final MultipartFile imageFile
    ) throws IOException {
        QuestionCreateRequest req = objectMapper.readValue(dataJson, QuestionCreateRequest.class);
        final QuestionResponse data = questionService.create(req, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Question created successfully",
                data
        ));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(message = "Question updated successfully")
    public ResponseEntity<ResponseObject> update(
            @PathVariable final Long id,
            @RequestPart("data") final String dataJson,
            @RequestPart(value = "image", required = false) final MultipartFile imageFile
    ) throws IOException {
        QuestionUpdateRequest req = objectMapper.readValue(dataJson, QuestionUpdateRequest.class);
        final QuestionResponse data = questionService.update(id, req, imageFile);
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
    public CompletableFuture<ResponseEntity<ResponseObject>> generate(@RequestBody QuestionGenerate request) {
        return questionService.generateQuestion(request) // phương thức async
                .thenApply(result -> ResponseEntity.ok(
                        new ResponseObject(
                                HttpStatus.OK.value(),
                                "Question generated successfully",
                                result
                        )
                ))
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                        new ResponseObject(
                                HttpStatus.SERVICE_UNAVAILABLE.value(),
                                "Hệ thống AI đang quá tải, vui lòng thử lại sau.",
                                ex.getMessage()
                        )
                ));
    }

    @PostMapping("/ai")
    @ApiResponse(message = "Question saved successfully")
    public ResponseEntity<ResponseObject> saveQuestionAi(@RequestBody List<QuestionCreateRequest> request) {
        var result = questionService.saveQuestionsAI(request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question saved successfully",
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

    @GetMapping("/by-status/{status}")
    @PageResponse
    @ApiResponse(message = "Questions retrieved by status successfully")
    public ResponseEntity<ResponseObject> listByStatus(
            @PathVariable final String status,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final Page<QuestionResponse> data = questionService.listByStatus(status, page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Questions retrieved by status successfully",
                data
        ));
    }

    @PatchMapping("/{id}/status")
    @ApiResponse(message = "Question status changed successfully")
    public ResponseEntity<ResponseObject> changeStatus(@PathVariable final Long id) {
        final QuestionResponse data = questionService.changeStatus(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question status changed successfully",
                data
        ));
    }
}
