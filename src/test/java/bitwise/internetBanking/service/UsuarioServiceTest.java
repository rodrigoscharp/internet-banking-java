package bitwise.internetBanking.service;

import bitwise.internetBanking.model.Usuario;
import bitwise.internetBanking.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testSalvar_EncodesPassword() {
        Usuario usuario = new Usuario();
        usuario.setSenha("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.salvar(usuario);

        verify(passwordEncoder).encode("plainPassword");
        verify(usuarioRepository).save(usuario);
        assert(usuario.getSenha().equals("encodedPassword"));
    }
}
