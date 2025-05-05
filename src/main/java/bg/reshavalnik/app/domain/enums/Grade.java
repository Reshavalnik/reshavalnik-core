package bg.reshavalnik.app.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Grade {
    G1(1),
    G2(2),
    G3(3),
    G4(4),
    G5(5),
    G6(6),
    G7(7),
    G8(8),
    G9(9),
    G10(10),
    G11(11),
    G12(12);

    private final int level;

    Grade(int level) {
        this.level = level;
    }

    @JsonValue
    public int getLevel() {
        return level;
    }

    @JsonCreator
    public static Grade fromLevel(int level) {
        for (Grade g : values()) {
            if (g.level == level) {
                return g;
            }
        }
        throw new IllegalArgumentException("Invalid Grade: " + level);
    }
}
