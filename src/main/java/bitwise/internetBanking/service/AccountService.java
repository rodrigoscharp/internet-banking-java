package bitwise.internetBanking.service;

import bitwise.internetBanking.exception.AccountNotFoundException;
import bitwise.internetBanking.exception.InsufficientBalanceException;
import bitwise.internetBanking.model.Account;
import bitwise.internetBanking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public List<Account> listAll() {
        return repository.findAll();
    }

    public Account findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void transfer(Long fromId, Long toId, Double amount) {
        Account from = findById(fromId);
        Account to = findById(toId);

        if (from.getBalance() < amount) {
            throw new InsufficientBalanceException("Saldo insuficiente");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        repository.save(from);
        repository.save(to);
    }
}
