package edu.eci.proyecto.service;

import edu.eci.proyecto.dto.UserCreateRequestDTO;
import edu.eci.proyecto.dto.UserResponseDTO;
import edu.eci.proyecto.dto.UserUpdateRequestDTO;
import edu.eci.proyecto.entity.User;
import edu.eci.proyecto.exception.EmailAlreadyExistsException;
import edu.eci.proyecto.exception.UserNotFoundException;
import edu.eci.proyecto.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserServiceImpl  implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestdto) {
        String hashedPassword=passwordEncoder.encode(userCreateRequestdto.password());
        UUID id =UUID.randomUUID();
        String email=userCreateRequestdto.email().toLowerCase();
        if(userRepository.findByEmail(email).isPresent()){
            throw  new EmailAlreadyExistsException("A user with this email already exists");
        }
        User user= new User(id, userCreateRequestdto.name(), email,hashedPassword);
        userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {
        User user= userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User with this ID:"+id+ "Not Found"));
        return convertToResponse(user);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserUpdateRequestDTO userUpdateRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User with this ID:"+id+ "Not Found"));

        if (userUpdateRequestDTO.name()!=null){user.setName(userUpdateRequestDTO.name());}

        if (userUpdateRequestDTO.email() != null) {
        String email =userUpdateRequestDTO.email().trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmail(email)
                .filter(existingUser->!existingUser.getId().equals(id))
                .ifPresent(existingUser-> {
                    throw new EmailAlreadyExistsException(
                            "A user with this email already exists");
                });
            user.setEmail(email);
        }

        if (userUpdateRequestDTO.password()!=null && !userUpdateRequestDTO.password().isBlank()){
            user.setPassword(passwordEncoder.encode(userUpdateRequestDTO.password()));}

        userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public void deleteUser(UUID id) {
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("User with this ID:"+id+ "Not Found");
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO convertToResponse(User user){
        return new UserResponseDTO(user.getId(),user.getName(),user.getEmail());
    }
}
