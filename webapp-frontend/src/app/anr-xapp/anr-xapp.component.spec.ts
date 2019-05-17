import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnrXappComponent } from './anr-xapp.component';

describe('AnrXappComponent', () => {
  let component: AnrXappComponent;
  let fixture: ComponentFixture<AnrXappComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnrXappComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnrXappComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
