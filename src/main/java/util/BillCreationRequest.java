package util;



import domain.enums.BillStatus;
import jakarta.validation.constraints.*;

public class BillCreationRequest {


    @NotNull
    private BillStatus billStatus;

    @NotEmpty
    private String payee;

    @NotEmpty
    private String nickname;

    @Max(31)
    @Min(1)
    private Integer recurringDate;

    @Positive
    @NotNull
    private Double paymentAmount;

    @NotNull
    private Long accountId;

    public BillStatus getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(BillStatus billStatus) {
        this.billStatus = billStatus;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getRecurringDate() {
        return recurringDate;
    }

    public void setRecurringDate(Integer recurringDate) {
        this.recurringDate = recurringDate;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
