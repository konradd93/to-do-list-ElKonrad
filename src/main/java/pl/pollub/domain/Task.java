package pl.pollub.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @NotNull
    private Long id;

    @NotBlank
    private String content;

    private boolean isActive;

    @NotNull
    private User owner;

    private User whoFinish;

    private Set<User> contributors;

    public Task(long id, String content) {
        this.id = id;
        this.content = content;
        isActive=false;
    }

    public Task(long id, String content, boolean isActive, User owner, Set<User> contributors) {
        this.id = id;
        this.content = content;
        this.owner = owner;
        this.contributors = contributors;
    }
}
