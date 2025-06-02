package com.ebanking.service;

import com.ebanking.entity.Administrator;
import com.ebanking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Service for initializing default data
 */
@Service
@Transactional
public class DataInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Initialize default admin user if not exists
     */
    @PostConstruct
    public void initializeDefaultData() {
        logger.info("Initializing default data...");
        createDefaultAdminUser();
        logger.info("Default data initialization completed.");
    }

    private void createDefaultAdminUser() {
        String defaultAdminUsername = "admin";
        
        if (!userRepository.existsByUsername(defaultAdminUsername)) {
            Administrator admin = new Administrator(
                defaultAdminUsername,
                passwordEncoder.encode("admin123"),
                "admin@ebanking.com",
                "+1234567890"
            );
            admin.setDepartment("IT");
            admin.setAdminLevel("SUPER");
            
            userRepository.save(admin);
            logger.info("Default admin user created: username={}, email={}", 
                       admin.getUsername(), admin.getEmail());
        } else {
            logger.info("Default admin user already exists");
        }
    }
}
