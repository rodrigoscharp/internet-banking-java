package bitwise.internetBanking.controller;

import bitwise.internetBanking.dto.AccountResponseDTO;
import bitwise.internetBanking.dto.UsuarioCreateDTO;
import bitwise.internetBanking.dto.UsuarioResponseDTO;
import bitwise.internetBanking.model.Usuario;
import bitwise.internetBanking.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listarTodos().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        // Você precisará adicionar um método findById no seu UsuarioService
        return service.findById(id)
                .map(usuario -> ResponseEntity.ok(convertToResponseDTO(usuario)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO salvar(@Valid @RequestBody UsuarioCreateDTO usuarioDTO) {
        Usuario usuario = convertToEntity(usuarioDTO);
        Usuario savedUsuario = service.salvar(usuario);
        return convertToResponseDTO(savedUsuario);
    }

    private UsuarioResponseDTO convertToResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        if (usuario.getAccounts() != null) {
            dto.setAccounts(usuario.getAccounts().stream().map(account -> {
                AccountResponseDTO accountDTO = new AccountResponseDTO();
                accountDTO.setId(account.getId());
                accountDTO.setBalance(account.getBalance());
                accountDTO.setUsuarioId(usuario.getId());
                return accountDTO;
            }).collect(Collectors.toSet()));
        }
        return dto;
    }

    private Usuario convertToEntity(UsuarioCreateDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        return usuario;
    }
}