import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffVdsEditComponent } from './staff-vds-edit.component';

describe('StaffVdsEditComponent', () => {
  let component: StaffVdsEditComponent;
  let fixture: ComponentFixture<StaffVdsEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffVdsEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffVdsEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
