package com.digishopkart.customer.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CustomerDtos {
  private CustomerDtos() {
  }

  public record CustomerRequest(
      @NotBlank @Size(max = 60) String firstName,
      @NotBlank @Size(max = 60) String lastName,
      @Email @NotBlank String email,
      @NotBlank @Size(max = 15) String mobile,
      String customerStatus
  ) {
  }

  public record AddressRequest(
      @NotBlank String addressType,
      @NotBlank String customerFullName,
      @NotBlank String country,
      @NotBlank String state,
      @NotBlank String town,
      @NotBlank String area,
      @NotBlank String houseOrBuilding,
      String landmark,
      @NotNull Integer pinCode,
      @NotBlank String mobile,
      Integer customerId
  ) {
  }

  public record AddressResponse(
      Integer id,
      Integer customerId,
      String addressType,
      String customerFullName,
      String country,
      String state,
      String town,
      String area,
      String houseOrBuilding,
      String landmark,
      Integer pinCode,
      String mobile
  ) {
  }

  public record CustomerResponse(
      Integer id,
      String firstName,
      String lastName,
      String email,
      String mobile,
      String customerStatus,
      java.util.List<AddressResponse> customerAddress
  ) {
  }
}
