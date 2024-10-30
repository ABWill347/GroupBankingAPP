package controller;


import domain.Customer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CustomerService;

@RestController
public class CustomerController {


    @Autowired
    private CustomerService service;

    @GetMapping(value = "/customers")
    public ResponseEntity<?> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {
        return service.getCustomerById(customerId);
    }

    @RequestMapping(value = "/customers", method = RequestMethod.POST)
    public ResponseEntity<?> createCustomer(@RequestBody @Valid Customer customer) {
        return service.createCustomer(customer);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody @Valid Customer customer) {
        return service.updateCustomer(id, customer);
    }

    @GetMapping("/accounts/{accountId}/customer")
    public ResponseEntity<?> getCustomerByAccountId(@PathVariable Long accountId) {
        return service.getCustomerByAccountId(accountId);
    }


}

