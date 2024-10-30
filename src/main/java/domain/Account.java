package domain;


import domain.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "ACCOUNT")
public class Account {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "ACCOUNT_ID")
 private Long id;

 @NotNull
 @Enumerated(EnumType.STRING)
 @Column(name = "ACCOUNT_TYPE")
 private AccountType type;

 @NotBlank
 @Size(min = 3, max = 20)
 @Column(name = "ACCOUNT_NICKNAME")
 private String nickname;

 @Min(0)
 @Column(name = "ACCOUNT_REWARDS")
 private Integer rewards;

 @Min(0)
 @Column(name = "ACCOUNT_BALANCE")
 private Double balance;

 @NotNull
 @Column(name = "CUSTOMER_ID")
 private Long customerId;


 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public AccountType getType() {
  return type;
 }

 public void setType(AccountType type) {
  this.type = type;
 }

 public String getNickname() {
  return nickname;
 }

 public void setNickname(String nickname) {
  this.nickname = nickname;
 }

 public Integer getRewards() {
  return rewards;
 }

 public void setRewards(Integer rewards) {
  this.rewards = rewards;
 }

 public Double getBalance() {
  return balance;
 }

 public void setBalance(Double balance) {
  this.balance = balance;
 }

 public @NotNull Long getCustomerId() {
  return customerId;
 }

 public void setCustomerId(@NotNull Long customerId) {
  this.customerId = customerId;
 }
}
