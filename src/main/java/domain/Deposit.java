package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Entity
public class Deposit {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name="deposit_id")
    private Long depositId;

    @Column(name="deposit_type")
    @NotEmpty
    private String type;

    @Column(name="transaction_date")
    @NotEmpty
    private String transactionDate;

    @Column(name="deposit_status")
    @NotEmpty
    private String status;

    @Column(name="deposit_payee_id")
    private Long payee_id;

    @Column(name="deposit_medium")
    @NotEmpty
    private String medium;

    @Column(name="deposit_amount")
    @NotNull
    @Positive
    private Double amount;

    @Column(name="deposit_description")
    private String description;

    @NotNull
    @Column(name="account_id")
    private Long accountId;


    // Getters and Setters

    public Long getDepositId() {
        return depositId;
    }

    public void setDepositId(Long depositId) {
        this.depositId = depositId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Long getPayee_id() {
        return payee_id;
    }


    public void setPayee_id(Long payee_id) {
        this.payee_id = payee_id;
    }


    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}


