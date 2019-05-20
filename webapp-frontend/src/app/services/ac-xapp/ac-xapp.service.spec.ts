import { TestBed } from '@angular/core/testing';

import { AcXappService } from './ac-xapp.service';

describe('AcXappService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AcXappService = TestBed.get(AcXappService);
    expect(service).toBeTruthy();
  });
});
