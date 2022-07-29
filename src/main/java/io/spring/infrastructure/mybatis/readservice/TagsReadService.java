package io.spring.infrastructure.mybatis.readservice;


import io.spring.core.tag.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagsReadService {

    List<Tag> findAllTags();
}
