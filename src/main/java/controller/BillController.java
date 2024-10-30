package controller;

import domain.Bill;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.BillService;
import util.BillCreationRequest;


@RestController
@Validated
public class BillController {

    @Autowired
    private BillService billService;


    @GetMapping("/accounts/{accountId}/bills")
    public ResponseEntity<?> getBillsForAccount(@PathVariable Long accountId) {

        return billService.getBillsForAnAccount(accountId);
    }



    @GetMapping("/bills/{billId}")
    public ResponseEntity<?> getBillById(@PathVariable Long billId) {

        return billService.getABillById(billId);
    }



    @GetMapping("/customers/{customerId}/bills")
    public ResponseEntity<?> getBillsForCustomer(@PathVariable Long customerId) {

        return billService.getAllBillsByCustomerId(customerId);
    }



    @PostMapping("/accounts/{accountId}/bills")
    public ResponseEntity<?> createABill(@PathVariable Long accountId, @RequestBody @Valid BillCreationRequest billRequest) {

        return billService.createTheBill(accountId, billRequest);

    }



    @PutMapping("/bills/{billId}")
    public ResponseEntity<?> updateBill(@PathVariable Long billId, @RequestBody @Valid Bill billToUpdateWith) {

        return billService.updateABill(billId, billToUpdateWith);

    }




    @DeleteMapping("/bills/{billId}")
    public ResponseEntity<?> deleteBill(@PathVariable Long billId){

        return billService.deleteABill(billId);

    }



}

