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

@Component({
  selector: 'jhi-mapr-requests-update',
  templateUrl: './mapr-requests-update.component.html',
})
export class MaprRequestsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required, Validators.minLength(3)]],
    action: [null, [Validators.required, Validators.minLength(3)]],
    name: [null, [Validators.required, Validators.minLength(3)]],
    path: [null, [Validators.required, Validators.minLength(3)]],
    source: [null, [Validators.required, Validators.minLength(3)]],
    quota: [null, [Validators.required, Validators.minLength(3)]],
    advisoryQuota: [null, [Validators.required, Validators.minLength(3)]],
    requestUser: [null, [Validators.required, Validators.minLength(3)]],
    requestDate: [null, [Validators.required]],
    requestStatus: [null, [Validators.required, Validators.minLength(3)]],
    statusChangedDate: [null, [Validators.required]],
    previousStatus: [null, [Validators.required, Validators.minLength(3)]],
  });

  constructor(protected maprRequestsService: MaprRequestsService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ maprRequests }) => {
      if (maprRequests.id === undefined) {
        const today = dayjs().startOf('day');
        maprRequests.requestDate = today;
        maprRequests.statusChangedDate = today;
      }

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
      id: maprRequests.id,
      type: maprRequests.type,
      action: maprRequests.action,
      name: maprRequests.name,
      path: maprRequests.path,
      source: maprRequests.source,
      quota: maprRequests.quota,
      advisoryQuota: maprRequests.advisoryQuota,
      requestUser: maprRequests.requestUser,
      requestDate: maprRequests.requestDate ? maprRequests.requestDate.format(DATE_TIME_FORMAT) : null,
      requestStatus: maprRequests.requestStatus,
      statusChangedDate: maprRequests.statusChangedDate ? maprRequests.statusChangedDate.format(DATE_TIME_FORMAT) : null,
      previousStatus: maprRequests.previousStatus,
    });
  }

  protected createFromForm(): IMaprRequests {
    return {
      ...new MaprRequests(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      action: this.editForm.get(['action'])!.value,
      name: this.editForm.get(['name'])!.value,
      path: this.editForm.get(['path'])!.value,
      source: this.editForm.get(['source'])!.value,
      quota: this.editForm.get(['quota'])!.value,
      advisoryQuota: this.editForm.get(['advisoryQuota'])!.value,
      requestUser: this.editForm.get(['requestUser'])!.value,
      requestDate: this.editForm.get(['requestDate'])!.value
        ? dayjs(this.editForm.get(['requestDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      requestStatus: this.editForm.get(['requestStatus'])!.value,
      statusChangedDate: this.editForm.get(['statusChangedDate'])!.value
        ? dayjs(this.editForm.get(['statusChangedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      previousStatus: this.editForm.get(['previousStatus'])!.value,
    };
  }
}
