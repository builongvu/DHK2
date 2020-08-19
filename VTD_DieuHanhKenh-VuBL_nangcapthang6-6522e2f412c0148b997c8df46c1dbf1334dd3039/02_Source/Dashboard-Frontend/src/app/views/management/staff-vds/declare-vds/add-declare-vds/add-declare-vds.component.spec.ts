import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDeclareVdsComponent } from './add-declare-vds.component';

describe('AddDeclareVdsComponent', () => {
  let component: AddDeclareVdsComponent;
  let fixture: ComponentFixture<AddDeclareVdsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddDeclareVdsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDeclareVdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
