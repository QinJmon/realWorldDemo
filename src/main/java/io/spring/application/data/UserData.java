package io.spring.application.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 与User相比缺少密码字段，并且哈希充血不一样，全参的构造函数不一样
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String id;
    private String username;
    private String email;
    private String bio;
    private String image;
}
