package com.stage.digibackend.repository;


import com.stage.digibackend.Collections.ERole;
import com.stage.digibackend.Collections.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);


  @Query(value="{'roles' : ?0}")
  List<User> findByRoleNot(String role);
  @Query(value="{'admin' : ?0}")
  List<User> findByAdmin(String admin);
  @Query(value = "{'email' : ?0}")
  User getUserByUsername(String username);
  @Query(value = "{'verify' : ?0}")
  User getUserCD(String code);

}
