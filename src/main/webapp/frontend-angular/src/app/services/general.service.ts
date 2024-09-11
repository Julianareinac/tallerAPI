import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IAuthResponse, IUser } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class GeneralService {
  private resourceUrl = 'http://localhost:8080/api/user';
  private resourceLoginUrl = 'http://localhost:8080/api/auth/login';

  constructor(private http: HttpClient) {}

  createUser(user: IUser): Observable<HttpResponse<IUser>> {
    return this.http.post<IUser>(this.resourceUrl, user, { observe: 'response' });
  }

  login(user: IUser): Observable<HttpResponse<IAuthResponse>> {
    return this.http.post<IAuthResponse>(this.resourceLoginUrl, user, { observe: 'response' });
  }
}
