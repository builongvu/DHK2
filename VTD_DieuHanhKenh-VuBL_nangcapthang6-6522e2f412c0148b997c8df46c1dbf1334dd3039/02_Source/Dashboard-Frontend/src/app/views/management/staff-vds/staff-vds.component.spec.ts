import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffVdsComponent } from './staff-vds.component';

describe('StaffVdsComponent', () => {
  let component: StaffVdsComponent;
  let fixture: ComponentFixture<StaffVdsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffVdsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffVdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
