jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MaprRequestsService } from '../service/mapr-requests.service';
import { IMaprRequests, MaprRequests } from '../mapr-requests.model';

import { MaprRequestsUpdateComponent } from './mapr-requests-update.component';

describe('Component Tests', () => {
  describe('MaprRequests Management Update Component', () => {
    let comp: MaprRequestsUpdateComponent;
    let fixture: ComponentFixture<MaprRequestsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let maprRequestsService: MaprRequestsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [MaprRequestsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(MaprRequestsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MaprRequestsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      maprRequestsService = TestBed.inject(MaprRequestsService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const maprRequests: IMaprRequests = { id: 'CBA' };

        activatedRoute.data = of({ maprRequests });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(maprRequests));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<MaprRequests>>();
        const maprRequests = { id: 'ABC' };
        jest.spyOn(maprRequestsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ maprRequests });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: maprRequests }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(maprRequestsService.update).toHaveBeenCalledWith(maprRequests);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<MaprRequests>>();
        const maprRequests = new MaprRequests();
        jest.spyOn(maprRequestsService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ maprRequests });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: maprRequests }));
        saveSubject.complete();

        // THEN
        expect(maprRequestsService.create).toHaveBeenCalledWith(maprRequests);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<MaprRequests>>();
        const maprRequests = { id: 'ABC' };
        jest.spyOn(maprRequestsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ maprRequests });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(maprRequestsService.update).toHaveBeenCalledWith(maprRequests);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
