package mx.culturavibe.act4.servicio;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.culturavibe.act4.dto.AuthRespuesta;
import mx.culturavibe.act4.dto.LoginDTO;
import mx.culturavibe.act4.dto.RegistroDTO;
import mx.culturavibe.act4.modelo.Usuario;
import mx.culturavibe.act4.repositorio.UsuarioRepository;

@Service
@Transactional
public class AuthService implements UserDetailsService {

	private final UsuarioRepository usuarios;

	private final PasswordEncoder passwordEncoder;

	private final ObjectProvider<AuthenticationManager> authenticationManager;

	private final JwtService jwtService;

	public AuthService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
			ObjectProvider<AuthenticationManager> authenticationManager, JwtService jwtService) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarios.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con el correo " + email));
		return new User(usuario.getEmail(), usuario.getPassword(),
				List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())));
	}

	public AuthRespuesta registrar(RegistroDTO dto) {
		if (usuarios.existsByEmailIgnoreCase(dto.email())) {
			throw new IllegalStateException("Ya existe una cuenta con el correo " + dto.email());
		}
		Usuario usuario = new Usuario(dto.nombre(), dto.email(),
				passwordEncoder.encode(dto.password()));
		usuarios.save(usuario);
		String token = jwtService.generarToken(usuario.getEmail());
		return AuthRespuesta.conToken(token, usuario.getEmail(), usuario.getNombre());
	}

	public AuthRespuesta login(LoginDTO dto) {
		authenticationManager.getObject().authenticate(
				new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
		Usuario usuario = usuarios.findByEmailIgnoreCase(dto.email())
				.orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con el correo " + dto.email()));
		String token = jwtService.generarToken(usuario.getEmail());
		return AuthRespuesta.conToken(token, usuario.getEmail(), usuario.getNombre());
	}
}
