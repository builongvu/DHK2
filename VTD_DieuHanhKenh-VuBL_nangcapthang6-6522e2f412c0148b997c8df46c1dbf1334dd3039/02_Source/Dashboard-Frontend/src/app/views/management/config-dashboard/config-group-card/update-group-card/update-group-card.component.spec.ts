import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateGroupCardComponent } from './update-group-card.component';

describe('UpdateGroupCardComponent', () => {
  let component: UpdateGroupCardComponent;
  let fixture: ComponentFixture<UpdateGroupCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdateGroupCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateGroupCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
