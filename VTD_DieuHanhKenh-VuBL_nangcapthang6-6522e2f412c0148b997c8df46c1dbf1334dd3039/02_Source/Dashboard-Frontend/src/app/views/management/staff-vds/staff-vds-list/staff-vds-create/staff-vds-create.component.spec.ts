import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffVdsCreateComponent } from './staff-vds-create.component';

describe('StaffVdsCreateComponent', () => {
  let component: StaffVdsCreateComponent;
  let fixture: ComponentFixture<StaffVdsCreateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffVdsCreateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffVdsCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
