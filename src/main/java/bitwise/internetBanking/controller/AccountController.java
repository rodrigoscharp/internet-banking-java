package bitwise.internetBanking.controller;

import bitwise.internetBanking.dto.AccountCreateDTO;
import bitwise.internetBanking.dto.AccountResponseDTO;
import bitwise.internetBanking.dto.TransactionDTO;
import bitwise.internetBanking.dto.TransferDTO;
import bitwise.internetBanking.exception.UsuarioNotFoundException;
import bitwise.internetBanking.model.Account;
import bitwise.internetBanking.model.Usuario;
import bitwise.internetBanking.repository.UsuarioRepository;
import bitwise.internetBanking.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UsuarioRepository usuarioRepository;

    public AccountController(AccountService accountService, UsuarioRepository usuarioRepository) {
        this.accountService = accountService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(@Valid @RequestBody AccountCreateDTO accountDTO) {
        Usuario usuario = usuarioRepository.findById(accountDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com id: " + accountDTO.getUsuarioId()));

        Account newAccount = new Account(usuario, accountDTO.getInitialBalance());
        Account savedAccount = accountService.createAccount(newAccount);
        return new ResponseEntity<>(convertToResponseDTO(savedAccount), HttpStatus.CREATED);
    }

    @GetMapping
    public List<AccountResponseDTO> list() {
        return accountService.listAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getById(@PathVariable Long id) {
        Account account = accountService.findById(id);
        return ResponseEntity.ok(convertToResponseDTO(account));
    }

    @PostMapping("/{id}/deposits")
    public ResponseEntity<AccountResponseDTO> deposit(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDTO) {
        Account updatedAccount = accountService.deposito(id, transactionDTO.getAmount());
        return ResponseEntity.ok(convertToResponseDTO(updatedAccount));
    }

    @PostMapping("/{id}/withdrawals")
    public ResponseEntity<AccountResponseDTO> withdraw(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDTO) {
        Account updatedAccount = accountService.saque(id, transactionDTO.getAmount());
        return ResponseEntity.ok(convertToResponseDTO(updatedAccount));
    }

    @PostMapping("/transfers")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferDTO transferDTO) {
        accountService.transfer(transferDTO.getFromAccountId(), transferDTO.getToAccountId(), transferDTO.getAmount());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }

    private AccountResponseDTO convertToResponseDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setBalance(account.getBalance());
        if (account.getUsuario() != null) {
            dto.setUsuarioId(account.getUsuario().getId());
        }
        return dto;
    }
}