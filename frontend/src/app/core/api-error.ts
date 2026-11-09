import { HttpErrorResponse } from '@angular/common/http';

export function errorMessage(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }
  if (error.status === 0) {
    return 'The gateway is not running. Start it on port 8088, then try again.';
  }
  const body = error.error as { message?: string; fields?: { field: string; message: string }[] } | null;
  if (body && typeof body.message === 'string') {
    const fields = Array.isArray(body.fields)
      ? body.fields.map((field) => `${field.field}: ${field.message}`).join(' ')
      : '';
    return fields ? `${body.message} ${fields}` : body.message;
  }
  return fallback;
}
