package com.mapr.mgrweb.service;

//import com.mapr.mgrweb.repository.UserRepository;

import com.mapr.mgrweb.repository.MapRUserRepository;
import com.mapr.mgrweb.security.MSUser;
import com.mapr.mgrweb.security.SecurityUtils;
import com.mapr.mgrweb.service.dto.CustomerDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BusinessRulesValidationService {

    @Autowired
    private MapRUserRepository userRepository;

    public boolean canAccess(String clientId) {
        String login = SecurityUtils.getCurrentUserLogin().get();
        return canAccess(login, clientId);
    }

    public void removeDisallowedCustomers(List<CustomerDto> customers) {
        String login = SecurityUtils.getCurrentUserLogin().get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> allowedCustomers = ((MSUser) authentication.getPrincipal()).getCustomers();
        //List<String> allowedCustomers = userRepository.findOneByLogin(login).get().getCustomers();
        customers.removeIf(x -> !allowedCustomers.contains(x.getName()));
    }

    public void removeDisallowedCustomerNames(List<String> customerIds) {
        String login = SecurityUtils.getCurrentUserLogin().get();
        //List<String> allowedCustomers = userRepository.findOneByLogin(login).get().getCustomers();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> allowedCustomers = ((MSUser) authentication.getPrincipal()).getCustomers();
        customerIds.retainAll(allowedCustomers);
    }

    public boolean canAccess(String login, String clientId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> allowedCustomers = ((MSUser) authentication.getPrincipal()).getCustomers();
        return allowedCustomers.contains(clientId);
        //return userRepository.findOneByLogin(login).get().getCustomers().contains(clientId);
    }
}
