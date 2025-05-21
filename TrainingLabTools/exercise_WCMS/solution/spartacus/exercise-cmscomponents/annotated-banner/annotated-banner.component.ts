import { Component, OnInit } from '@angular/core';
import { Image, CmsBannerComponent, ImageGroup } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable } from 'rxjs';


export interface AnnotatedCmsBannerComponent extends CmsBannerComponent {
  title?: string;
}

@Component({
  selector: 'app-annotated-banner',
  templateUrl: './annotated-banner.component.html',
  styleUrls: ['./annotated-banner.component.scss']
})
export class AnnotatedBannerComponent implements OnInit {

  data$: Observable<AnnotatedCmsBannerComponent> = this.componentData.data$;

  constructor(private componentData: CmsComponentData<AnnotatedCmsBannerComponent>) { }

  ngOnInit(): void {
  }

  getImage(data: AnnotatedCmsBannerComponent): Image | ImageGroup | undefined {
    if (data.media) {
      if ('url' in data.media) {
        return data.media as Image;
      } else {
        return data.media as ImageGroup;
      }
    }
    return undefined;
  }


}
