package mx.culturavibe.act4.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.culturavibe.act4.modelo.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
