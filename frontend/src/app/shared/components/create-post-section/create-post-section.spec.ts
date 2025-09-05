import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatePostSection } from './create-post-section';

describe('CreatePostSection', () => {
  let component: CreatePostSection;
  let fixture: ComponentFixture<CreatePostSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatePostSection]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreatePostSection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
