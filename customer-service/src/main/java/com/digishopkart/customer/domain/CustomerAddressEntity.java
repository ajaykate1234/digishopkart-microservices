package com.digishopkart.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_addresses")
public class CustomerAddressEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id")
  private CustomerEntity customer;

  @Column(name = "address_type", nullable = false, length = 16)
  private String addressType;

  @Column(name = "customer_full_name", nullable = false, length = 120)
  private String customerFullName;

  @Column(nullable = false, length = 80)
  private String country;

  @Column(nullable = false, length = 80)
  private String state;

  @Column(nullable = false, length = 80)
  private String town;

  @Column(nullable = false, length = 80)
  private String area;

  @Column(name = "house_or_building", nullable = false, length = 120)
  private String houseOrBuilding;

  @Column(length = 120)
  private String landmark;

  @Column(name = "pin_code", nullable = false)
  private Integer pinCode;

  @Column(nullable = false, length = 15)
  private String mobile;

  public Integer getId() {
    return id;
  }

  public CustomerEntity getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerEntity customer) {
    this.customer = customer;
  }

  public String getAddressType() {
    return addressType;
  }

  public void setAddressType(String addressType) {
    this.addressType = addressType;
  }

  public String getCustomerFullName() {
    return customerFullName;
  }

  public void setCustomerFullName(String customerFullName) {
    this.customerFullName = customerFullName;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getHouseOrBuilding() {
    return houseOrBuilding;
  }

  public void setHouseOrBuilding(String houseOrBuilding) {
    this.houseOrBuilding = houseOrBuilding;
  }

  public String getLandmark() {
    return landmark;
  }

  public void setLandmark(String landmark) {
    this.landmark = landmark;
  }

  public Integer getPinCode() {
    return pinCode;
  }

  public void setPinCode(Integer pinCode) {
    this.pinCode = pinCode;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
