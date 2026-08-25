package edu.eci.proyecto.respository;

import edu.eci.proyecto.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UserRepository  extends MongoRepository<User, UUID> {
    User getById(UUID id);
}
