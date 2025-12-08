package bitwise.internetBanking.service;

import bitwise.internetBanking.exception.InsufficientBalanceException;
import bitwise.internetBanking.model.Account;
import bitwise.internetBanking.model.Usuario;
import bitwise.internetBanking.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Usuario usuario;
    private Account account;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Test User");

        account = new Account(usuario, new BigDecimal("100.00"));
        account.setId(1L);
    }

    @Test
    void testDeposito_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArguments()[0]);

        BigDecimal amount = new BigDecimal("50.00");
        Account updatedAccount = accountService.deposito(1L, amount);

        assertEquals(new BigDecimal("150.00"), updatedAccount.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testDeposito_NegativeAmount_ThrowsException() {
        BigDecimal amount = new BigDecimal("-50.00");
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.deposito(1L, amount);
        });
    }

    @Test
    void testSaque_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        BigDecimal amount = new BigDecimal("30.00");
        Account updatedAccount = accountService.saque(1L, amount);

        assertEquals(new BigDecimal("70.00"), updatedAccount.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void testSaque_InsufficientBalance_ThrowsException() {
        BigDecimal amount = new BigDecimal("120.00");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class, () -> {
            accountService.saque(1L, amount);
        });
    }

    @Test
    void testTransfer_Success() {
        Account fromAccount = new Account(usuario, new BigDecimal("200.00"));
        fromAccount.setId(1L);
        Account toAccount = new Account(usuario, new BigDecimal("50.00"));
        toAccount.setId(2L);

        when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(toAccount));

        BigDecimal amount = new BigDecimal("75.00");
        accountService.transfer(1L, 2L, amount);

        assertEquals(new BigDecimal("125.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("125.00"), toAccount.getBalance());
        verify(accountRepository, times(2)).save(any(Account.class));
    }
}
