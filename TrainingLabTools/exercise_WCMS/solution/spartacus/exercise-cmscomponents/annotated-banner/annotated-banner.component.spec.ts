import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnotatedBannerComponent } from './annotated-banner.component';

describe('AnnotatedBannerComponent', () => {
  let component: AnnotatedBannerComponent;
  let fixture: ComponentFixture<AnnotatedBannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnnotatedBannerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnnotatedBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
