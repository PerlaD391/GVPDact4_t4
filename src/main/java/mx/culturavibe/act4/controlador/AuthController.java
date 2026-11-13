package mx.culturavibe.act4.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import mx.culturavibe.act4.dto.AuthRespuesta;
import mx.culturavibe.act4.dto.LoginDTO;
import mx.culturavibe.act4.dto.RegistroDTO;
import mx.culturavibe.act4.servicio.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthRespuesta> registrar(@Valid @RequestBody RegistroDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
	}

	@PostMapping("/login")
	public AuthRespuesta login(@Valid @RequestBody LoginDTO dto) {
		return authService.login(dto);
	}
}
