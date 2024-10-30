package service;


import domain.Account;
import domain.Withdrawal;
import domain.enums.TransactionType;
import exceptions.InsufficientFundsException;
import exceptions.ResourceNotFoundException;
import exceptions.TransactionMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import repository.AccountRepository;
import repository.WithdrawalRepository;
import successfulresponse.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalService {

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private AccountRepository accountRepository;


    public void createWithdrawal(Withdrawal withdrawal, Long accountId) {
        verifyAccountExists(accountId);
        verifyNotDeposit(withdrawal.getType());
        verifySufficientFunds(accountId, withdrawal.getAmount());
        if (withdrawal.getMedium().equalsIgnoreCase("balance")) {
            Double newAccountBalance = accountRepository.findById(accountId).get().getBalance() - withdrawal.getAmount();
            if (withdrawal.getStatus().equalsIgnoreCase("executed")) {
                accountRepository.findById(accountId).get().setBalance(newAccountBalance);
            }
        }
        if (withdrawal.getMedium().equalsIgnoreCase("rewards")) {
            Integer newRewardsBalance = accountRepository.findById(accountId).get().getRewards() - withdrawal.getAmount().intValue();
            if (withdrawal.getStatus().equalsIgnoreCase("executed")) {
                accountRepository.findById(accountId).get().setRewards(newRewardsBalance);
            }
        }
        withdrawal = withdrawalRepository.save(withdrawal);
    }

    public Iterable<Withdrawal> getAllWithdrawalsByAccountId(Long accountId) {
        verifyAccountExists(accountId);
        return withdrawalRepository.findAll();
    }


    public ResponseEntity<?> getWithdrawalById(Long withdrawalId) {
        verifyWithdrawalExists(withdrawalId);
        Optional<Withdrawal> withdrawal = withdrawalRepository.findById(withdrawalId);
        List<Withdrawal> listOfWithdrawal = new ArrayList<>();
        withdrawal.ifPresent(listOfWithdrawal::add);
        ApiResponse<Withdrawal> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Withdrawal with id " + withdrawalId + " retrieved");
        successfulResponse.setData(listOfWithdrawal);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }

    public void updateWithdrawal(Withdrawal withdrawal, Long withdrawalId) {
        Double dbAmount = withdrawalRepository.findById(withdrawalId).get().getAmount();
        Double bodyAmount = withdrawal.getAmount();
        Long accountId = withdrawalRepository.findById(withdrawalId).get().getPayer_id();
        Double balance = accountRepository.findById(accountId).get().getBalance();
        Integer rewards = accountRepository.findById(accountId).get().getRewards();
        Double newBalance;
        Integer newRewards;
        String dbMedium = withdrawalRepository.findById(withdrawalId).get().getMedium();
        String bodyMedium = withdrawal.getMedium();
        String dbStatus = withdrawalRepository.findById(withdrawalId).get().getStatus();
        String bodyStatus = withdrawal.getStatus();

        verifyWithdrawalExists(withdrawalId);
        verifySufficientFunds(accountId, withdrawal.getAmount());

        if(dbMedium.equalsIgnoreCase(bodyMedium) && bodyStatus.equalsIgnoreCase(dbStatus) && bodyStatus.equalsIgnoreCase("executed")){
            if (dbAmount > bodyAmount) {
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                    newBalance = balance + (dbAmount - bodyAmount);
                    accountRepository.findById(accountId).get().setBalance(newBalance);
                }
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                    newRewards = rewards + (dbAmount.intValue() - bodyAmount.intValue());
                    accountRepository.findById(accountId).get().setRewards(newRewards);
                }
            }
            if (dbAmount < bodyAmount) {
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                    newBalance = balance - (bodyAmount - dbAmount);
                    accountRepository.findById(accountId).get().setBalance(newBalance);
                }
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                    newRewards = rewards - (bodyAmount.intValue() - dbAmount.intValue());
                    accountRepository.findById(accountId).get().setRewards(newRewards);
                }
            }
        }
        if(!dbMedium.equalsIgnoreCase(bodyMedium) && bodyStatus.equalsIgnoreCase(dbStatus) && bodyStatus.equalsIgnoreCase("executed")){
            if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                Double newAccountBalance = accountRepository.findById(accountId).get().getBalance() + dbAmount;
                accountRepository.findById(accountId).get().setBalance(newAccountBalance);
            }
            if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                Integer newRewardsBalance = accountRepository.findById(accountId).get().getRewards() + dbAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewardsBalance);
            }
            if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                newRewards = rewards - bodyAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewards);
            }
            if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                newBalance = balance - bodyAmount;
                accountRepository.findById(accountId).get().setBalance(newBalance);
            }
        }
        if(dbMedium.equalsIgnoreCase(bodyMedium) && !bodyStatus.equalsIgnoreCase(dbStatus) && bodyStatus.equalsIgnoreCase("executed")){
            if (withdrawal.getMedium().equalsIgnoreCase("balance")) {
                newBalance = balance - bodyAmount;
                accountRepository.findById(accountId).get().setBalance(newBalance);
            }
            if (withdrawal.getMedium().equalsIgnoreCase("rewards")) {
                newRewards = rewards - bodyAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewards);
            }
            if (dbAmount > bodyAmount) {
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                    newBalance = balance + (dbAmount - bodyAmount);
                    accountRepository.findById(accountId).get().setBalance(newBalance);
                }
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                    newRewards = rewards + (dbAmount.intValue() - bodyAmount.intValue());
                    accountRepository.findById(accountId).get().setRewards(newRewards);
                }
            }
            if (dbAmount < bodyAmount) {
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                    newBalance = balance - (bodyAmount - dbAmount);
                    accountRepository.findById(accountId).get().setBalance(newBalance);
                }
                if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                    newRewards = rewards - (bodyAmount.intValue() - dbAmount.intValue());
                    accountRepository.findById(accountId).get().setRewards(newRewards);
                }
            }
        }
        if(!dbMedium.equalsIgnoreCase(bodyMedium) && !bodyStatus.equalsIgnoreCase(dbStatus) && bodyStatus.equalsIgnoreCase("executed")){
            if (withdrawal.getMedium().equalsIgnoreCase("balance")) {
                newBalance = balance - bodyAmount;
                accountRepository.findById(accountId).get().setBalance(newBalance);
            }
            if (withdrawal.getMedium().equalsIgnoreCase("rewards")) {
                newRewards = rewards - bodyAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewards);
            }
            if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
                newRewards = rewards - bodyAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewards);
            }
            if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
                newBalance = balance - bodyAmount;
                accountRepository.findById(accountId).get().setBalance(newBalance);
            }
        }
        if(dbMedium.equalsIgnoreCase(bodyMedium) && !bodyStatus.equalsIgnoreCase(dbStatus) && bodyStatus.equalsIgnoreCase("pending")){
            if (withdrawal.getMedium().equalsIgnoreCase("balance")) {
                newBalance = balance + dbAmount;
                accountRepository.findById(accountId).get().setBalance(newBalance);
            }
            if (withdrawal.getMedium().equalsIgnoreCase("rewards")) {
                newRewards = rewards + dbAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewards);
            }
        }
        if(!dbMedium.equalsIgnoreCase(bodyMedium) && !bodyStatus.equalsIgnoreCase(dbStatus) && bodyStatus.equalsIgnoreCase("pending")){
            if (withdrawal.getMedium().equalsIgnoreCase("balance")) {
                newRewards = rewards + dbAmount.intValue();
                accountRepository.findById(accountId).get().setRewards(newRewards);
            }
            if (withdrawal.getMedium().equalsIgnoreCase("rewards")) {
                newBalance = balance + dbAmount;
                accountRepository.findById(accountId).get().setBalance(newBalance);
            }
        }

        withdrawal = withdrawalRepository.save(withdrawal);
    }


    public void deleteWithdrawal(Long withdrawalId) {
        verifyWithdrawalExists(withdrawalId);
        Long accountId = withdrawalRepository.findById(withdrawalId).get().getPayer_id();
        Double withdrawal = withdrawalRepository.findById(withdrawalId).get().getAmount();
        if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("balance")) {
            Double newAccountBalance = accountRepository.findById(accountId).get().getBalance() + withdrawal;
            accountRepository.findById(accountId).get().setBalance(newAccountBalance);
        }
        if (withdrawalRepository.findById(withdrawalId).get().getMedium().equalsIgnoreCase("rewards")) {
            Integer newRewardsBalance = accountRepository.findById(accountId).get().getRewards() + withdrawal.intValue();
            accountRepository.findById(accountId).get().setRewards(newRewardsBalance);
        }
        withdrawalRepository.deleteById(withdrawalId);
    }


    private void verifyAccountExists(Long accountId) throws ResourceNotFoundException {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new ResourceNotFoundException("Account with Id: " + accountId + " not found.");
        }
    }


    public void verifyNotDeposit(TransactionType transactionType) {
        if (transactionType.equals(TransactionType.DEPOSIT)) {
            throw new TransactionMismatchException("Transaction type: " + transactionType.name() + " is not valid for this operation.");
        }
    }

    public void verifyWithdrawalExists(Long withdrawalId) throws ResourceNotFoundException {
        Optional<Withdrawal> withdrawal = withdrawalRepository.findById(withdrawalId);
        if (withdrawal.isEmpty()) {
            throw new ResourceNotFoundException("Withdrawal with Id: " + withdrawalId + " not found.");
        }
    }

    public void verifySufficientFunds(Long accountId, Double withdrawalAmount) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        Account account = accountOptional.get();
        Double accountBalance = account.getBalance();
        if (accountBalance < withdrawalAmount) {
            throw new InsufficientFundsException("Insufficient funds in account for deposit transaction of: " + withdrawalAmount + ". Current account balance: " + accountBalance + ".");
        }
    }
}
