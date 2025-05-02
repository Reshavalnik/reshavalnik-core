package bg.reshavalnik.app.userMapper;

import static org.junit.jupiter.api.Assertions.*;

import bg.reshavalnik.app.mapper.user.UserMapper;
import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void mapToUser_ShouldCopyAllFields() {
        SignupRequest req = new SignupRequest();
        req.setUsername("user@example.com");
        req.setPassword("Passw0rd!1");
        req.setPhone("0888123456");
        req.setNickname("nick");
        req.setFirstName("First");
        req.setLastName("Last");

        User user = userMapper.mapToUser(req);

        assertNotNull(user, "Mapper must return a non-null User object");
        assertEquals(req.getUsername(), user.getUsername(), "username does not match");
        assertEquals(req.getPassword(), user.getPassword(), "password does not match");
        assertEquals(req.getPhone(), user.getPhone(), "phone does not match");
        assertEquals(req.getNickname(), user.getNickname(), "nickname does not match");
        assertEquals(req.getFirstName(), user.getFirstName(), "firstName does not match");
        assertEquals(req.getLastName(), user.getLastName(), "lastName does not match");
    }
}
