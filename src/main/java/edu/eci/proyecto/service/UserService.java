package edu.eci.proyecto.service;

import edu.eci.proyecto.dto.Userdto;
import edu.eci.proyecto.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(Userdto userdto);
    User updateUser(UUID id , Userdto userdto);
    User getUserById(UUID id);
    List<User>getAll();
    Boolean deleteUser(UUID id);


}