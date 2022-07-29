package io.spring.core.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关注相关类,当前登陆用户id和要关注的用户id
 */
@Data
@NoArgsConstructor
public class FollowRelation {
    private String userId;
    private String targetId;

    public FollowRelation(String userId, String targetId) {
        this.userId = userId;
        this.targetId = targetId;
    }
}
