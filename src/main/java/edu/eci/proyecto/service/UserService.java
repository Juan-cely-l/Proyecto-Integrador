package edu.eci.proyecto.service;

import edu.eci.proyecto.dto.UserCreateRequestDTO;
import edu.eci.proyecto.dto.UserResponseDTO;
import edu.eci.proyecto.dto.UserUpdateRequestDTO;

import java.util.List;
import java.util.UUID;


public interface UserService {
    UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestdto);
    UserResponseDTO updateUser(UUID id , UserUpdateRequestDTO userUpdateRequestDTO);
    UserResponseDTO getUserById(UUID id);
    List<UserResponseDTO>getAll();
    void deleteUser(UUID id);
}