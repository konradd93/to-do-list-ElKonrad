package pl.pollub.component;

import org.springframework.stereotype.Component;
import pl.pollub.domain.User;
import pl.pollub.dto.NewTask;
import pl.pollub.domain.Task;
import pl.pollub.dto.UserCreateForm;

/**
 * Created by konrad on 25.07.17.
 */
@Component
public interface CustomMapper {

    Task mapToEntity(NewTask newTask);

    User mapToEntity(UserCreateForm userCreateForm);
}
