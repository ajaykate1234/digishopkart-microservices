import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CartStore, discountPercent, errorMessage, imageUrl, Product, SessionStore, ShopService } from '@digi/shared';

@Component({
  selector: 'app-product-detail-page',
  imports: [RouterLink],
  templateUrl: './product-detail-page.html',
})
export class ProductDetailPage implements OnInit {
  private readonly shop = inject(ShopService);
  private readonly route = inject(ActivatedRoute);
  private readonly cart = inject(CartStore);
  private readonly session = inject(SessionStore);

  protected readonly product = signal<Product | null>(null);
  protected readonly selectedImage = signal<number | null>(null);
  protected readonly error = signal<string | null>(null);
  protected readonly notice = signal<string | null>(null);
  protected readonly imageUrl = imageUrl;
  protected readonly discountPercent = discountPercent;
  protected readonly canUpload = () => {
    const role = this.session.user()?.role;
    return role === 'ADMIN' || role === 'STAFF';
  };

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('productId'));
      this.shop.product(id).subscribe({
        next: (product) => {
          this.product.set(product);
          this.selectedImage.set(product.images.find((image) => image.primaryImage)?.id ?? product.images[0]?.id ?? null);
          this.error.set(null);
        },
        error: (error: unknown) => this.error.set(errorMessage(error, 'Product was not found.')),
      });
    });
  }

  protected add(): void {
    const product = this.product();
    if (!product) {
      return;
    }
    this.cart.add({
      productId: product.id,
      productName: product.productName,
      brand: product.brand,
      price: product.productPrice,
      imageId: this.selectedImage(),
    });
    this.notice.set('Added to the bag.');
  }

  protected upload(event: Event): void {
    const product = this.product();
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!product || !file) {
      return;
    }
    this.shop.uploadImage(product.id, file).subscribe({
      next: () => {
        this.notice.set('Picture saved in the product database.');
        this.shop.product(product.id).subscribe((fresh) => this.product.set(fresh));
      },
      error: (error: unknown) => this.error.set(errorMessage(error, 'Could not upload the picture.')),
    });
  }
}
