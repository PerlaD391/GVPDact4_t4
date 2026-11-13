package mx.culturavibe.act4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
		@NotBlank(message = "El correo es obligatorio")
		@Email(message = "El correo no tiene un formato válido")
		String email,
		@NotBlank(message = "La contraseña es obligatoria")
		String password
) {
}
