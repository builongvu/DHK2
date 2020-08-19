import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeclareVdsComponent } from './declare-vds.component';

describe('DeclareVdsComponent', () => {
  let component: DeclareVdsComponent;
  let fixture: ComponentFixture<DeclareVdsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeclareVdsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeclareVdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
