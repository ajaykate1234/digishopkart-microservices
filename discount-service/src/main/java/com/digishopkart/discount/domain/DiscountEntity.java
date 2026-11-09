package com.digishopkart.discount.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "discounts")
public class DiscountEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "coupon_name", nullable = false, unique = true, length = 80)
  private String couponName;

  @Column(name = "discount_type", nullable = false, length = 16)
  private String discountType;

  @Column(name = "coupon_value", nullable = false, precision = 12, scale = 2)
  private BigDecimal couponValue;

  public Integer getId() {
    return id;
  }

  public String getCouponName() {
    return couponName;
  }

  public void setCouponName(String couponName) {
    this.couponName = couponName;
  }

  public String getDiscountType() {
    return discountType;
  }

  public void setDiscountType(String discountType) {
    this.discountType = discountType;
  }

  public BigDecimal getCouponValue() {
    return couponValue;
  }

  public void setCouponValue(BigDecimal couponValue) {
    this.couponValue = couponValue;
  }
}
