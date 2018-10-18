package oauth.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import oauth.model.User;

@Repository
public interface UserRepo extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}