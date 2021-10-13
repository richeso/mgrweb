import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MaprRequestsComponent } from './list/mapr-requests.component';
import { MaprRequestsDetailComponent } from './detail/mapr-requests-detail.component';
import { MaprRequestsUpdateComponent } from './update/mapr-requests-update.component';
import { MaprRequestsDeleteDialogComponent } from './delete/mapr-requests-delete-dialog.component';
import { MaprRequestsRoutingModule } from './route/mapr-requests-routing.module';

@NgModule({
  imports: [SharedModule, MaprRequestsRoutingModule],
  declarations: [MaprRequestsComponent, MaprRequestsDetailComponent, MaprRequestsUpdateComponent, MaprRequestsDeleteDialogComponent],
  entryComponents: [MaprRequestsDeleteDialogComponent],
})
export class MaprRequestsModule {}
