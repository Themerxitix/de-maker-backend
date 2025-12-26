package nl.demaker.demaker.config;

import nl.demaker.demaker.model.Role;
import nl.demaker.demaker.model.User;
import nl.demaker.demaker.repository.RoleRepository;
import nl.demaker.demaker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            // 1. Initialize roles
            Role adminRole = initializeRole("ROLE_ADMIN");
            Role monteurRole = initializeRole("ROLE_MONTEUR");

            // 2. Initialize test users
            initializeUser("admin", "admin@demaker.nl", "admin123", Set.of(adminRole, monteurRole));
            initializeUser("monteur1", "monteur@demaker.nl", "monteur123", Set.of(monteurRole));

            System.out.println("\n=== Test Credentials ===");
            System.out.println("Admin:   username=admin,    password=admin123");
            System.out.println("Monteur: username=monteur1, password=monteur123");
            System.out.println("========================\n");

        } catch (Exception e) {
            System.err.println("ERROR initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Role initializeRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                    System.out.println("✓ " + roleName + " created");
                    return role;
                });
    }

    private void initializeUser(String username, String email, String password, Set<Role> roles) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(new HashSet<>(roles));
            userRepository.save(user);
            System.out.println("✓ User '" + username + "' created");
        } else {
            System.out.println("✓ User '" + username + "' already exists");
        }
    }
}
