import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMaprRequests } from '../mapr-requests.model';
import { MaprRequestsService } from '../service/mapr-requests.service';
import { MaprRequestsDeleteDialogComponent } from '../delete/mapr-requests-delete-dialog.component';
import { MaprRequestsDownloadDialogComponent } from '../download/mapr-requests-download-dialog.component';
@Component({
  selector: 'jhi-mapr-listvolumes',
  templateUrl: './mapr-listvolumes.component.html',
})
export class MaprListVolumesComponent implements OnInit {
  maprRequests?: IMaprRequests[];
  isLoading = false;

  constructor(protected maprRequestsService: MaprRequestsService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.maprRequestsService.listvolumes().subscribe(
      (res: HttpResponse<IMaprRequests[]>) => {
        this.isLoading = false;
        this.maprRequests = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IMaprRequests): string {
    return item.id!;
  }

  download(maprRequests: IMaprRequests): void {
    const modalRef = this.modalService.open(MaprRequestsDownloadDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.maprRequests = maprRequests;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  delete(maprRequests: IMaprRequests): void {
    const modalRef = this.modalService.open(MaprRequestsDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.maprRequests = maprRequests;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
