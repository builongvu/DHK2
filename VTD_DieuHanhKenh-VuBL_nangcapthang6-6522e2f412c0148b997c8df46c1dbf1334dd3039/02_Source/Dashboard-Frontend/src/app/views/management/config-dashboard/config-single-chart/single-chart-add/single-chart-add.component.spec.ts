import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleChartAddComponent } from './single-chart-add.component';

describe('SingleChartAddComponent', () => {
  let component: SingleChartAddComponent;
  let fixture: ComponentFixture<SingleChartAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SingleChartAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleChartAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
