import * as dayjs from 'dayjs';

export interface IMaprRequests {
  id?: string;
  type?: string | null;
  action?: string | null;
  name?: string | null;
  path?: string | null;
  requestUser?: string | null;
  requestDate?: dayjs.Dayjs | null;
  status?: string | null;
  statusDate?: dayjs.Dayjs | null;
}

export class MaprRequests implements IMaprRequests {
  constructor(
    public id?: string,
    public type?: string | null,
    public action?: string | null,
    public name?: string | null,
    public path?: string | null,
    public requestUser?: string | null,
    public requestDate?: dayjs.Dayjs | null,
    public status?: string | null,
    public statusDate?: dayjs.Dayjs | null
  ) {}
}

export function getMaprRequestsIdentifier(maprRequests: IMaprRequests): string | undefined {
  return maprRequests.id;
}
export function okToDelete(maprRequests: IMaprRequests): boolean {
  if (maprRequests === 'undefined') {
    if (maprRequests.status === 'CREATED') {
      return true;
    } else {
      return false;
    }
  } else {
    return false;
  }
}
