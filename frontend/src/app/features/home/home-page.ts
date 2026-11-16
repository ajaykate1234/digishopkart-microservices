import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartStore, discountPercent, errorMessage, imageUrl, primaryImage, Product, ShopService } from '@digi/shared';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink],
  templateUrl: './home-page.html',
})
export class HomePage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly cart = inject(CartStore);
  protected readonly products = signal<Product[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly imageUrl = imageUrl;
  protected readonly primaryImage = primaryImage;
  protected readonly discountPercent = discountPercent;

  ngOnInit(): void {
    this.shop.products().subscribe({
      next: (rows) => this.products.set(rows.slice(0, 4)),
      error: (error: unknown) => this.error.set(errorMessage(error, 'The catalog is not reachable.')),
    });
  }

  protected add(product: Product): void {
    const image = primaryImage(product);
    this.cart.add({
      productId: product.id,
      productName: product.productName,
      brand: product.brand,
      price: product.productPrice,
      imageId: image?.id ?? null,
    });
  }
}
