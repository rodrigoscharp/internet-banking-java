package bitwise.internetBanking.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountCreateDTO {

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;

    private BigDecimal initialBalance = BigDecimal.ZERO;

    // Getters and Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
