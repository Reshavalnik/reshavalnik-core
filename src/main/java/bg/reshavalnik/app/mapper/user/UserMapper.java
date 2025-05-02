package bg.reshavalnik.app.mapper.user;

import bg.reshavalnik.app.security.domain.User;
import bg.reshavalnik.app.security.dto.request.SignupRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User mapToUser(SignupRequest userRegisterRequestModel);
}
