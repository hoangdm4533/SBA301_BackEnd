package com.example.demologin.utils;

import com.example.demologin.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

    /**
     * Convert Spring Data Page to custom PageResponse DTO
     */
    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
    
    /**
     * Create Pageable with default sorting by ID desc
     */
    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    }
    
    /**
     * Create Pageable with custom sorting
     */
    public static Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
    
    /**
     * Create Pageable with multiple sort fields
     */
    public static Pageable createPageable(int page, int size, Sort sort) {
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * Default page size constant
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * Validate and normalize page size
     */
    public static int normalizePageSize(int size) {
        if (size <= 0) return DEFAULT_PAGE_SIZE;
        return Math.min(size, MAX_PAGE_SIZE);
    }
    
    /**
     * Validate and normalize page number
     */
    public static int normalizePageNumber(int page) {
        return Math.max(page, 0);
    }
}