jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMaprRequests, MaprRequests } from '../mapr-requests.model';
import { MaprRequestsService } from '../service/mapr-requests.service';

import { MaprRequestsRoutingResolveService } from './mapr-requests-routing-resolve.service';

describe('Service Tests', () => {
  describe('MaprRequests routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: MaprRequestsRoutingResolveService;
    let service: MaprRequestsService;
    let resultMaprRequests: IMaprRequests | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(MaprRequestsRoutingResolveService);
      service = TestBed.inject(MaprRequestsService);
      resultMaprRequests = undefined;
    });

    describe('resolve', () => {
      it('should return IMaprRequests returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 'ABC' };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMaprRequests = result;
        });

        // THEN
        expect(service.find).toBeCalledWith('ABC');
        expect(resultMaprRequests).toEqual({ id: 'ABC' });
      });

      it('should return new IMaprRequests if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMaprRequests = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultMaprRequests).toEqual(new MaprRequests());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as MaprRequests })));
        mockActivatedRouteSnapshot.params = { id: 'ABC' };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMaprRequests = result;
        });

        // THEN
        expect(service.find).toBeCalledWith('ABC');
        expect(resultMaprRequests).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
