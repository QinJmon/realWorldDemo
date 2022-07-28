package io.spring.application.user;

import io.spring.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
/**
 * 自定义验证注解的验证逻辑实现处
 */
public class DuplicatedUsernameValidator
        implements ConstraintValidator<DuplicatedUsernameConstraint, String> {

    @Autowired
    private UserRepository userRepository;

    /**
     * 当value为空/null或者在数据库中查找email为空返回true
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value == null || value.isEmpty()) || !userRepository.findByUsername(value).isPresent();
    }
}
