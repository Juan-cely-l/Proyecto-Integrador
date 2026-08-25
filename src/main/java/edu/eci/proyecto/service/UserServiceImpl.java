package edu.eci.proyecto.service;

import edu.eci.proyecto.dto.UserRequestDTO;
import edu.eci.proyecto.dto.UserResponseDTO;
import edu.eci.proyecto.entity.User;
import edu.eci.proyecto.exception.UserNotFoundException;
import edu.eci.proyecto.respository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl  implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }
    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestdto) {
        UUID id =UUID.randomUUID();
        User user= new User(id,userRequestdto.getName().toLowerCase(),userRequestdto.getEmail().toLowerCase(),userRequestdto.getPassword().trim());
        userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {
        User user =userRepository.getById(id);
        if(user== null){
            throw new UserNotFoundException("User with this ID:"+id+ "Not Found");
        }
        return convertToResponse(user);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestdto) {
        User user = userRepository.getById(id);
        if(user==null){
            throw new UserNotFoundException("User with this ID:"+id+ "Not Found");
        }
        if (userRequestdto.getName()!=null){user.setName(userRequestdto.getName());}
        if (userRequestdto.getEmail()!=null){user.setEmail(userRequestdto.getEmail());}
        if (userRequestdto.getPassword()!=null){user.setPassword(userRequestdto.getPassword());}

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
