import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ACCESS_ROWS } from '../../core/access';
import { errorMessage } from '../../core/api-error';

@Component({
  selector: 'app-home-page',
  imports: [RouterLink],
  templateUrl: './home-page.html',
})
export class HomePage implements OnInit {
  private readonly http = inject(HttpClient);

  protected readonly rows = ACCESS_ROWS;
  protected readonly api = signal<'checking' | 'up' | 'down'>('checking');
  protected readonly apiDetail = signal('Checking the API.');

  ngOnInit(): void {
    this.http.get<{ status: string; application: string }>('/api/health').subscribe({
      next: (body) => {
        this.api.set('up');
        this.apiDetail.set(`${body.application} is up.`);
      },
      error: (error: unknown) => {
        this.api.set('down');
        this.apiDetail.set(errorMessage(error, 'The API did not answer.'));
      },
    });
  }
}
