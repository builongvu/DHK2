import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigGroupCardComponent } from './config-single-chart.component';

describe('ConfigGroupCardComponent', () => {
  let component: ConfigGroupCardComponent;
  let fixture: ComponentFixture<ConfigGroupCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigGroupCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigGroupCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
