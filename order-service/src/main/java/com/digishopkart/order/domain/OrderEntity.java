package com.digishopkart.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Integer orderId;

  @Column(name = "customer_id", nullable = false)
  private Integer customerId;

  @Column(name = "customer_email", nullable = false)
  private String customerEmail;

  @Column(name = "discount_id")
  private Integer discountId;

  @Column(name = "status", nullable = false, length = 32)
  private String orderStatus;

  @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "activated_at", nullable = false, length = 40)
  private String activatedAt;

  @Column(name = "updated_at", nullable = false, length = 40)
  private String updatedAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItemEntity> items = new ArrayList<>();

  public Integer getOrderId() {
    return orderId;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  public Integer getDiscountId() {
    return discountId;
  }

  public void setDiscountId(Integer discountId) {
    this.discountId = discountId;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getActivatedAt() {
    return activatedAt;
  }

  public void setActivatedAt(String activatedAt) {
    this.activatedAt = activatedAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<OrderItemEntity> getItems() {
    return items;
  }
}
