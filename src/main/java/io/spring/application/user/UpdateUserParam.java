package io.spring.application.user;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

/**
 * 对可能要更新的字段进行封装，同时设置默认值
 */
@Getter
@JsonRootName("user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserParam {
    @Builder.Default
    @Email(message = "should be an email")
    private String email="";
    @Builder.Default
    private String password="";
    @Builder.Default
    private String username="";
    @Builder.Default
    private String bio="";
    @Builder.Default
    private String image="";
}
