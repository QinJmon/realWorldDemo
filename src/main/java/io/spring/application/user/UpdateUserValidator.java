package io.spring.application.user;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdateUserValidator
        implements ConstraintValidator<UpdateUserConstraint,UpdateUserCommand> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(UpdateUserCommand value, ConstraintValidatorContext context) {
        //获取参数数据
        String inputEmail = value.getParam().getEmail();
        String inputUsername = value.getParam().getUsername();
        final User targetUser= value.getTargetUser();

        //判断邮箱If a value is present, returns the value, otherwise returns other
        Boolean isEmailValid = userRepository.findByEmail(inputEmail).map(user -> user.equals(targetUser)).orElse(true);
        //判断用户名
        Boolean isUsernameValid = userRepository.findByUsername(inputUsername).map(user -> user.equals(targetUser)).orElse(true);
        //邮箱或者用户名都可用返回true
        if(isEmailValid && isUsernameValid){
            return true;
        }else{//否则，进行分情况处理
            context.disableDefaultConstraintViolation();
            if(!isEmailValid){
                context.buildConstraintViolationWithTemplate("email already exist")
                        .addPropertyNode("email")
                        .addConstraintViolation();
            }
            if(!isUsernameValid){
                context.buildConstraintViolationWithTemplate("username already exist")
                        .addPropertyNode("username")
                        .addConstraintViolation();
            }
            return false;
        }

    }
}
