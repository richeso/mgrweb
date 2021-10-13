import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMaprRequests, MaprRequests } from '../mapr-requests.model';
import { MaprRequestsService } from '../service/mapr-requests.service';

@Injectable({ providedIn: 'root' })
export class MaprRequestsRoutingResolveService implements Resolve<IMaprRequests> {
  constructor(protected service: MaprRequestsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMaprRequests> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((maprRequests: HttpResponse<MaprRequests>) => {
          if (maprRequests.body) {
            return of(maprRequests.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new MaprRequests());
  }
}
