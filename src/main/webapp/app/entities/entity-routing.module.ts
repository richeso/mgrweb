import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'mapr-requests',
        data: { pageTitle: 'mgrwebApp.maprRequests.home.title' },
        loadChildren: () => import('./mapr-requests/mapr-requests.module').then(m => m.MaprRequestsModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
