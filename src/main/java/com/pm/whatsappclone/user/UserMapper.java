package com.pm.whatsappclone.user;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserMapper {
    public User fromTokenAttribute(Map<String, Object> attributes) {
        User user = new User();
        if(attributes.containsKey("sub")){
            user.setId(attributes.get("name").toString());
        }

        if (attributes.containsKey(attributes.get("given_name"))){
            user.setFirstName(attributes.get("given_name").toString());
        } else if (attributes.containsKey(attributes.get("nickname"))){
            user.setFirstName(attributes.get("nickname").toString());
        }

        if (attributes.containsKey(attributes.get("family_name"))){
            user.setLastName(attributes.get("family_name").toString());
        } else if (attributes.containsKey(attributes.get("preferred_username"))){
            user.setLastName(attributes.get("preferred_username").toString());
        }

        if (attributes.containsKey("email")){
            user.setEmail(attributes.get("email").toString());
        }

        user.setLastSeen(LocalDateTime.now());

        return user;
    }

    public UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .lastSeen(user.getLastSeen())
                .isOnline(user.isUserOnline())
                .build();
    }
}
