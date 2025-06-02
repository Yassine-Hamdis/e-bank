import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientTransferComponent } from './client-transfer.component';

describe('ClientTransferComponent', () => {
  let component: ClientTransferComponent;
  let fixture: ComponentFixture<ClientTransferComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientTransferComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientTransferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
