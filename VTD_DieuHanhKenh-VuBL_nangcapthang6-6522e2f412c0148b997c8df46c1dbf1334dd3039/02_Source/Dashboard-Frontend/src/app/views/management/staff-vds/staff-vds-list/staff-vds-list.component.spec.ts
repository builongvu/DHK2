import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffVdsListComponent } from './staff-vds-list.component';

describe('StaffVdsListComponent', () => {
  let component: StaffVdsListComponent;
  let fixture: ComponentFixture<StaffVdsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffVdsListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffVdsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
