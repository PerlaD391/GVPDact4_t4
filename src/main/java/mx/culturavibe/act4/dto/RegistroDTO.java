package mx.culturavibe.act4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
		String nombre,
		@NotBlank(message = "El correo es obligatorio")
		@Email(message = "El correo no tiene un formato válido")
		String email,
		@NotBlank(message = "La contraseña es obligatoria")
		@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
		String password
) {
}
