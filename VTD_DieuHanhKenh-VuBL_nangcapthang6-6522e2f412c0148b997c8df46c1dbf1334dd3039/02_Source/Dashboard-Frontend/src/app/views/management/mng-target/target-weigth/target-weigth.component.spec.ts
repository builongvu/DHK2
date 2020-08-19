import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TargetWeigthComponent } from './target-weigth.component';

describe('TargetWeigthComponent', () => {
  let component: TargetWeigthComponent;
  let fixture: ComponentFixture<TargetWeigthComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TargetWeigthComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TargetWeigthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
