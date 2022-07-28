package io.spring.application;

import io.spring.application.data.ProfileData;
import io.spring.application.data.UserData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ProfileQueryService {

    private UserReadService userReadService;
    private UserRelationshipQueryService userRelationshipQueryService;

    public Optional<ProfileData> findByUsername(String username, User currentUser) {
        //先根据用户名得到用户data
        UserData userData=userReadService.findByUsername(username);
        //判断
        if(userData==null){
            return Optional.empty();
        }else{
            //对形象描述进行封装
            ProfileData profileData=new ProfileData(userData.getId(),
                    userData.getUsername(),
                    userData.getBio(),
                    currentUser!=null && userRelationshipQueryService.isUserFollowing(
                            currentUser.getId(),userData.getId()
                    ),
                    userData.getImage());
            //返回结果
            return Optional.of(profileData);
        }

    }
}
