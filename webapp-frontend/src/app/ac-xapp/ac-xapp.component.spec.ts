import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcXappComponent } from './ac-xapp.component';

describe('AcXappComponent', () => {
  let component: AcXappComponent;
  let fixture: ComponentFixture<AcXappComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcXappComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcXappComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
