import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsecutiveWarningComponent } from './consecutive-warning.component';

describe('ConsecutiveWarningComponent', () => {
  let component: ConsecutiveWarningComponent;
  let fixture: ComponentFixture<ConsecutiveWarningComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConsecutiveWarningComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsecutiveWarningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
