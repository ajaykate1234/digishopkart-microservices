package com.digishopkart.customer.api;

import com.digishopkart.customer.api.CustomerDtos.AddressRequest;
import com.digishopkart.customer.api.CustomerDtos.AddressResponse;
import com.digishopkart.customer.api.CustomerDtos.CustomerRequest;
import com.digishopkart.customer.api.CustomerDtos.CustomerResponse;
import com.digishopkart.customer.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

  private final CustomerService customers;

  public CustomerController(CustomerService customers) {
    this.customers = customers;
  }

  @GetMapping("/api/v1/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "application", "customer-service");
  }

  @GetMapping("/api/v1/customers")
  public List<CustomerResponse> list() {
    return customers.list();
  }

  @GetMapping("/api/v1/customers/me")
  public CustomerResponse me() {
    return customers.me();
  }

  @GetMapping("/api/v1/customers/{customerId}")
  public CustomerResponse get(@PathVariable Integer customerId) {
    return customers.get(customerId);
  }

  @PostMapping("/api/v1/customers")
  public CustomerResponse create(@Valid @RequestBody CustomerRequest request) {
    return customers.create(request);
  }

  @PutMapping("/api/v1/customers/{customerId}")
  public CustomerResponse update(@PathVariable Integer customerId, @Valid @RequestBody CustomerRequest request) {
    return customers.update(customerId, request);
  }

  @GetMapping("/api/v1/customerAddress/{customerId}")
  public List<AddressResponse> addresses(@PathVariable Integer customerId) {
    return customers.addressesFor(customerId);
  }

  @PostMapping("/api/v1/customerAddress")
  public AddressResponse addAddress(@Valid @RequestBody AddressRequest request) {
    return customers.addAddress(request);
  }

  @PutMapping("/api/v1/customerAddress/{addressId}")
  public AddressResponse updateAddress(@PathVariable Integer addressId, @Valid @RequestBody AddressRequest request) {
    return customers.updateAddress(addressId, request);
  }
}
