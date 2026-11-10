package com.digishopkart.product.repo;

import com.digishopkart.product.domain.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

  @Query("""
      select distinct p from ProductEntity p
      left join fetch p.variants
      where p.productStatus = :status
        and (:q is null or lower(p.productName) like lower(concat('%', :q, '%'))
             or lower(p.brand) like lower(concat('%', :q, '%'))
             or lower(p.highlight) like lower(concat('%', :q, '%')))
        and (:skipCategory = true or p.productCategory in :categories)
        and (:skipBrand = true or p.brand in :brands)
        and (:minPrice is null or p.productPrice >= :minPrice)
        and (:maxPrice is null or p.productPrice <= :maxPrice)
      order by p.id
      """)
  List<ProductEntity> search(
      String q,
      boolean skipCategory,
      List<String> categories,
      boolean skipBrand,
      List<String> brands,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      String status);

  @Query("""
      select p.productCategory, count(p) from ProductEntity p
      where p.productStatus = 'active'
      group by p.productCategory
      order by p.productCategory
      """)
  List<Object[]> categoryCounts();

  @Query("""
      select p.brand, count(p) from ProductEntity p
      where p.productStatus = 'active'
      group by p.brand
      order by p.brand
      """)
  List<Object[]> brandCounts();

  @Query("select min(p.productPrice), max(p.productPrice) from ProductEntity p where p.productStatus = 'active'")
  List<Object[]> priceRange();
}
