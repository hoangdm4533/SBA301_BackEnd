package com.example.demologin.mapper.chapter;

import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.entity.Chapter;

public interface IChapterMapper {
    ChapterResponse toResponse(Chapter chapter);
}
