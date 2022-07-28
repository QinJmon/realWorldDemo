package io.spring.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonRootName("user")//在json反序列化为实体类的时候带上实体类的根名字
@AllArgsConstructor
@NoArgsConstructor
public class RegisterParam {

    @NotBlank(message = "can't be empty")//至少包含一个非空字符串
    @Email(message = "should be an email")
    @DuplicatedEmailConstraint //自定义注解验证
    private String email;

    @NotBlank(message = "can't be empty")//至少包含一个非空字符串
    @DuplicatedUsernameConstraint  //自定义注解验证
    private String username;

    @NotBlank(message = "can't be empty")//至少包含一个非空字符串
    private String password;
}
