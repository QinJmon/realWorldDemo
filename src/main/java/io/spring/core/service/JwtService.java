package io.spring.core.service;

import io.spring.core.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface JwtService {

    Optional<String> getSubFromToken(String token);

    String toToken(User user);
}
