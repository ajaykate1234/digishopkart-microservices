package com.digishopkart.discount.api;

import com.digishopkart.common.exception.ConflictException;
import com.digishopkart.common.exception.NotFoundException;
import com.digishopkart.discount.domain.DiscountEntity;
import com.digishopkart.discount.repo.DiscountRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscountController {

  private static final Set<String> TYPES = Set.of("FLAT", "PERCENTAGE");
  private final DiscountRepository discounts;

  public DiscountController(DiscountRepository discounts) {
    this.discounts = discounts;
  }

  @GetMapping("/api/v1/health")
  public Map<String, String> health() {
    return Map.of("status", "UP", "application", "discount-service");
  }

  @GetMapping("/api/v1/discounts")
  public List<DiscountResponse> list() {
    return discounts.findAll().stream().map(DiscountController::toResponse).toList();
  }

  @GetMapping("/api/v1/discounts/{discountId}")
  public DiscountResponse get(@PathVariable Integer discountId) {
    return toResponse(require(discountId));
  }

  @PostMapping("/api/v1/discounts")
  public DiscountResponse create(@Valid @RequestBody DiscountRequest request) {
    DiscountEntity discount = new DiscountEntity();
    apply(discount, request);
    return toResponse(discounts.save(discount));
  }

  @PutMapping("/api/v1/discounts/{discountId}")
  @Transactional
  public DiscountResponse update(@PathVariable Integer discountId, @Valid @RequestBody DiscountRequest request) {
    DiscountEntity discount = require(discountId);
    apply(discount, request);
    return toResponse(discount);
  }

  private DiscountEntity require(Integer id) {
    return discounts.findById(id).orElseThrow(() -> new NotFoundException("Discount " + id + " was not found."));
  }

  private static void apply(DiscountEntity discount, DiscountRequest request) {
    String type = request.discountType().trim().toUpperCase();
    if (!TYPES.contains(type)) {
      throw new ConflictException("discountType must be FLAT or PERCENTAGE.");
    }
    discount.setCouponName(request.couponName().trim());
    discount.setDiscountType(type);
    discount.setCouponValue(request.couponValue());
  }

  private static DiscountResponse toResponse(DiscountEntity discount) {
    return new DiscountResponse(discount.getId(), discount.getCouponName(), discount.getDiscountType(), discount.getCouponValue());
  }

  public record DiscountRequest(
      @NotBlank String couponName,
      @NotBlank String discountType,
      @NotNull @PositiveOrZero BigDecimal couponValue
  ) {
  }

  public record DiscountResponse(Integer id, String couponName, String discountType, BigDecimal couponValue) {
  }
}
