package bg.reshavalnik.app.domain.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Grade {
    G1(1, "Първи клас"),
    G2(2, "Втори клас"),
    G3(3, "Трети клас"),
    G4(4, "Четвърти клас"),
    G5(5, "Пети клас"),
    G6(6, "Шести клас"),
    G7(7, "Седми клас"),
    G8(8, "Осми клас"),
    G9(9, "Девети клас"),
    G10(10, "Десети клас"),
    G11(11, "Единайсти клас"),
    G12(12, "Дванайсти клас");

    private final int level;
    private final String desc;

    public String getName() {
        return name();
    }

    public static Grade fromLevel(int level) {
        Grade grade = BY_LEVEL.get(level);
        if (grade == null) {
            throw new IllegalArgumentException("Invalid Grade: " + level);
        }
        return grade;
    }

    private static final Map<Integer, Grade> BY_LEVEL =
            Arrays.stream(values()).collect(Collectors.toMap(g -> g.level, g -> g));
}
