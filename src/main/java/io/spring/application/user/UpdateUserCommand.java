package io.spring.application.user;

import io.spring.core.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 更新参数封装：包含要更改的用户和要更新的参数
 */
@Getter
@AllArgsConstructor
@UpdateUserConstraint //自定义验证注解
public class UpdateUserCommand {

    private User targetUser;
    private UpdateUserParam param;
}
