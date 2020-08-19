import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { Spark3Component } from './spark3.component';

describe('Spark3Component', () => {
  let component: Spark3Component;
  let fixture: ComponentFixture<Spark3Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ Spark3Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(Spark3Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
