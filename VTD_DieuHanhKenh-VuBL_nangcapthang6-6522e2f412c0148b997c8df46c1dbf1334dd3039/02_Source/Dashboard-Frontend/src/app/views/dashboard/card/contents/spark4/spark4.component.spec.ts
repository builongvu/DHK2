import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Spark4Component } from './spark4.component';

describe('Spark4Component', () => {
  let component: Spark4Component;
  let fixture: ComponentFixture<Spark4Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Spark4Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Spark4Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
