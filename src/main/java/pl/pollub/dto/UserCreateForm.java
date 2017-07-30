package pl.pollub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Created by konrad on 29.07.17.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateForm {

    @NotBlank
    @Size(min = 5)
    private String username;
}
