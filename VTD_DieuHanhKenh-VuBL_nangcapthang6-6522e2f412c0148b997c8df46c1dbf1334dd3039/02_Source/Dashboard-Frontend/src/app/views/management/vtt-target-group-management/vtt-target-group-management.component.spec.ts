import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VttTargetGroupManagementComponent } from './vtt-target-group-management.component';

describe('VttTargetGroupManagementComponent', () => {
  let component: VttTargetGroupManagementComponent;
  let fixture: ComponentFixture<VttTargetGroupManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VttTargetGroupManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VttTargetGroupManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
