package bg.reshavalnik.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bg.reshavalnik.app.controller.auth.AuthController;
import bg.reshavalnik.app.security.dto.request.LoginRequest;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import bg.reshavalnik.app.service.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock private SecurityService securityService;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(securityService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void authenticateUser_ShouldReturnOkAndSetCookie() throws Exception {
        String requestJson = "{\"username\":\"user@example.com\",\"password\":\"Valid1!\"}";
        ResponseCookie cookie = ResponseCookie.from("JWT", "token123").build();
        when(securityService.getAuthenticateUser(any(LoginRequest.class))).thenReturn(cookie);

        mockMvc.perform(
                        post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, cookie.toString()));
    }

    @Test
    void authenticateUser_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        String badJson = "{\"username\":\"user@example.com\",\"password\":\"pwd\"}";
        mockMvc.perform(
                        post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_ShouldReturnOkAndSetCookie() throws Exception {
        String requestJson =
                "{\"username\":\"newuser@example.com\",\"email\":\"user@example.com\",\"password\":\"Strong1!\"}";
        ResponseCookie cookie = ResponseCookie.from("JWT", "signup-token").build();
        when(securityService.saveRegisterUser(any(SignupRequest.class))).thenReturn(cookie);

        mockMvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, cookie.toString()));
    }

    @Test
    void registerUser_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        String badJson = "{\"username\":\"bad-email\",\"password\":\"pwd\"}";
        mockMvc.perform(
                        post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badJson))
                .andExpect(status().isBadRequest());
    }
}
