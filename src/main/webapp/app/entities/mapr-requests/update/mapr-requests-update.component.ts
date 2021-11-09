import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IMaprRequests, MaprRequests } from '../mapr-requests.model';
import { MaprRequestsService } from '../service/mapr-requests.service';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { AlertService, Alert } from 'app/core/util/alert.service';

@Component({
  selector: 'jhi-mapr-requests-update',
  templateUrl: './mapr-requests-update.component.html',
})
export class MaprRequestsUpdateComponent implements OnInit {
  isSaving = false;
  account: Account | null = null;
  alerts: Alert[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
    path: [null, [Validators.required, Validators.minLength(3)]],
    extraProperties: this.fb.group({
      advisoryquota: ['0', Validators.required],
      quota: ['0', Validators.required],
    }),
  });

  constructor(
    protected maprRequestsService: MaprRequestsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder,
    protected accountService: AccountService,
    protected alertService: AlertService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ maprRequests }) => {
      if (maprRequests.id === undefined) {
        const today = dayjs().startOf('day');
        //       maprRequests.requestDate = today;
        //       maprRequests.statusDate = today;
        //       maprRequests.action = "create volume";
        //        maprRequests.type = "volume create";
      }

      this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
      this.updateForm(maprRequests);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const maprRequests = this.createFromForm();
    if (maprRequests.id !== undefined) {
      this.subscribeToSaveResponse(this.maprRequestsService.update(maprRequests));
    } else {
      this.subscribeToSaveResponse(this.maprRequestsService.create(maprRequests));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMaprRequests>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(maprRequests: IMaprRequests): void {
    this.editForm.patchValue({
      name: maprRequests.name,
      path: maprRequests.path,
      extraProperties: maprRequests.extraProperties,
    });
  }

  protected createFromForm(): IMaprRequests {
    return {
      ...new MaprRequests(),
      name: this.editForm.get(['name'])!.value,
      path: this.editForm.get(['path'])!.value,
      extraProperties: this.editForm.get(['extraProperties'])!.value,
    };
  }
}
