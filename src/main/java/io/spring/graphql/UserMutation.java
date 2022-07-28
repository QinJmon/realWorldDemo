package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import graphql.execution.DataFetcherResult;
import io.spring.application.user.RegisterParam;
import io.spring.application.user.UpdateUserCommand;
import io.spring.application.user.UpdateUserParam;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.exception.GraphQLCustomizeExceptionHandler;
import io.spring.graphql.exception.InvalidAuthenticationException;
import io.spring.graphql.types.CreateUserInput;
import io.spring.graphql.types.UpdateUserInput;
import io.spring.graphql.types.UserPayload;
import io.spring.graphql.types.UserResult;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@AllArgsConstructor
@DgsComponent
public class UserMutation {
    private UserRepository userRepository;
    private UserService userService;
    private PasswordEncoder encryptService;

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,field = DgsConstants.MUTATION.CreateUser)
    public DataFetcherResult<UserResult> createUser(@InputArgument("input")CreateUserInput input){
        //封装注册用户的参数
        RegisterParam registerParam=new RegisterParam(input.getEmail(),
                input.getUsername(), input.getPassword());
        //调用用户服务创建user
        User user;
        try {
             user = userService.createUser(registerParam);
        } catch (ConstraintViolationException cve) {
            System.out.println(cve.getClass());
            //根据自定义的异常处理方式进行异常处理
            return DataFetcherResult.<UserResult>newResult()
                    .data(GraphQLCustomizeExceptionHandler.getErrorsAsData(cve))
                    .build();

        }
        //返回结果  一个可以从DataFetcher返回的对象，它包含了数据、本地上下文和错误
        return DataFetcherResult.<UserResult>newResult()
                .data(UserPayload.newBuilder().build())
                .localContext(user)
                .build();
    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,field = DgsConstants.MUTATION.Login)
    public DataFetcherResult<UserPayload> login(@InputArgument("password")String password,
                             @InputArgument("email")String email){
        //先根据邮件查找用户
        Optional<User> optional = userRepository.findByEmail(email);
        //如果用户存在并且校验密码正确rawPassword - 要进行编码和匹配的原始密码
        //encodedPassword - 储存的编码密码，与之比较。
        if(optional.isPresent() && encryptService.matches(password,optional.get().getPassword())){
            return DataFetcherResult.<UserPayload>newResult()
                    .data(UserPayload.newBuilder().build())
                    .localContext(optional.get())
                    .build();
        }else {
            //抛出自定义异常
            throw new InvalidAuthenticationException();
        }


    }

    @DgsData(parentType = DgsConstants.MUTATION.TYPE_NAME,field = DgsConstants.MUTATION.UpdateUser)
    public DataFetcherResult<UserPayload> updateUser(@InputArgument("changes")UpdateUserInput updateUserInput){
        //先获取身份验证
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        //验证
        if(authentication instanceof AnonymousAuthenticationToken || authentication.getPrincipal()==null){
            return null;
        }
        //得到当前用户
        io.spring.core.user.User currentUser = (User) authentication.getPrincipal();
        //封装更新用户的参数
        UpdateUserParam param=UpdateUserParam.builder()
                .username(updateUserInput.getUsername())
                .email(updateUserInput.getEmail())
                .bio(updateUserInput.getBio())
                .image(updateUserInput.getImages())
                .password(updateUserInput.getPassword())
                .build();
        //调用服务进行更新操作
        userService.updateUser(new UpdateUserCommand(currentUser,param));
        //返回结果
       return DataFetcherResult.<UserPayload>newResult()
               .data(UserPayload.newBuilder().build())
               .localContext(currentUser)
               .build();

    }
}
