package edu.eci.proyecto.controller;

import edu.eci.proyecto.dto.UserCreateRequestDTO;
import edu.eci.proyecto.dto.UserResponseDTO;
import edu.eci.proyecto.dto.UserUpdateRequestDTO;
import edu.eci.proyecto.service.UserService;
import edu.eci.proyecto.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>>getAllUsers(){
        return ResponseEntity.ok(userService.getAll());
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString()== authentication.principal.user.id.toString()")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO>getById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO>createUser(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO){
        UserResponseDTO createdUser=userService.createUser(userCreateRequestDTO);
        URI location=URI.create("/api/v1/users/" + createdUser.id());
        return ResponseEntity.created(location).body(createdUser);
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO>updateUser(@PathVariable UUID id,@Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO){
        return ResponseEntity.ok(userService.updateUser(id, userUpdateRequestDTO));
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }

}
