import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportUserPopupComponent } from './report-user-popup';

describe('ReportUserPopupComponent', () => {
  let component: ReportUserPopupComponent;
  let fixture: ComponentFixture<ReportUserPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportUserPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportUserPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
