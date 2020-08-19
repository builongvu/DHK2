import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSingleCardComponent } from './create-single-card.component';

describe('CreateSingleCardComponent', () => {
  let component: CreateSingleCardComponent;
  let fixture: ComponentFixture<CreateSingleCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateSingleCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateSingleCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
