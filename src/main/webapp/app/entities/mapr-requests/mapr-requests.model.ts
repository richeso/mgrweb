import * as dayjs from 'dayjs';

export interface IMaprRequests {
  id?: string;
  type?: string;
  action?: string;
  name?: string;
  path?: string;
  source?: string;
  quota?: string;
  advisoryQuota?: string;
  requestUser?: string;
  requestDate?: dayjs.Dayjs;
  requestStatus?: string;
  statusChangedDate?: dayjs.Dayjs;
  previousStatus?: string;
}

export class MaprRequests implements IMaprRequests {
  constructor(
    public id?: string,
    public type?: string,
    public action?: string,
    public name?: string,
    public path?: string,
    public source?: string,
    public quota?: string,
    public advisoryQuota?: string,
    public requestUser?: string,
    public requestDate?: dayjs.Dayjs,
    public requestStatus?: string,
    public statusChangedDate?: dayjs.Dayjs,
    public previousStatus?: string
  ) {}
}

export function getMaprRequestsIdentifier(maprRequests: IMaprRequests): string | undefined {
  return maprRequests.id;
}
