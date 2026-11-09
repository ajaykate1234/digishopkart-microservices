package com.digishopkart.customer.service;

import com.digishopkart.common.exception.ConflictException;
import com.digishopkart.common.exception.NotFoundException;
import com.digishopkart.common.security.CurrentUser;
import com.digishopkart.common.security.UserPrincipal;
import com.digishopkart.customer.api.CustomerDtos.AddressRequest;
import com.digishopkart.customer.api.CustomerDtos.AddressResponse;
import com.digishopkart.customer.api.CustomerDtos.CustomerRequest;
import com.digishopkart.customer.api.CustomerDtos.CustomerResponse;
import com.digishopkart.customer.domain.CustomerAddressEntity;
import com.digishopkart.customer.domain.CustomerEntity;
import com.digishopkart.customer.repo.AddressRepository;
import com.digishopkart.customer.repo.CustomerRepository;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

  private final CustomerRepository customers;
  private final AddressRepository addresses;

  public CustomerService(CustomerRepository customers, AddressRepository addresses) {
    this.customers = customers;
    this.addresses = addresses;
  }

  @Transactional(readOnly = true)
  public List<CustomerResponse> list() {
    if (!CurrentUser.hasRole("ADMIN")) {
      throw new AccessDeniedException("admin");
    }
    return customers.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public CustomerResponse get(Integer id) {
    CustomerEntity customer = require(id);
    assertCanRead(customer);
    return toResponse(customer);
  }

  @Transactional(readOnly = true)
  public CustomerResponse me() {
    return toResponse(requireByEmail(CurrentUser.require().getUsername()));
  }

  @Transactional
  public CustomerResponse create(CustomerRequest request) {
    String email = request.email().trim().toLowerCase();
    UserPrincipal user = CurrentUser.require();
    if (!CurrentUser.hasRole("ADMIN") && !user.getUsername().equalsIgnoreCase(email)) {
      throw new AccessDeniedException("own profile");
    }
    if (customers.existsByEmailIgnoreCase(email)) {
      throw new ConflictException("A customer with this email already exists.");
    }
    CustomerEntity customer = new CustomerEntity();
    apply(customer, request, email);
    return toResponse(customers.save(customer));
  }

  @Transactional
  public CustomerResponse update(Integer id, CustomerRequest request) {
    CustomerEntity customer = require(id);
    assertCanWrite(customer);
    String email = request.email().trim().toLowerCase();
    customers.findByEmailIgnoreCase(email)
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
          throw new ConflictException("A customer with this email already exists.");
        });
    apply(customer, request, email);
    return toResponse(customer);
  }

  @Transactional(readOnly = true)
  public List<AddressResponse> addressesFor(Integer customerId) {
    CustomerEntity customer = require(customerId);
    assertCanRead(customer);
    return customer.getAddresses().stream().map(this::toAddress).toList();
  }

  @Transactional
  public AddressResponse addAddress(AddressRequest request) {
    CustomerEntity customer = request.customerId() == null
        ? requireByEmail(CurrentUser.require().getUsername())
        : require(request.customerId());
    assertCanWrite(customer);
    CustomerAddressEntity address = new CustomerAddressEntity();
    address.setCustomer(customer);
    applyAddress(address, request);
    return toAddress(addresses.save(address));
  }

  @Transactional
  public AddressResponse updateAddress(Integer addressId, AddressRequest request) {
    CustomerAddressEntity address = addresses.findById(addressId)
        .orElseThrow(() -> new NotFoundException("Address " + addressId + " was not found."));
    assertCanWrite(address.getCustomer());
    applyAddress(address, request);
    return toAddress(address);
  }

  private void apply(CustomerEntity customer, CustomerRequest request, String email) {
    customer.setFirstName(request.firstName().trim());
    customer.setLastName(request.lastName().trim());
    customer.setEmail(email);
    customer.setMobile(request.mobile().trim());
    String status = request.customerStatus() == null || request.customerStatus().isBlank()
        ? "active"
        : request.customerStatus().trim().toLowerCase();
    if (!status.equals("active") && !status.equals("inactive")) {
      throw new ConflictException("customerStatus must be active or inactive.");
    }
    if (!CurrentUser.hasRole("ADMIN") && status.equals("inactive")) {
      throw new AccessDeniedException("status");
    }
    customer.setCustomerStatus(status);
  }

  private void applyAddress(CustomerAddressEntity address, AddressRequest request) {
    String type = request.addressType().trim().toLowerCase();
    if (!type.equals("shipping") && !type.equals("billing")) {
      throw new ConflictException("addressType must be shipping or billing.");
    }
    address.setAddressType(type);
    address.setCustomerFullName(request.customerFullName().trim());
    address.setCountry(request.country().trim());
    address.setState(request.state().trim());
    address.setTown(request.town().trim());
    address.setArea(request.area().trim());
    address.setHouseOrBuilding(request.houseOrBuilding().trim());
    address.setLandmark(request.landmark() == null ? null : request.landmark().trim());
    address.setPinCode(request.pinCode());
    address.setMobile(request.mobile().trim());
  }

  private CustomerEntity require(Integer id) {
    return customers.findById(id).orElseThrow(() -> new NotFoundException("Customer " + id + " was not found."));
  }

  private CustomerEntity requireByEmail(String email) {
    return customers.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new NotFoundException("No customer profile for " + email + "."));
  }

  private void assertCanRead(CustomerEntity customer) {
    if (CurrentUser.hasRole("ADMIN") || CurrentUser.hasRole("STAFF")) {
      return;
    }
    if (!customer.getEmail().equalsIgnoreCase(CurrentUser.require().getUsername())) {
      throw new AccessDeniedException("customer");
    }
  }

  private void assertCanWrite(CustomerEntity customer) {
    if (CurrentUser.hasRole("ADMIN")) {
      return;
    }
    if (!customer.getEmail().equalsIgnoreCase(CurrentUser.require().getUsername())) {
      throw new AccessDeniedException("customer");
    }
  }

  private CustomerResponse toResponse(CustomerEntity customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getFirstName(),
        customer.getLastName(),
        customer.getEmail(),
        customer.getMobile(),
        customer.getCustomerStatus(),
        customer.getAddresses().stream().map(this::toAddress).toList());
  }

  private AddressResponse toAddress(CustomerAddressEntity address) {
    return new AddressResponse(
        address.getId(),
        address.getCustomer().getId(),
        address.getAddressType(),
        address.getCustomerFullName(),
        address.getCountry(),
        address.getState(),
        address.getTown(),
        address.getArea(),
        address.getHouseOrBuilding(),
        address.getLandmark(),
        address.getPinCode(),
        address.getMobile());
  }
}
