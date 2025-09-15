package bitwise.internetBanking.service;

import bitwise.internetBanking.model.Account;
import bitwise.internetBanking.repository.AccountRepository;
import org.springframework.stereotype.Service;

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
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
