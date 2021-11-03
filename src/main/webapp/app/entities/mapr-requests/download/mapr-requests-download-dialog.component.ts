import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IMaprRequests } from '../mapr-requests.model';
import { MaprRequestsService } from '../service/mapr-requests.service';
import * as fileSaver from 'file-saver';

@Component({
  templateUrl: './mapr-requests-download-dialog.component.html',
})
export class MaprRequestsDownloadDialogComponent {
  maprRequests?: IMaprRequests;

  constructor(protected maprRequestsService: MaprRequestsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDownload(id: string): void {
    //  this.maprRequestsService.download(id).subscribe(() => {
    //   this.activeModal.close('downloaded');
    // });
    // this.maprRequestsService.download(id).subscribe(response => {

    //  const url: string | null = response.url;
    //  if (url) {
    //    window.open(url);
    //  }
    //  this.activeModal.dismiss();

    this.maprRequestsService.download(id).subscribe((response: any) => {
      //when you use stricter type checking
      const blob: any = new Blob([response.body], { type: 'application/octet-stream' });
      const url = window.URL.createObjectURL(blob);
      //window.open(url);
      //window.location.href = response.url;
      fileSaver.saveAs(blob, 'tempfile.csv');
      this.activeModal.dismiss();
      //}), error => console.log('Error downloading the file'),
    }),
      (error: any) => {
        window.console.log('Error downloading the file');
      }, //when you use stricter type checking
      () => {
        window.console.info('File downloaded successfully');
      };
  }
}
