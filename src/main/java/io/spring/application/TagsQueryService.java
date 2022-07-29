package io.spring.application;


import io.spring.core.tag.Tag;
import io.spring.infrastructure.mybatis.readservice.TagsReadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagsQueryService {
    private TagsReadService tagsReadService;


    public List<Tag> findAllTags() {
        return tagsReadService.findAllTags();
    }
}
