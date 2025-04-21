package bg.reshavalnik.app.security.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {

    @InjectMocks private AuthEntryPointJwt authEntryPointJwt;

    @Mock private HttpServletRequest request;

    @Mock private HttpServletResponse response;

    @Mock private AuthenticationException authException;

    private ByteArrayOutputStream baos;

    @BeforeEach
    void setUp() throws IOException {
        baos = new ByteArrayOutputStream();
        ServletOutputStream sos =
                new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        baos.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {}
                };
        when(response.getOutputStream()).thenReturn(sos);
    }

    @Test
    void commence_ShouldWriteUnauthorizedJson() throws IOException, ServletException {
        String path = "/api/test";
        String message = "Auth failed";
        when(request.getServletPath()).thenReturn(path);
        when(authException.getMessage()).thenReturn(message);

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> body = mapper.readValue(baos.toByteArray(), Map.class);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals(message, body.get("message"));
        assertEquals(path, body.get("path"));
    }
}
