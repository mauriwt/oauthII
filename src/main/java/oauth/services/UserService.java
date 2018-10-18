package oauth.services;


import java.util.List;

import oauth.model.User;

public interface UserService {

  User save(User user);

  List<User> findAll();

  void delete(Integer id);
}
