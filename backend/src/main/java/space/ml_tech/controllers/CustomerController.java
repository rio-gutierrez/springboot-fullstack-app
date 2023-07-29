package space.ml_tech.controllers;

import space.ml_tech.customer.Customer;
import space.ml_tech.customer.CustomerService;
import space.ml_tech.customer.CustomerDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"api/v1/customers", "api/v1/customers/"})
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCostumers();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable("id") Integer customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public void addCustomer(@RequestBody CustomerDTO customerDto) {
         customerService.addCustomer(customerDto);
    }

    @PutMapping("/{id}")
    public void updateCustomer(@PathVariable("id") Integer customerId,
                               @RequestBody CustomerDTO customerDTO) {
        customerService.updateCustomer(customerId, customerDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable("id") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }
}
