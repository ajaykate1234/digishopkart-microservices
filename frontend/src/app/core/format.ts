const money = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
});

const when = new Intl.DateTimeFormat('en-IN', {
  dateStyle: 'medium',
  timeStyle: 'short',
});

export function inr(value: number): string {
  return money.format(value);
}

export function timestamp(value: string): string {
  return when.format(new Date(value));
}
