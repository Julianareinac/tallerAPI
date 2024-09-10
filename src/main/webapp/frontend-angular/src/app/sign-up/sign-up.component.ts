import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GeneralService } from '../services/general.service';
import { HttpResponse } from '@angular/common/http';
import { IUser, User } from '../models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css'],
})
export class SignUpComponent implements OnInit {
  editForm = this.fb.group({
    id: [],
    login: ['', Validators.required],
    password: ['', Validators.required],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    activated: [false, Validators.required],
    langKey: ['', Validators.required],
  });

  constructor(
    private fb: FormBuilder,
    private generalService: GeneralService,
    private router: Router
  ) {}

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.editForm.valid) {
      const user = this.createFromForm();
      this.generalService
        .createUser(user)
        .subscribe((res: HttpResponse<IUser>) => {
          this.router.navigate(['/login']);
        });
    }
  }

  protected createFromForm(): IUser {
    return {
      ...new User(),
      id: this.editForm.get(['id'])!.value ?? undefined,
      login: this.editForm.get(['login'])!.value,
      password: this.editForm.get(['login'])!.value,
      firstName: this.editForm.get(['firstName'])!.value,
      lastName: this.editForm.get(['lastName'])!.value,
      email: this.editForm.get(['email'])!.value,
      activated: this.editForm.get(['activated'])!.value,
      langKey: this.editForm.get(['langKey'])!.value,
    };
  }
}
