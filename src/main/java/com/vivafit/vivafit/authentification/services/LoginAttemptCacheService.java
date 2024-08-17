package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptCacheService {
    @Autowired
    private CacheManager cacheManager;

    public void storeLoginAttempt(String username, LoginUserDto loginUserDto) {
            cacheManager.getCache("loginAttempts").put(username, loginUserDto);
    }

    public LoginUserDto getLoginAttempt(String username) {
        return cacheManager.getCache("loginAttempts").get(username, LoginUserDto.class);
    }

    public void removeLoginAttempt(String username) {
        if(cacheManager.getCache("loginAttempts").get(username) != null) {
            cacheManager.getCache("loginAttempts").evict(username);
        }
    }
}
