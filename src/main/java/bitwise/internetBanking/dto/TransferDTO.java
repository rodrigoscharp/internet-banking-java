package bitwise.internetBanking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferDTO {

    @NotNull(message = "A conta de origem é obrigatória")
    private Long fromAccountId;

    @NotNull(message = "A conta de destino é obrigatória")
    private Long toAccountId;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor da transferência deve ser positivo")
    private BigDecimal amount;

    // Getters and Setters
    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
