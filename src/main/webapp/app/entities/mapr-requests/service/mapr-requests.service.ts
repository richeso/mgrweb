import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMaprRequests, getMaprRequestsIdentifier } from '../mapr-requests.model';

export type EntityResponseType = HttpResponse<IMaprRequests>;
export type EntityArrayResponseType = HttpResponse<IMaprRequests[]>;

@Injectable({ providedIn: 'root' })
export class MaprRequestsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/mapr-requests');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(maprRequests: IMaprRequests): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(maprRequests);
    return this.http
      .post<IMaprRequests>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(maprRequests: IMaprRequests): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(maprRequests);
    return this.http
      .put<IMaprRequests>(`${this.resourceUrl}/${getMaprRequestsIdentifier(maprRequests) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(maprRequests: IMaprRequests): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(maprRequests);
    return this.http
      .patch<IMaprRequests>(`${this.resourceUrl}/${getMaprRequestsIdentifier(maprRequests) as string}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IMaprRequests>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IMaprRequests[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMaprRequestsToCollectionIfMissing(
    maprRequestsCollection: IMaprRequests[],
    ...maprRequestsToCheck: (IMaprRequests | null | undefined)[]
  ): IMaprRequests[] {
    const maprRequests: IMaprRequests[] = maprRequestsToCheck.filter(isPresent);
    if (maprRequests.length > 0) {
      const maprRequestsCollectionIdentifiers = maprRequestsCollection.map(
        maprRequestsItem => getMaprRequestsIdentifier(maprRequestsItem)!
      );
      const maprRequestsToAdd = maprRequests.filter(maprRequestsItem => {
        const maprRequestsIdentifier = getMaprRequestsIdentifier(maprRequestsItem);
        if (maprRequestsIdentifier == null || maprRequestsCollectionIdentifiers.includes(maprRequestsIdentifier)) {
          return false;
        }
        maprRequestsCollectionIdentifiers.push(maprRequestsIdentifier);
        return true;
      });
      return [...maprRequestsToAdd, ...maprRequestsCollection];
    }
    return maprRequestsCollection;
  }

  protected convertDateFromClient(maprRequests: IMaprRequests): IMaprRequests {
    return Object.assign({}, maprRequests, {
      // requestDate: maprRequests.requestDate?.isValid() ? maprRequests.requestDate.toJSON() : undefined,
      //  statusDate: maprRequests.statusDate?.isValid() ? maprRequests.statusDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.requestDate = res.body.requestDate ? dayjs(res.body.requestDate) : undefined;
      res.body.statusDate = res.body.statusDate ? dayjs(res.body.statusDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((maprRequests: IMaprRequests) => {
        maprRequests.requestDate = maprRequests.requestDate ? dayjs(maprRequests.requestDate) : undefined;
        maprRequests.statusDate = maprRequests.statusDate ? dayjs(maprRequests.statusDate) : undefined;
      });
    }
    return res;
  }
}
