package service;


import domain.Account;
import domain.Customer;
import exceptions.InvalidInputException;
import exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import repository.AccountRepository;
import repository.CustomerRepository;
import successfulresponse.ApiResponse;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;


    public ResponseEntity<?> getAllCustomers() {
        ApiResponse<Customer> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Success");
        Iterable<Customer> allCustomers = customerRepository.findAll();
        List<Customer> listOfCustomers = new ArrayList<>();
        allCustomers.forEach(listOfCustomers::add);
        if (listOfCustomers.isEmpty()) {
            throw new ResourceNotFoundException("No customers found");
        }

        successfulResponse.setData(listOfCustomers);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getCustomerById(Long customerId) {
        verifyCustomerExists(customerId);
        ApiResponse<Customer> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Success");
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new ResourceNotFoundException("Customer with id " + customerId + " not found");
        }

        List<Customer> listOfCustomers = new ArrayList<>();
        customer.ifPresent(listOfCustomers::add);
        successfulResponse.setData(listOfCustomers);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> updateCustomer(Long id, Customer customer) {
        verifyCustomerExists(id);
        if (!id.equals(customer.getId())) {
            throw new ResourceNotFoundException("Customer with id " + id + " does not exist");
        }
        if (customer == null) {
            throw new InvalidInputException("Customer object cannot be null");
        }

        customer = customerRepository.save(customer);
        ApiResponse<Customer> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Success");
        List<Customer> updatedCustomers = new ArrayList<>();
        updatedCustomers.add(customer);
        successfulResponse.setData(updatedCustomers);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }


    public ResponseEntity<?> createCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidInputException("Customer object cannot be null");
        }

        customer = customerRepository.save(customer);
        ApiResponse<Customer> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Customer created");
        List<Customer> createdCustomers = new ArrayList<>();
        createdCustomers.add(customer);
        successfulResponse.setData(createdCustomers);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> getCustomerByAccountId(Long accountId) {
        verifyAccountExists(accountId);
        ApiResponse<Customer> successfulResponse = new ApiResponse<>();
        successfulResponse.setCode(HttpStatus.OK.value());
        successfulResponse.setMessage("Success");

        Account account = accountRepository.findById(accountId).get();

        Long customerId = account.getCustomerId();
        verifyCustomerExists(customerId);
        Customer customer = customerRepository.findById(customerId).get();

        List<Customer> listOfCustomers = new ArrayList<>();
        listOfCustomers.add(customer);
        successfulResponse.setData(listOfCustomers);
        return new ResponseEntity<>(successfulResponse, HttpStatus.OK);
    }

    private void verifyCustomerExists(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer with id " + id + " not found");
        }
    }

    private void verifyAccountExists(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account with id " + id + " not found");
        }
    }
}

