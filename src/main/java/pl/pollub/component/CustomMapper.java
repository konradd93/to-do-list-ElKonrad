package pl.pollub.component;

import org.springframework.stereotype.Component;
import pl.pollub.domain.NewTask;
import pl.pollub.domain.Task;

/**
 * Created by konrad on 25.07.17.
 */
@Component
public interface CustomMapper {

    Task mapToEntity(NewTask newTask);
}
