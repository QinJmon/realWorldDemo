package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insert(@Param("user")User user);

    User findByEmail(@Param("email") String email);

    User findById(@Param("id") String id);

    //在保存用户的时候，根据id查找，如果存在了就update，不存在就save
    void update(@Param(("user")) User user);

    User findByUsername(@Param("username") String username);

    FollowRelation findByRelation(@Param("userId") String userId,@Param("targetId") String targetId);

    void saveRelation(@Param("followRelation") FollowRelation followRelation);

    void deleteRelation(@Param("followRelation") FollowRelation followRelation);
}
