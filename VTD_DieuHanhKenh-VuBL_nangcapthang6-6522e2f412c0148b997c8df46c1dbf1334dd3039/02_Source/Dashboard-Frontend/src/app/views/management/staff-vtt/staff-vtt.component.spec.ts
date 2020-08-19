import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StaffVttComponent } from './staff-vtt.component';

describe('StaffVttComponent', () => {
  let component: StaffVttComponent;
  let fixture: ComponentFixture<StaffVttComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StaffVttComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StaffVttComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
