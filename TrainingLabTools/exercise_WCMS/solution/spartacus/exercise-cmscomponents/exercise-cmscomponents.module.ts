import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnnotatedBannerComponent } from './annotated-banner/annotated-banner.component';
import { MediaModule } from '@spartacus/storefront'; 
import { CmsConfig, ConfigModule} from '@spartacus/core';

@NgModule({
  declarations: [
    AnnotatedBannerComponent
  ],
  imports: [
    CommonModule,
    MediaModule,
    ConfigModule.withConfig({
      cmsComponents: {
        AnnotatedResponsiveBannerComponent: {
          component: AnnotatedBannerComponent,
        },
      },
    } as CmsConfig)
  ],
  exports: [AnnotatedBannerComponent]
})
export class ExerciseCmscomponentsModule { }
