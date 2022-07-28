package io.spring.application.user;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated//验证注解在类上使用
public class UserService {
    private UserRepository userRepository;
    private String defaultImage;
    //用于编码密码的服务接口
    private PasswordEncoder passwordEncoder;

    /**
     * 在创建生产应用程序上下文时，Spring 将自动使用此构造函数来实例化 RegisterUseCase 对象。
     * 注意，在 Spring 5 之前，我们需要在构造函数中添加 @Autowired 注解，以便 Spring 找到构造函数。
     */
    @Autowired
    public UserService(UserRepository userRepository,
                        @Value("${image.default}")String defaultImage,
                        PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.defaultImage=defaultImage;
        this.passwordEncoder=passwordEncoder;
    }

    //对注册的参数需要进行校验
    public User createUser(@Valid RegisterParam registerParam){
        User user=new User(registerParam.getUsername(),
                //对原始密码进行编码。一般来说，一个好的编码算法是应用SHA-1或更大的哈希值与一个8字节或更大的随机生成的盐相结合。
                passwordEncoder.encode(registerParam.getPassword()),
                registerParam.getEmail(),
                "",
                defaultImage);
        userRepository.save(user);
        return user;
    }

    //对更新用户的参数需要进行校验
    public void updateUser(@Valid UpdateUserCommand updateUserCommand) {
        //先得到要更新的用户
        User targetUser = updateUserCommand.getTargetUser();
        //得到要更新的参数
        UpdateUserParam updateUserParam = updateUserCommand.getParam();
        //进行更新操作
        targetUser.update(updateUserParam.getEmail(),
                updateUserParam.getUsername(),
                updateUserParam.getPassword(),
                updateUserParam.getBio(),
                updateUserParam.getImage());
        //更新好之后重新保存用户
        userRepository.save(targetUser);
    }
}
