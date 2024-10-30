package service;


import domain.Account;
import domain.Deposit;
import domain.enums.Medium;
import domain.enums.TransactionType;
import exceptions.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import repository.AccountRepository;
import repository.DepositRepository;
import successfulresponse.ApiResponse;
import util.DepositCreationRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DepositService {

    private static final Logger log = LoggerFactory.getLogger(DepositService.class);

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<?> getAllDepositsByAccount(Long accountId){

        // verify account exists
        verifyAccountExists(accountId);

        // run method that gets all deposits by accountId and store it in an object
        Iterable<Deposit> allDeposits = depositRepository.findAllByAccountId(accountId);

        // generate a Successful response object and store relevant data in it
        List<Deposit> listForResponse = new ArrayList<>();

        allDeposits.forEach(listForResponse::add);

        ApiResponse<Deposit> apiResponse = new ApiResponse<>(200, "All deposits with accountId (" + accountId + ") retrieved successfully.", listForResponse);

        // log

        log.info("All deposits with accountId (" + accountId + ") retrieved successfully.");

        // populate the response entity
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }

    public ResponseEntity<?> getADepositById(Long depositId) {

        // validate that deposit exists
        verifyDepositExists(depositId);

        // write code that returns the deposit
        Optional<Deposit> depositOptional = depositRepository.findById(depositId);


        // generate a successful response object and store relevant data in it
        List<Deposit> listForResponse = new ArrayList<>();
        Deposit deposit = depositOptional.get();
        listForResponse.add(deposit);
        ApiResponse<Deposit> apiResponse = new ApiResponse<>(200, listForResponse);


        // log
        log.info("Deposit with Id (" + depositId + ") retrieved successfully.");


        // populate the response entity
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> startCreateDepositProcess(Long accountId, DepositCreationRequest depositCreationRequest) {

        // extract variables from depositRequestFormat
        TransactionType depositType = depositCreationRequest.getDepositType();
        Long payeeId = depositCreationRequest.getPayeeId();
        Medium depositMedium = depositCreationRequest.getDepositMedium();
        Double depositAmount = depositCreationRequest.getDepositAmount();
        String depositDescription = depositCreationRequest.getDepositDescription();


        // if depositDescription is null, change it to "No description given"
        if (depositDescription == null){
            depositDescription = "No description given";
        }


        // validate that medium is not withdraw
        verifyNotWithdraw(depositType);

        // validate that account exists and if so generate the Account object
        verifyAccountExists(accountId);
        Account account = accountRepository.findById(accountId).get();

        // check deposit type
        // if deposit type = deposit
        if (depositType.equals(TransactionType.DEPOSIT)) {
            // run a method that processes deposit transaction type
            return processDepositTransaction(accountId, payeeId, depositMedium, depositAmount, depositDescription);

        } else {
            // if deposit type = p2p
            // run a method that processes p2p transaction type
            return processP2pTransaction(accountId, payeeId, depositMedium, depositAmount, depositDescription);

        }
    }

    public ResponseEntity<?> updateADeposit(Long depositId, @Valid Deposit depositToUpdateWith) {

        // validate that depositId still exists
        verifyDepositExists(depositId);

        // get old deposit
        Deposit oldDeposit = depositRepository.findById(depositId).get();

        // validate new deposit
        verifyUpdateDeposit(oldDeposit, depositToUpdateWith);

        // update timestamp
        depositToUpdateWith.setTransactionDate(LocalDate.now().toString());

        // if new depositToUpdate.depositDescription == null set default
        if (depositToUpdateWith.getDescription() == null){
            depositToUpdateWith.setDescription("No description given");
        }

        // afterwards, update
        Deposit updatedDeposit = depositRepository.save(depositToUpdateWith);

        // populate ApiResponse
        List<Deposit> listForResponse = new ArrayList<>();
        listForResponse.add(updatedDeposit);

        ApiResponse<Deposit> apiResponse = new ApiResponse<>(200, "Accepted deposit modification for deposit with Id (" + depositId + ") .", listForResponse);

        // log
        log.info("Accepted deposit modification for deposit with Id (" + depositId + ").");

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteADeposit(Long depositId) {

        // delete deposit logic
        verifyDepositExists(depositId);
        depositRepository.deleteById(depositId);

        // log
        log.info("Deposit with Id (" + depositId + ") deleted successfully.");

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Verify Methods

    public void verifyDepositExists(Long depositId){

        // validation logic
        Optional<Deposit> deposit = depositRepository.findById(depositId);
        if(deposit.isEmpty()){
            throw new ResourceNotFoundException("Deposit with Id (" + depositId + ") not found.");
        }

    }

    public void verifyAccountExists(Long accountId){

        // validation logic
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with Id (" + accountId + ") not found.");
        }

    }

    public void verifyNotWithdraw(TransactionType transactionType){

        // throw TransactionMismatchException or something

        if (transactionType.equals(TransactionType.WITHDRAWAL)){
            throw new TransactionMismatchException("Transaction type (" + transactionType.name() + ") is not valid for this operation.");
        }

    }

    public void verifySufficientFunds(Long accountId, Double depositAmount){

        // get account balance
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        Account account = accountOptional.get();
        Double accountBalance = account.getBalance();

        // check for sufficient funds

        if (accountBalance < depositAmount){
            throw new InsufficientFundsException("Insufficient funds in account for deposit transaction of ($" + depositAmount + "). Current account balance is ($" + accountBalance + ").");
        }
    }

    public void verifyUpdateDeposit(Deposit oldDeposit, Deposit depositToUpdateWith){

        // validate that deposit status is pending
        if (!oldDeposit.getStatus().equals("Pending")){
            throw new InvalidInputException("Can not update deposit with status (" + oldDeposit.getStatus() + ").");
        }

        // accountId's match
        if (!oldDeposit.getAccountId().equals(depositToUpdateWith.getAccountId())){
            throw new ConflictException("Updated accountId must match previous accountId.");
        }

        // accountId still exists
        verifyAccountExists(depositToUpdateWith.getAccountId());

        // depositId's match
        if (!oldDeposit.getDepositId().equals(depositToUpdateWith.getDepositId())){
            throw new ConflictException("Updated depositId must match previous depositId.");
        }

        // depositId still exists
        verifyDepositExists(depositToUpdateWith.getDepositId());

        // verify the new deposit type is Deposit or P2P
        if (!depositToUpdateWith.getType().equals("Deposit") && !depositToUpdateWith.getType().equals("P2P")){
            throw new TransactionMismatchException("Transaction type (" + depositToUpdateWith.getType() + ") is not valid for this operation.");
        }

        // check if p2p, and if so that payeeid still exists
        if (depositToUpdateWith.getType().equals("P2P")){
            verifyAccountExists(depositToUpdateWith.getPayee_id());

            // if p2p, make sure medium isn't points
            if (!depositToUpdateWith.getMedium().equals("Balance")){
                throw new MediumMismatchException("Medium type (" + depositToUpdateWith.getMedium() + ") is not valid for this operation.");
            }
        }
    }

    public void verifyDepositIsPending(Deposit deposit){

        String depositStatus = deposit.getStatus();

        if (!depositStatus.equals("Pending")){
            throw new ConflictException("Deposit status of (" + depositStatus + ") is not valid for processing.");
        }
    }

    // Process Transaction Methods

    public ResponseEntity<?> processDepositTransaction(Long accountId, @Null Long payeeId, Medium depositMedium, Double depositAmount, String depositDescription){

        // check deposit medium
        // if Balance medium
        if (depositMedium.equals(Medium.BALANCE)){
            // make Balance deposit
            return createBalanceDeposit(accountId, depositAmount, depositDescription);

        } else {
            // if rewards medium
            // make rewards deposit
            return createRewardsDeposit(accountId, depositAmount, depositDescription);
        }
    }

    public ResponseEntity<?> processP2pTransaction(Long accountId, @NotNull Long payeeId, Medium depositMedium, Double depositAmount, String depositDescription){

        // validate payeeId
        verifyAccountExists(payeeId);

        // validate that deposit medium is not rewards, if so, throw new MediumMismatch error
        if (depositMedium.equals(Medium.REWARDS)){
            throw new MediumMismatchException("Medium type (" + depositMedium.name() + ") is not valid for this operation.");
        }

        // make p2p deposit
        return createP2pDeposit(accountId, payeeId, depositAmount, depositDescription);

    }


    // Create Deposit Methods

    public ResponseEntity<?> createBalanceDeposit(Long accountId, Double depositAmount, String depositDescription){

        // create new Deposit object
        Deposit deposit = new Deposit();

        // set values of object
        deposit.setType("Deposit");
        deposit.setTransactionDate(LocalDate.now().toString());
        deposit.setStatus("Pending");
        deposit.setMedium("Balance");
        deposit.setAmount(depositAmount);
        deposit.setDescription(depositDescription);
        deposit.setAccountId(accountId);

        // save object to repo and store method call as a new Deposit object
        deposit = depositRepository.save(deposit);

        // populate api response
        List<Deposit> listForResponse = new ArrayList<>();
        listForResponse.add(deposit);

        ApiResponse<?> apiResponse = new ApiResponse<>(201, "Created deposit and added it to the account.", listForResponse);

        // log
        log.info("Created deposit and added it to the account.");

        //return response entity
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<?> createRewardsDeposit(Long accountId, Double depositAmount, String depositDescription){

        // create new Deposit object
        Deposit deposit = new Deposit();

        // set values of object
        deposit.setType("Deposit");
        deposit.setTransactionDate(LocalDate.now().toString());
        deposit.setStatus("Pending");
        deposit.setMedium("Rewards");
        deposit.setAmount(depositAmount);
        deposit.setDescription(depositDescription);
        deposit.setAccountId(accountId);

        // save object to repo and store method call as a new Deposit object
        deposit = depositRepository.save(deposit);

        // populate api response
        List<Deposit> listForResponse = new ArrayList<>();
        listForResponse.add(deposit);

        ApiResponse<?> apiResponse = new ApiResponse<>(201, "Created deposit and added it to the account.", listForResponse);


        // log
        log.info("Created deposit and added it to the account.");

        //return response entity
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<?> createP2pDeposit(Long accountId, Long payeeId, Double depositAmount, String depositDescription){

        // create new Deposit object
        Deposit deposit = new Deposit();

        // set values of object
        deposit.setType("P2P");
        deposit.setTransactionDate(LocalDate.now().toString());
        deposit.setStatus("Pending");
        deposit.setPayee_id(payeeId);
        deposit.setMedium("Balance");
        deposit.setAmount(depositAmount);
        deposit.setDescription(depositDescription);
        deposit.setAccountId(accountId);

        // save object to repo and store method call as a new Deposit object
        deposit = depositRepository.save(deposit);

        // populate ApiResponse
        List<Deposit> listForResponse = new ArrayList<>();
        listForResponse.add(deposit);

        ApiResponse<?> apiResponse = new ApiResponse<>(201, "Created deposit and added it to the account.", listForResponse);

        // log
        log.info("Created deposit and added it to the account.");

        //return response entity
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }


    // Process Deposit Methods

    public ResponseEntity<?> processDepositById(Long depositId) {

        // get deposit from depositId
        verifyDepositExists(depositId);
        Deposit deposit = depositRepository.findById(depositId).get();

        // validate that deposit is pending
        verifyDepositIsPending(deposit);

        // get account id from deposit
        Long accountId = deposit.getAccountId();

        // validate account still exists
        verifyAccountExists(accountId);

        // get transactionType
        String transactionType = deposit.getType();

        // get deposit amount
        Double depositAmount = deposit.getAmount();


        // if p2p
        if (transactionType.equals("P2P")) {

            // check payee account still exists
            Long payeeId = deposit.getPayee_id();
            verifyAccountExists(payeeId);

            // check that payer balance >= deposit amount, if not throw InsufficientFundsException
            verifySufficientFunds(accountId, depositAmount);

            // add depositAmount to payee account balance
            Optional<Account> payeeOptional = accountRepository.findById(payeeId);
            Account payeeAccount = payeeOptional.get();

            Double payeeBalance = payeeAccount.getBalance();
            Double payeeNewBalance = payeeBalance + depositAmount;

            payeeAccount.setBalance(payeeNewBalance);

            accountRepository.save(payeeAccount);

            // subtract depositAmount from payer account

            Optional<Account> payerAccountOptional = accountRepository.findById(accountId);
            Account payerAccount = payerAccountOptional.get();

            Double payerBalance = payerAccount.getBalance();
            Double payerNewBalance = payerBalance - depositAmount;

            payerAccount.setBalance(payerNewBalance);

            accountRepository.save(payerAccount);

            // update deposit status
            deposit.setStatus("Completed");
            deposit = depositRepository.save(deposit);

            List<Deposit> listForResponse = new ArrayList<>();
            listForResponse.add(deposit);

            ApiResponse<?> apiResponse = new ApiResponse<>(200, "Deposit with Id (" + depositId + ") processed successfully.", listForResponse);

            // log
            log.info("Deposit with Id (" + depositId + ") processed successfully.");

            // return response entity
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

        } else {
            // if Deposit
            // add deposit amount based on medium type to account balance
            Optional<Account> accountOptional = accountRepository.findById(accountId);
            Account account = accountOptional.get();

            Double accountBalance = account.getBalance();
            Double accountNewBalance = accountBalance + depositAmount;

            account.setBalance(accountNewBalance);

            accountRepository.save(account);

            // update deposit status
            deposit.setStatus("Completed");
            deposit = depositRepository.save(deposit);

            // populate ApiResponse
            List<Deposit> listForResponse = new ArrayList<>();
            listForResponse.add(deposit);

            ApiResponse<?> apiResponse = new ApiResponse<>(200, "Deposit with Id (" + depositId + ") processed successfully.", listForResponse);

            // log
            log.info("Deposit with Id (" + depositId  + ") processed successfully.");

            //return response entity
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);

        }
    }
}
