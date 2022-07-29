package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import io.spring.application.TagsQueryService;
import io.spring.core.tag.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@DgsComponent
@AllArgsConstructor
public class TagDatafetcher {
    private TagsQueryService tagsQueryService;

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME,field = DgsConstants.QUERY.Tags)
    public List<Tag> getTags(){
        return tagsQueryService.findAllTags();
    }
}
