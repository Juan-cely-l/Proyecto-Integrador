package edu.eci.proyecto.service;

import edu.eci.proyecto.dto.UserRequestDTO;
import edu.eci.proyecto.dto.UserResponseDTO;
import edu.eci.proyecto.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestdto);
    UserResponseDTO updateUser(UUID id , UserRequestDTO userRequestdto);
    UserResponseDTO getUserById(UUID id);
    List<UserResponseDTO>getAll();
    UserResponseDTO deleteUser(UUID id);
}