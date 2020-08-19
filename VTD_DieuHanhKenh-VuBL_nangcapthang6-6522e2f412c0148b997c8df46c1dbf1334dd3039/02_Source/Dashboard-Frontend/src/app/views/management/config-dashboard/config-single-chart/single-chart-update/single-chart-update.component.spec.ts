import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleChartUpdateComponent } from './single-chart-update.component';

describe('SingleChartUpdateComponent', () => {
  let component: SingleChartUpdateComponent;
  let fixture: ComponentFixture<SingleChartUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SingleChartUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleChartUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
