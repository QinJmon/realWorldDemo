package io.spring.application;

import io.spring.application.data.UserData;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserQueryService {
    private UserReadService userReadService;


    public Optional<UserData> findById(String id) {
        return Optional.ofNullable(userReadService.findById(id));
    }
}
