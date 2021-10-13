import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMaprRequests } from '../mapr-requests.model';

@Component({
  selector: 'jhi-mapr-requests-detail',
  templateUrl: './mapr-requests-detail.component.html',
})
export class MaprRequestsDetailComponent implements OnInit {
  maprRequests: IMaprRequests | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ maprRequests }) => {
      this.maprRequests = maprRequests;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
