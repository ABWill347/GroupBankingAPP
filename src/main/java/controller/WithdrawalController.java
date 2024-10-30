package controller;


import domain.Withdrawal;
import service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import successfulresponse.ApiResponse;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @GetMapping(value = "/accounts/{accountId}/withdrawals")
    public ResponseEntity<?> getAllWithdrawalsByAccountId(@PathVariable Long accountId) {
        List<Withdrawal> listOfWithdrawals = new ArrayList<>();
        withdrawalService.getAllWithdrawalsByAccountId(accountId).forEach(listOfWithdrawals::add);
        ApiResponse<Withdrawal> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("All withdrawals retrieved successfully");
        successfulResponse.setData(listOfWithdrawals);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }


    @GetMapping(value = "/withdrawals/{withdrawalId}")
    public ResponseEntity<?> getWithdrawalById(@PathVariable Long withdrawalId) {
        return withdrawalService.getWithdrawalById(withdrawalId);
    }


    @PostMapping(value = "/accounts/{accountId}/withdrawals")
    public ResponseEntity<?> createWithdrawal(@RequestBody Withdrawal withdrawal, @PathVariable Long accountId) {
        withdrawalService.createWithdrawal(withdrawal,accountId);
        ApiResponse<Withdrawal> successfulResponse = new ApiResponse<>();
        List<Withdrawal> listOfWithdrawals = new ArrayList<>();
        listOfWithdrawals.add(withdrawal);
        successfulResponse.setCode(HttpStatus.CREATED.value());
        successfulResponse.setMessage("Withdrawal successful");
        successfulResponse.setData(listOfWithdrawals);
        return new ResponseEntity<>(successfulResponse, HttpStatus.CREATED);
    }


    @PutMapping(value = "/withdrawals/{withdrawalId}")
    public ResponseEntity<?> updateWithdrawal(@RequestBody Withdrawal withdrawal, @PathVariable Long withdrawalId) {
        withdrawalService.updateWithdrawal(withdrawal, withdrawalId);
        ApiResponse<Withdrawal> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Updated withdrawal successfully");
        List<Withdrawal> listOfWithdrawal = new ArrayList<>();
        listOfWithdrawal.add(withdrawal);
        successfulResponse.setData(listOfWithdrawal);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }
    
    @DeleteMapping(value = "/withdrawals/{withdrawalId}")
    public ResponseEntity<?> deleteWithdrawal(@PathVariable Long withdrawalId) {
        withdrawalService.deleteWithdrawal(withdrawalId);
        ApiResponse<Withdrawal> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Withdrawal with id: " + withdrawalId + " successfully deleted");
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }
}
