package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByTituloContainingIgnoreCase (String titulo);

    @Query("SELECT l FROM Livro l WHERE l.idioma ILIKE %:idiomaInformado%")
    List<Livro> livrosPorIdioma(String idiomaInformado);
}
