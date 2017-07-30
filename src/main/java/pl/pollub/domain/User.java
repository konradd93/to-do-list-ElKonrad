package pl.pollub.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by konrad on 29.07.17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 5)
    private String username;
}
