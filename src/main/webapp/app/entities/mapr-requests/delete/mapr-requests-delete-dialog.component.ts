import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMaprRequests } from '../mapr-requests.model';
import { MaprRequestsService } from '../service/mapr-requests.service';

@Component({
  templateUrl: './mapr-requests-delete-dialog.component.html',
})
export class MaprRequestsDeleteDialogComponent {
  maprRequests?: IMaprRequests;

  constructor(protected maprRequestsService: MaprRequestsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.maprRequestsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
