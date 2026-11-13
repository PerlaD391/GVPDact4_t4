package mx.culturavibe.act4.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.culturavibe.act4.modelo.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
