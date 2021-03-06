package pl.pollub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import pl.pollub.domain.User;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewTask {

    @NotBlank
    private String content;

    private Set<User> contributors;

    public NewTask(String content) {
        this.content = content;
    }
}
