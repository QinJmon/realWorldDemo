package io.spring.infrastructure.service;



import io.spring.core.tag.Tag;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.mybatis.readservice.TagsReadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TagsQueryServiceTest extends DbTestBase {

    @Autowired
    private TagsReadService tagsReadService;

    @Test
    void testFindAllTags() {
        List<Tag> tags = tagsReadService.findAllTags();
        for (Tag tag : tags) {
            System.out.println(tag);
        }
    }
}
