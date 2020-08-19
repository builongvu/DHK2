import {Component, OnInit} from '@angular/core';
import {languages} from './languages.model';
import {TranslateService} from '@ngx-translate/core';
import {config} from '../../config/application.config';

@Component({
  selector: 'sa-language-selector',
  templateUrl: './language-selector.component.html',

})
export class LanguageSelectorComponent implements OnInit {

  public languages: Array<any>;
  public currentLanguage: any;

  constructor(private translate: TranslateService) {
  }

  ngOnInit() {
    this.languages = languages;
    this.languages.forEach(item => {
      if (item.key == this.translate.currentLang) {
        this.currentLanguage = item;
      }
    });
  }

  setLanguage(language) {
    if (this.currentLanguage != undefined && this.currentLanguage.key == language.key) {
      return;
    }
    this.currentLanguage = language;
    localStorage.setItem(config.user_default_language, language.key);
    // this.translate.use(language.key);
    window.location.reload();
  }

}
