package bg.reshavalnik.app.domain.model.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private String kind;
    private String mime;
    private String base64;
}
