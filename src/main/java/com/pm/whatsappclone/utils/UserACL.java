package com.pm.whatsappclone.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserACL {

    public boolean getUser(){
        if(!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)){
            return SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
        } else {
            return false;
        }
    }
}
