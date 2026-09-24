package by.kovalevskiy.FootballBaseGradle.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerErrorResponse {
    private String message;
    private long timestamp;
}
