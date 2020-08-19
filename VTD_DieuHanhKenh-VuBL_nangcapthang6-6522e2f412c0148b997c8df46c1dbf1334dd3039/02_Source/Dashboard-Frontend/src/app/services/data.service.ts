import {BehaviorSubject} from 'rxjs';
import {Injectable} from '@angular/core';

@Injectable()
export class DataService {
  private messageSource = new BehaviorSubject<boolean>(true);
  currentMessage = this.messageSource.asObservable();

  constructor() {
  }

  changeMessage(message: boolean) {
    this.messageSource.next(message);
  }
}
