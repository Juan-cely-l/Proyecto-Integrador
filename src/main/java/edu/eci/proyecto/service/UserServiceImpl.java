package edu.eci.proyecto.service;

import edu.eci.proyecto.dto.UserRequestDTO;
import edu.eci.proyecto.dto.UserResponseDTO;
import edu.eci.proyecto.entity.User;
import edu.eci.proyecto.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl  implements UserService{

    private final HashMap<UUID, User> database =new HashMap<>();

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestdto) {
        UUID id =UUID.randomUUID();
        User user= new User(id,userRequestdto.getName(),userRequestdto.getEmail(),userRequestdto.getPassword());
        database.put(id,user);
        return convertToResponse(user);
    }

    @Override
    public UserResponseDTO getUserById(UUID id) {
        User user =database.get(id);
        if(user== null){
            throw new UserNotFoundException("User with this ID:"+id+ "Not Found");
        }
        return convertToResponse(user);
    }

    @Override
    public List<UserResponseDTO> getAll() {
        return database.values().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestdto) {
        User user = database.get(id);
        if(user==null){
            throw new UserNotFoundException("User with this ID:"+id+ "Not Found");
        }
        if (userRequestdto.getName()!=null){user.setName(userRequestdto.getName());}
        if (userRequestdto.getEmail()!=null){user.setEmail(userRequestdto.getEmail());}
        if (userRequestdto.getPassword()!=null){user.setPassword(userRequestdto.getPassword());}

        return convertToResponse(user);
    }

    @Override
    public UserResponseDTO deleteUser(UUID id) {
        if(!database.containsKey(id)){
            throw new UserNotFoundException("User with this ID:"+id+ "Not Found");
        }
        return convertToResponse(database.remove(id));
    }

    private UserResponseDTO convertToResponse(User user){
        return new UserResponseDTO(user.getId(),user.getName(),user.getEmail());
    }
}
