package me.artemiyulyanov.uptodate.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import me.artemiyulyanov.uptodate.models.Role;
import me.artemiyulyanov.uptodate.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        if (roleRepository.count() > 0) return;

        Role roleUser = Role.builder()
                        .name("USER")
                        .build();
        roleRepository.save(roleUser);

        Role roleAdmin = Role.builder()
                        .name("ADMIN")
                        .build();
        roleRepository.save(roleAdmin);
    }

    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}