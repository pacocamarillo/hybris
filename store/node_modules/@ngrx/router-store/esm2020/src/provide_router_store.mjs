import { ENVIRONMENT_INITIALIZER, inject } from '@angular/core';
import { _createRouterConfig, _ROUTER_CONFIG, ROUTER_CONFIG, } from './router_store_config';
import { FullRouterStateSerializer, } from './serializers/full_serializer';
import { MinimalRouterStateSerializer } from './serializers/minimal_serializer';
import { RouterStateSerializer, } from './serializers/base';
import { StoreRouterConnectingService } from './store_router_connecting.service';
/**
 * Connects the Angular Router to the Store.
 *
 * @usageNotes
 *
 * ```ts
 * bootstrapApplication(AppComponent, {
 *   providers: [
 *     provideStore({ router: routerReducer }),
 *     provideRouterStore(),
 *   ],
 * });
 * ```
 */
export function provideRouterStore(config = {}) {
    return {
        ɵproviders: [
            { provide: _ROUTER_CONFIG, useValue: config },
            {
                provide: ROUTER_CONFIG,
                useFactory: _createRouterConfig,
                deps: [_ROUTER_CONFIG],
            },
            {
                provide: RouterStateSerializer,
                useClass: config.serializer
                    ? config.serializer
                    : config.routerState === 0 /* RouterState.Full */
                        ? FullRouterStateSerializer
                        : MinimalRouterStateSerializer,
            },
            {
                provide: ENVIRONMENT_INITIALIZER,
                multi: true,
                useFactory() {
                    return () => inject(StoreRouterConnectingService);
                },
            },
            StoreRouterConnectingService,
        ],
    };
}
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoicHJvdmlkZV9yb3V0ZXJfc3RvcmUuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyIuLi8uLi8uLi8uLi8uLi9tb2R1bGVzL3JvdXRlci1zdG9yZS9zcmMvcHJvdmlkZV9yb3V0ZXJfc3RvcmUudHMiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUEsT0FBTyxFQUFFLHVCQUF1QixFQUFFLE1BQU0sRUFBRSxNQUFNLGVBQWUsQ0FBQztBQUNoRSxPQUFPLEVBQ0wsbUJBQW1CLEVBQ25CLGNBQWMsRUFDZCxhQUFhLEdBR2QsTUFBTSx1QkFBdUIsQ0FBQztBQUMvQixPQUFPLEVBQ0wseUJBQXlCLEdBRTFCLE1BQU0sK0JBQStCLENBQUM7QUFDdkMsT0FBTyxFQUFFLDRCQUE0QixFQUFFLE1BQU0sa0NBQWtDLENBQUM7QUFDaEYsT0FBTyxFQUVMLHFCQUFxQixHQUN0QixNQUFNLG9CQUFvQixDQUFDO0FBQzVCLE9BQU8sRUFBRSw0QkFBNEIsRUFBRSxNQUFNLG1DQUFtQyxDQUFDO0FBR2pGOzs7Ozs7Ozs7Ozs7O0dBYUc7QUFDSCxNQUFNLFVBQVUsa0JBQWtCLENBRWhDLFNBQStCLEVBQUU7SUFDakMsT0FBTztRQUNMLFVBQVUsRUFBRTtZQUNWLEVBQUUsT0FBTyxFQUFFLGNBQWMsRUFBRSxRQUFRLEVBQUUsTUFBTSxFQUFFO1lBQzdDO2dCQUNFLE9BQU8sRUFBRSxhQUFhO2dCQUN0QixVQUFVLEVBQUUsbUJBQW1CO2dCQUMvQixJQUFJLEVBQUUsQ0FBQyxjQUFjLENBQUM7YUFDdkI7WUFDRDtnQkFDRSxPQUFPLEVBQUUscUJBQXFCO2dCQUM5QixRQUFRLEVBQUUsTUFBTSxDQUFDLFVBQVU7b0JBQ3pCLENBQUMsQ0FBQyxNQUFNLENBQUMsVUFBVTtvQkFDbkIsQ0FBQyxDQUFDLE1BQU0sQ0FBQyxXQUFXLDZCQUFxQjt3QkFDekMsQ0FBQyxDQUFDLHlCQUF5Qjt3QkFDM0IsQ0FBQyxDQUFDLDRCQUE0QjthQUNqQztZQUNEO2dCQUNFLE9BQU8sRUFBRSx1QkFBdUI7Z0JBQ2hDLEtBQUssRUFBRSxJQUFJO2dCQUNYLFVBQVU7b0JBQ1IsT0FBTyxHQUFHLEVBQUUsQ0FBQyxNQUFNLENBQUMsNEJBQTRCLENBQUMsQ0FBQztnQkFDcEQsQ0FBQzthQUNGO1lBQ0QsNEJBQTRCO1NBQzdCO0tBQ0YsQ0FBQztBQUNKLENBQUMiLCJzb3VyY2VzQ29udGVudCI6WyJpbXBvcnQgeyBFTlZJUk9OTUVOVF9JTklUSUFMSVpFUiwgaW5qZWN0IH0gZnJvbSAnQGFuZ3VsYXIvY29yZSc7XG5pbXBvcnQge1xuICBfY3JlYXRlUm91dGVyQ29uZmlnLFxuICBfUk9VVEVSX0NPTkZJRyxcbiAgUk9VVEVSX0NPTkZJRyxcbiAgUm91dGVyU3RhdGUsXG4gIFN0b3JlUm91dGVyQ29uZmlnLFxufSBmcm9tICcuL3JvdXRlcl9zdG9yZV9jb25maWcnO1xuaW1wb3J0IHtcbiAgRnVsbFJvdXRlclN0YXRlU2VyaWFsaXplcixcbiAgU2VyaWFsaXplZFJvdXRlclN0YXRlU25hcHNob3QsXG59IGZyb20gJy4vc2VyaWFsaXplcnMvZnVsbF9zZXJpYWxpemVyJztcbmltcG9ydCB7IE1pbmltYWxSb3V0ZXJTdGF0ZVNlcmlhbGl6ZXIgfSBmcm9tICcuL3NlcmlhbGl6ZXJzL21pbmltYWxfc2VyaWFsaXplcic7XG5pbXBvcnQge1xuICBCYXNlUm91dGVyU3RvcmVTdGF0ZSxcbiAgUm91dGVyU3RhdGVTZXJpYWxpemVyLFxufSBmcm9tICcuL3NlcmlhbGl6ZXJzL2Jhc2UnO1xuaW1wb3J0IHsgU3RvcmVSb3V0ZXJDb25uZWN0aW5nU2VydmljZSB9IGZyb20gJy4vc3RvcmVfcm91dGVyX2Nvbm5lY3Rpbmcuc2VydmljZSc7XG5pbXBvcnQgeyBFbnZpcm9ubWVudFByb3ZpZGVycyB9IGZyb20gJ0BuZ3J4L3N0b3JlJztcblxuLyoqXG4gKiBDb25uZWN0cyB0aGUgQW5ndWxhciBSb3V0ZXIgdG8gdGhlIFN0b3JlLlxuICpcbiAqIEB1c2FnZU5vdGVzXG4gKlxuICogYGBgdHNcbiAqIGJvb3RzdHJhcEFwcGxpY2F0aW9uKEFwcENvbXBvbmVudCwge1xuICogICBwcm92aWRlcnM6IFtcbiAqICAgICBwcm92aWRlU3RvcmUoeyByb3V0ZXI6IHJvdXRlclJlZHVjZXIgfSksXG4gKiAgICAgcHJvdmlkZVJvdXRlclN0b3JlKCksXG4gKiAgIF0sXG4gKiB9KTtcbiAqIGBgYFxuICovXG5leHBvcnQgZnVuY3Rpb24gcHJvdmlkZVJvdXRlclN0b3JlPFxuICBUIGV4dGVuZHMgQmFzZVJvdXRlclN0b3JlU3RhdGUgPSBTZXJpYWxpemVkUm91dGVyU3RhdGVTbmFwc2hvdFxuPihjb25maWc6IFN0b3JlUm91dGVyQ29uZmlnPFQ+ID0ge30pOiBFbnZpcm9ubWVudFByb3ZpZGVycyB7XG4gIHJldHVybiB7XG4gICAgybVwcm92aWRlcnM6IFtcbiAgICAgIHsgcHJvdmlkZTogX1JPVVRFUl9DT05GSUcsIHVzZVZhbHVlOiBjb25maWcgfSxcbiAgICAgIHtcbiAgICAgICAgcHJvdmlkZTogUk9VVEVSX0NPTkZJRyxcbiAgICAgICAgdXNlRmFjdG9yeTogX2NyZWF0ZVJvdXRlckNvbmZpZyxcbiAgICAgICAgZGVwczogW19ST1VURVJfQ09ORklHXSxcbiAgICAgIH0sXG4gICAgICB7XG4gICAgICAgIHByb3ZpZGU6IFJvdXRlclN0YXRlU2VyaWFsaXplcixcbiAgICAgICAgdXNlQ2xhc3M6IGNvbmZpZy5zZXJpYWxpemVyXG4gICAgICAgICAgPyBjb25maWcuc2VyaWFsaXplclxuICAgICAgICAgIDogY29uZmlnLnJvdXRlclN0YXRlID09PSBSb3V0ZXJTdGF0ZS5GdWxsXG4gICAgICAgICAgPyBGdWxsUm91dGVyU3RhdGVTZXJpYWxpemVyXG4gICAgICAgICAgOiBNaW5pbWFsUm91dGVyU3RhdGVTZXJpYWxpemVyLFxuICAgICAgfSxcbiAgICAgIHtcbiAgICAgICAgcHJvdmlkZTogRU5WSVJPTk1FTlRfSU5JVElBTElaRVIsXG4gICAgICAgIG11bHRpOiB0cnVlLFxuICAgICAgICB1c2VGYWN0b3J5KCkge1xuICAgICAgICAgIHJldHVybiAoKSA9PiBpbmplY3QoU3RvcmVSb3V0ZXJDb25uZWN0aW5nU2VydmljZSk7XG4gICAgICAgIH0sXG4gICAgICB9LFxuICAgICAgU3RvcmVSb3V0ZXJDb25uZWN0aW5nU2VydmljZSxcbiAgICBdLFxuICB9O1xufVxuIl19