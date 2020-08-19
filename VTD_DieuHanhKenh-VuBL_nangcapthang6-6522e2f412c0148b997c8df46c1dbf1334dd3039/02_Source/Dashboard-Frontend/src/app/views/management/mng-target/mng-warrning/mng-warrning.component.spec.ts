import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MngWarrningComponent } from './mng-warning.component';

describe('MngWarrningComponent', () => {
  let component: MngWarrningComponent;
  let fixture: ComponentFixture<MngWarrningComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MngWarrningComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MngWarrningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
