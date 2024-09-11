import { Component, OnInit } from '@angular/core';
import { IAuthResponse, IUser } from '../models/user.model';
import { GeneralService } from '../services/general.service';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username = '';
  password = '';

  constructor(private generalService: GeneralService) { }

  ngOnInit(): void {
  }

  login(): void {
    const loginData: IUser = {
      id: undefined,
      login: this.username,
      password: this.password
    }
    this.generalService.login(loginData).subscribe((res: HttpResponse<IAuthResponse>) => {
      const auth = res.body;
      if(auth) {
        localStorage.setItem('jwtToken', auth.token); 
      }
      
    });
  }

}
