import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateSingleCardComponent } from './update-single-card.component';

describe('UpdateSingleCardComponent', () => {
  let component: UpdateSingleCardComponent;
  let fixture: ComponentFixture<UpdateSingleCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdateSingleCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateSingleCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
