package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.exception.ResourceNotFoundException;
import io.spring.graphql.types.Profile;
import io.spring.graphql.types.ProfilePayload;
import lombok.AllArgsConstructor;


@DgsComponent
@AllArgsConstructor
public class ProfileDatafetcher {

    private ProfileQueryService profileQueryService;

    @DgsData(parentType = DgsConstants.USER.TYPE_NAME,field = DgsConstants.USER.Profile)
    public Profile getUserProfile(DataFetchingEnvironment dataFetchingEnvironment){
        //从应用程序上下文当中获得user
        User user = dataFetchingEnvironment.getLocalContext();
        //得到用户的用户名
        String username = user.getUsername();
        //根据用户名查找profile
        return queryProfile(username);
    }

    @DgsData(parentType = DgsConstants.QUERY.TYPE_NAME,field = DgsConstants.QUERY.Profile)
    public ProfilePayload queryProfile(@InputArgument("username")String username,
            DataFetchingEnvironment dataFetchingEnvironment){
        //获得返回命名的参数,不能直接用username吗？还要再获取参数，看follow
        Profile profile = queryProfile(dataFetchingEnvironment.getArgument("username"));
        return ProfilePayload.newBuilder().profile(profile).build();
    }

    private Profile queryProfile(String username) {
        //通过工具类得到当前用户
       User current= SecurityUtil.getCurrentUser().orElse(null);
        //将根据用户名和当前用户查询得到的形象描述封装为data
        ProfileData profileData=profileQueryService.findByUsername(username,current).orElseThrow(ResourceNotFoundException::new);
        //返回结果
        return Profile.newBuilder()
                .username(profileData.getUsername())
                .bio(profileData.getBio())
                .image(profileData.getImage())
                .following(profileData.getFollowing())
                .build();
    }
}
