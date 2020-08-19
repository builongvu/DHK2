import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigSingleCardComponent } from './config-single-card.component';

describe('ConfigSingleCardComponent', () => {
  let component: ConfigSingleCardComponent;
  let fixture: ComponentFixture<ConfigSingleCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigSingleCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigSingleCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
