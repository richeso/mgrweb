package com.mapr.mgrweb.service;

//import com.mapr.mgrweb.repository.UserRepository;

import com.mapr.mgrweb.repository.MapRUserRepository;
import com.mapr.mgrweb.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusinessRulesValidationService {

    @Autowired
    private MapRUserRepository userRepository;

    public boolean canAccess(String clientId) {
        String login = SecurityUtils.getCurrentUserLogin().get();
        return true;
    }
}
