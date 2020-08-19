import {Directive, Input, OnChanges, ElementRef, Renderer2, Injectable} from '@angular/core';

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: '[ngxDisabledOnSelector]'
})
@Injectable({
  providedIn: 'root',
})
export class DisabledOnSelectorDirective implements OnChanges {
  @Input() ngxDisabledOnSelector: string;
  @Input() disabled: boolean;
  private readonly nativeElement: HTMLElement;

  constructor(
    private el: ElementRef,
    private renderer2: Renderer2) {
    this.nativeElement = el.nativeElement;
  }

  ngOnChanges() {
    const elements = this.nativeElement.querySelectorAll(this.ngxDisabledOnSelector);
    for (let i = 0; i < elements.length; i++) {
      this.renderer2.setProperty(elements[i], 'disabled', this.disabled);
    }
  }
}
