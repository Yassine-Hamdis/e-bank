import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MobileRechargeComponent } from './mobile-recharge.component';

describe('MobileRechargeComponent', () => {
  let component: MobileRechargeComponent;
  let fixture: ComponentFixture<MobileRechargeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MobileRechargeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MobileRechargeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
