package bitwise.internetBanking.repository;

import bitwise.internetBanking.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Aqui você pode criar consultas personalizadas depois, se quiser.
}
