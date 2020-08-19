import {Component, NgZone, OnInit} from '@angular/core';
import { OauthService } from '../../services/oauth.service';
import { Router } from '@angular/router';
import { config } from '../../config/application.config';
import { first } from 'rxjs/operators';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {environment} from '../../../environments/environment';

@Component({
  selector: 'app-dashboard',
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.scss']
})
export class LoginComponent implements OnInit {
  isLoginFail = false;
  submitted = false;
  isSucessfull = false;
  isUserActive = false;
  isPassActive = false;
  loginForm: FormGroup;
  loginSuccessful = true;

  constructor(private router: Router, private oauthService: OauthService, private fb: FormBuilder,
              private zone: NgZone) {
  }

  ngOnInit() {
    this.createForm();
  }

  createForm() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
// username: new FormControl('', [Validators.required, Validators.email]),
    });
  }

  login(username: string, password: string) {
    this.oauthService.login(username, password).pipe(first())
      .subscribe(
        data => {
          this.loginSuccessful = false;
          setTimeout(() => {
            this.zone.run(() => {
              this.isLoginFail = false;
              this.loginSuccessful = true;
              this.router.navigate([data.defaulLink]);
            });
            console.log(this.loginSuccessful, ' login');
          }, 1200);

        },
        error => {
// this.router.navigate(['/login']);
          this.isLoginFail = true;
          this.loginSuccessful = false;
          setTimeout(() => {
            this.loginSuccessful = true;
          }, 500);
        }, () => {
          this.isLoginFail = false;
          setTimeout(() => {
            this.isSucessfull = true;
          }, 800);
        }
      );
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;
// stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }
    this.zone.run(() => {
      this.login(this.loginForm.value.username, this.loginForm.value.password);
    });
  }

  userActive() {
    this.isUserActive = true;
  }

  userBlur() {
    this.isUserActive = false;
  }

  passActive() {
    this.isPassActive = true;
  }

  passBlur() {
    this.isPassActive = false;
  }

  preChangePass() {
    window.open(environment.url_vsa_change_passwd);
  }
}
