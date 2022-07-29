package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.exception.ResourceNotFoundException;
import io.spring.graphql.types.Profile;
import io.spring.graphql.types.ProfilePayload;
import lombok.AllArgsConstructor;
import java.util.Optional;

@DgsComponent
@AllArgsConstructor
public class RelationMutation {

    private UserRepository userRepository;
    private ProfileQueryService profileQueryService;

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,field = DgsConstants.MUTATION.FollowUser)
    public ProfilePayload follow(@InputArgument("username")String username){
        //先获取当前登陆的用户A
        User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);

        //再根据username查找用户B
        return userRepository.findByUsername(username)
                .map(target ->{
                    //将当前登陆用户和目标用户封装起来
                    FollowRelation followRelation=new FollowRelation(user.getId(),target.getId());
                    //保存两者的关系（当两者原本没有关系的时候才会进行操作）
                    userRepository.saveRelation(followRelation);
                    //封装方法： 根据用户名和当前用户得到目标用户名称为username的描述（描述里面有follow）
                    Profile profile=buildProfile(username,user);
                    return ProfilePayload.newBuilder().profile(profile).build();
                }).orElseThrow(ResourceNotFoundException::new);//当没有找到名称为username的用户的时候抛出资源为找到异常


    }
    //为什么参数中还要加@InputArgument???
    private Profile buildProfile(@InputArgument("username") String username, User current) {
        //根据目标用户的username和当前用户得到profileData（主要是找两者的联系，即relation）
        ProfileData profileData = profileQueryService.findByUsername(username, current).get();
        //根据profileData构建Profile
        return Profile.newBuilder()
                .username(username)
                .image(profileData.getImage())
                .bio(profileData.getBio())
                .following(profileData.getFollowing())
                .build();
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,field = DgsConstants.MUTATION.UnfollowUser)
    public ProfilePayload unfollow(@InputArgument("username")String username){
        //得到当前登陆用户A(写在一起省的再做校验)
        User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);

        //根据用户名称得到目标用户B
        return userRepository.findByUsername(username)
                .map(target->{
                    //将当前登陆用户和目标用户封装起来
                    FollowRelation followRelation=new FollowRelation(user.getId(),target.getId());
                    //保存两者的关系（当两者原本没有关系的时候才会进行操作）
                    userRepository.removeRelation(followRelation);
                    //封装方法： 根据用户名和当前用户得到目标用户名称为username的描述（描述里面有follow）
                    Profile profile=buildProfile(username,user);
                    return ProfilePayload.newBuilder().profile(profile).build();

                })
                .orElseThrow(ResourceNotFoundException::new);
    }
}
