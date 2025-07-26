package com.example.demologin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("pageNumber")
    private int pageNumber;

    @JsonProperty("pageSize")
    private int pageSize;

    @JsonProperty("totalElements")
    private long totalElements;

    @JsonProperty("totalPages")
    private int totalPages;

    @JsonProperty("last")
    private boolean last;
}

