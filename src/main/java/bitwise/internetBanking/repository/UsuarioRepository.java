package bitwise.internetBanking.repository;

import bitwise.internetBanking.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}