package pl.pollub.repository;

import org.springframework.stereotype.Component;
import pl.pollub.domain.User;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * Created by konrad on 29.07.17.
 */
@Component
public class InMemoryUserRepository {

    private AtomicLong counter = new AtomicLong();

    private List<User> usersEntity = new LinkedList<>();

    public User save(User user){
        requireNonNull(user);
        user.setId(generateId());
        usersEntity.add(user);
        return user;
    }

    public User findOne(Long id){
        if(!usersEntity.stream().anyMatch(e -> e.getId().equals(id)))
            return null;
        return usersEntity.stream().filter(e -> e.getId() == id).findFirst().get();
    }

    public List<User> findAll(){
        return usersEntity;
    }

    public void delete(Long id){
        User user = findOne(id);
        usersEntity.remove(user);
    }

    public void delete(User user){
        usersEntity.remove(user);
    }

    public User findUserByUsername(String username){
        if (!usersEntity.stream().anyMatch(e -> e.getUsername().toLowerCase().equals(username.toLowerCase())))
            return null;
        User user = usersEntity.stream().filter(e -> e.getUsername().toLowerCase().equals(username.toLowerCase())).findFirst().get();
        return user;
    }

    private Long generateId() {
        return counter.incrementAndGet();
    }
}
