package nl.demaker.demaker.service;

import nl.demaker.demaker.dto.request.LoginRequest;
import nl.demaker.demaker.dto.request.RegisterRequest;
import nl.demaker.demaker.dto.response.JwtAuthResponse;

public interface AuthService {

    JwtAuthResponse login(LoginRequest request);

    String register(RegisterRequest request);
}
