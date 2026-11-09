package com.digishopkart.product.domain;

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
@Table(name = "products")
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "product_name", nullable = false, length = 120)
  private String productName;

  @Column(nullable = false, length = 80)
  private String brand;

  @Column(name = "product_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal productPrice;

  @Column(name = "status", nullable = false, length = 16)
  private String productStatus;

  @Column(name = "product_category", nullable = false, length = 32)
  private String productCategory;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<VariantEntity> variants = new ArrayList<>();

  public Integer getId() {
    return id;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public BigDecimal getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(BigDecimal productPrice) {
    this.productPrice = productPrice;
  }

  public String getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(String productStatus) {
    this.productStatus = productStatus;
  }

  public String getProductCategory() {
    return productCategory;
  }

  public void setProductCategory(String productCategory) {
    this.productCategory = productCategory;
  }

  public List<VariantEntity> getVariants() {
    return variants;
  }
}
