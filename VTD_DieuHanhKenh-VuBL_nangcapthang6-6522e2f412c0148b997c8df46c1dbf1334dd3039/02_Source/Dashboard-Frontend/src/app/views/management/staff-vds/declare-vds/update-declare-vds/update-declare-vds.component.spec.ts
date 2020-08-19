import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateDeclareVdsComponent } from './update-declare-vds.component';

describe('UpdateDeclareVdsComponent', () => {
  let component: UpdateDeclareVdsComponent;
  let fixture: ComponentFixture<UpdateDeclareVdsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UpdateDeclareVdsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateDeclareVdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
