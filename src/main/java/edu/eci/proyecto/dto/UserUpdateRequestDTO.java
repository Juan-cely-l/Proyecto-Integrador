package edu.eci.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
        @Size(min=2,max=100)
        String name,
        @Email
        String email,
        @Size(min=6)
        String password

) {
}
