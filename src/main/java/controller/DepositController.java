package controller;



import domain.Deposit;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.DepositService;
import util.DepositCreationRequest;

@RestController
@Validated
public class DepositController {


@Autowired
private DepositService depositService;


    @GetMapping("/accounts/{accountId}/deposits")
    public ResponseEntity<?> getDepositsByAccount(@PathVariable Long accountId){

        return depositService.getAllDepositsByAccount(accountId);

    }



    @GetMapping("/deposits/{depositId}")
    public ResponseEntity<?> getDepositById(@PathVariable Long depositId){

        return depositService.getADepositById(depositId);

    }


    @PostMapping("/accounts/{accountId}/deposits")
    public ResponseEntity<?> createADeposit(@PathVariable Long accountId, @Valid @RequestBody DepositCreationRequest depositCreationRequest){

        return depositService.startCreateDepositProcess(accountId, depositCreationRequest);

    }



    @PutMapping("/deposits/{depositId}")
    public ResponseEntity<?> updateDeposit(@PathVariable Long depositId, @Valid @RequestBody Deposit depositToUpdateWith){

        return depositService.updateADeposit(depositId, depositToUpdateWith);

    }



    @DeleteMapping("/deposits/{depositId}")
    public ResponseEntity<?> deleteDeposit(@PathVariable Long depositId){

        return depositService.deleteADeposit(depositId);

    }

    @PutMapping("/deposits/process/{depositId}")
    public ResponseEntity<?> processDeposit(@PathVariable Long depositId){

        return depositService.processDepositById(depositId);

    }

}
