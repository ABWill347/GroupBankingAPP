package util;


import domain.enums.Medium;
import domain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DepositCreationRequest {

    @NotNull
    private TransactionType depositType;

    private Long payeeId;

    @NotNull
    private Medium depositMedium;

    @Positive
    @NotNull
    private Double depositAmount;

    private String depositDescription;

    public TransactionType getDepositType() {
        return depositType;
    }

    public void setDepositType(TransactionType depositType) {
        this.depositType = depositType;
    }

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public Medium getDepositMedium() {
        return depositMedium;
    }

    public void setDepositMedium(Medium depositMedium) {
        this.depositMedium = depositMedium;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getDepositDescription() {
        return depositDescription;
    }

    public void setDepositDescription(String depositDescription) {
        this.depositDescription = depositDescription;
    }
}
