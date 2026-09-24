package by.kovalevskiy.FootballBaseGradle.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    @NotEmpty(message = "name should not be empty")
    @Size(min = 2,max = 20,message = "allowed from 2 to 20 characters")
    private String name;
    @NotEmpty(message = "surname should not be empty")
    @Size(min = 2,max = 30,message = "allowed from 2 to 30 characters")
    private String surname;
    @Min(value = 4,message = "player should be older than 3 years")
    private int age;
    @NotEmpty(message = "city should not be empty")
    private String city;
}
