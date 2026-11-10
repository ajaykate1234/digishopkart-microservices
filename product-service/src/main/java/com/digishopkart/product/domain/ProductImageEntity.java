package com.digishopkart.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "product_images")
public class ProductImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  @Column(name = "file_name", nullable = false, length = 180)
  private String fileName;

  @Column(name = "content_type", nullable = false, length = 80)
  private String contentType;

  @JdbcTypeCode(SqlTypes.LONGVARBINARY)
  @Column(name = "image_data", nullable = false, columnDefinition = "longblob")
  private byte[] imageData;

  @Column(name = "primary_image", nullable = false)
  private boolean primaryImage;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  public Integer getId() {
    return id;
  }

  public ProductEntity getProduct() {
    return product;
  }

  public void setProduct(ProductEntity product) {
    this.product = product;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public byte[] getImageData() {
    return imageData;
  }

  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }

  public boolean isPrimaryImage() {
    return primaryImage;
  }

  public void setPrimaryImage(boolean primaryImage) {
    this.primaryImage = primaryImage;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(int sortOrder) {
    this.sortOrder = sortOrder;
  }
}
