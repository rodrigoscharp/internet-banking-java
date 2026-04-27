package bitwise.internetBanking.service;

import bitwise.internetBanking.model.Usuario;
import bitwise.internetBanking.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }
}