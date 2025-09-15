package bitwise.internetBanking.controller;

import bitwise.internetBanking.model.Account;
import bitwise.internetBanking.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public Account create(@RequestBody Account account) {
        return service.createAccount(account);
    }

    @GetMapping
    public List<Account> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Account getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}