package com.pm.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public void synchronizeWithIdp(Jwt token) {
        log.info("Synchronizing user with id {}", token.getId());
        getUserEmail(token).ifPresent(email -> {
           log.info("User email synchronizing {}", email);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            User user = userMapper.fromTokenAttribute(token.getClaims());

            if (optionalUser.isPresent()) {
                log.info("User with email {} found, updating user", email);
                user.setId(optionalUser.get().getId());
            } else {
                log.info("User with email {} not found, creating new user", email);
            }
            userRepository.save(user);

        });
    }

    private Optional<String> getUserEmail(Jwt token){
        Map<String, Object> claims = token.getClaims();
        if (claims.containsKey("email")){
            return Optional.of((String) claims.get("email"));
        }
        return Optional.empty();
    }
}
