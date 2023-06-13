package com.stage.digibackend.repository;


import com.stage.digibackend.Entity.ERole;
import com.stage.digibackend.Entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
