package bg.reshavalnik.app.userMapper;

import static org.junit.jupiter.api.Assertions.*;

import bg.reshavalnik.app.mapper.UserMapper;
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

        assertNotNull(user, "Mapper трябва да върне различен от null обект User");
        assertEquals(req.getUsername(), user.getUsername(), "username не съвпада");
        assertEquals(req.getPassword(), user.getPassword(), "password не съвпада");
        assertEquals(req.getPhone(), user.getPhone(), "phone не съвпада");
        assertEquals(req.getNickname(), user.getNickname(), "nickname не съвпада");
        assertEquals(req.getFirstName(), user.getFirstName(), "firstName не съвпада");
        assertEquals(req.getLastName(), user.getLastName(), "lastName не съвпада");
    }
}
