package nl.demaker.demaker.service;

import nl.demaker.demaker.dto.request.LoginRequest;
import nl.demaker.demaker.dto.request.RegisterRequest;
import nl.demaker.demaker.dto.response.JwtAuthResponse;
import nl.demaker.demaker.exception.ApiException;
import nl.demaker.demaker.model.Role;
import nl.demaker.demaker.model.User;
import nl.demaker.demaker.repository.RoleRepository;
import nl.demaker.demaker.repository.UserRepository;
import nl.demaker.demaker.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                          RoleRepository roleRepository,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtAuthResponse login(LoginRequest request) {
        // Create authentication token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        return new JwtAuthResponse(token);
    }

    @Override
    public String register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Find ROLE_MONTEUR
        Role monteurRole = roleRepository.findByName("ROLE_MONTEUR")
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Role ROLE_MONTEUR not found"));

        // Add role to user
        Set<Role> roles = new HashSet<>();
        roles.add(monteurRole);
        user.setRoles(roles);

        // Save user
        userRepository.save(user);

        return "User registered successfully";
    }
}
