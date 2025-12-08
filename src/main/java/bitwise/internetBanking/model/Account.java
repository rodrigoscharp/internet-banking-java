package bitwise.internetBanking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    private BigDecimal balance;

    public Account() {}

    public Account(Usuario usuario, BigDecimal balance) {
        this.usuario = usuario;
        this.balance = balance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public BigDecimal getBalance() { return balance; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
