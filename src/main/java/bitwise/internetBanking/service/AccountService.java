package bitwise.internetBanking.service;

import bitwise.internetBanking.exception.AccountNotFoundException;
import bitwise.internetBanking.exception.InsufficientBalanceException;
import bitwise.internetBanking.model.Account;
import bitwise.internetBanking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account createAccount(Account account) {
        // Garante que a conta comece com um saldo não nulo
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        return repository.save(account);
    }

    public List<Account> listAll() {
        return repository.findAll();
    }

    public Account findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new AccountNotFoundException("Conta não encontrada: " + id));
    }

    @Transactional
    public Account deposito(Long id, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }
        Account account = findById(id);
        account.setBalance(account.getBalance().add(amount));
        return repository.save(account);
    }

    @Transactional
    public Account saque(Long id, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser positivo.");
        }
        Account account = findById(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente para o saque.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return repository.save(account);
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo.");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("A conta de origem e destino não podem ser a mesma.");
        }

        // Bloqueio pessimista para evitar race conditions
        Account from = repository.findByIdWithLock(fromId)
                .orElseThrow(() -> new AccountNotFoundException("Conta de origem não encontrada: " + fromId));
        Account to = repository.findByIdWithLock(toId)
                .orElseThrow(() -> new AccountNotFoundException("Conta de destino não encontrada: " + toId));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo insuficiente na conta de origem.");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        repository.save(from);
        repository.save(to);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
