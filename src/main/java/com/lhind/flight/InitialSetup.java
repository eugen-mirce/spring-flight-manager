package com.lhind.flight;

import com.lhind.flight.model.entity.RoleEntity;
import com.lhind.flight.model.entity.UserEntity;
import com.lhind.flight.repository.RoleRepository;
import com.lhind.flight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

@Component
public class InitialSetup {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public InitialSetup(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {

        createRole("ROLE_USER");
        RoleEntity roleAdmin = createRole("ROLE_ADMIN");

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Eugen");
        adminUser.setLastName("Mirce");
        adminUser.setEmail("eugen.el92@hotmail.com");
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("12345678"));
        adminUser.setRoles(Collections.singletonList(roleAdmin));

        UserEntity storedUserDetails = userRepository.findByEmail("eugen.el92@hotmail.com");
        if (storedUserDetails == null) {
            userRepository.save(adminUser);
        }
    }

    @Transactional
    private RoleEntity createRole(String name) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            roleRepository.save(role);
        }
        return role;
    }
}
