import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GLOSSARY } from '../../core/glossary';

@Component({
  selector: 'app-glossary-page',
  imports: [FormsModule],
  templateUrl: './glossary-page.html',
})
export class GlossaryPage {
  protected readonly query = signal('');
  protected readonly group = signal('All');
  protected readonly groups = ['All', 'Services', 'Security', 'Data', 'Angular'];

  protected readonly entries = computed(() => {
    const query = this.query().trim().toLowerCase();
    const group = this.group();
    return GLOSSARY.filter((entry) => {
      const inGroup = group === 'All' || entry.group === group;
      const inQuery = !query || `${entry.term} ${entry.plain}`.toLowerCase().includes(query);
      return inGroup && inQuery;
    });
  });
}
