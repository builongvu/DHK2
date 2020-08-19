import { FormGroup } from "@angular/forms";

// export function forbiddenNameValidator(nameRe: RegExp): ValidatorFn {
//     return (control: AbstractControl): { [key: string]: any } | null => {
//         const forbidden = nameRe.test(control.value);
//         return forbidden ? { 'forbiddenName': { value: control.value } } : null;
//     };
// }

export function MustMatch(controlName: string, matchingControlName: string) {
  return (formGroup: FormGroup) => {
    const control = formGroup.controls[controlName];
    const matchingControl = formGroup.controls[matchingControlName];

    if (matchingControl.errors && !matchingControl.errors.mustMatch) {
      // return if another validator has already found an error on the matchingControl
      return;
    }

    // set error on matchingControl if validation fails
    if (control.value !== matchingControl.value) {
      matchingControl.setErrors({ mustMatch: true });
    } else {
      matchingControl.setErrors(null);
    }
  };
}

export function RequireCombo(controlName: string) {
  return (formGroup: FormGroup) => {
    const control = formGroup.controls[controlName];

    // set error on matchingControl if validation fails
    if (control.value == -1) {
      control.setErrors({ requireCombo: true });
    } else {
      control.setErrors(null);
    }
  };
}

export function EndDateAfterOrEqualValidator(
  startDate: string,
  endDate: string
) {
  return (formGroup: FormGroup) => {
    const startDateTimestamp = Date.parse(formGroup.controls[startDate].value);
    const endDateTimestamp = Date.parse(formGroup.controls[endDate].value);

    const matchingEnd = formGroup.controls[endDate];
    const matchingStart = formGroup.controls[startDate];

    if (matchingEnd.errors && !matchingEnd.errors.endDateLessThanStartDate) {
      return;
    }

    if (
      matchingStart.errors &&
      !matchingStart.errors.endDateLessThanStartDate
    ) {
      return;
    }

    if (endDateTimestamp < startDateTimestamp) {
      matchingStart.setErrors({ endDateLessThanStartDate: true });
    } else {
      matchingStart.setErrors(null);
    }
  };
}

export function codeConflict(codeFormControl: string, conflict: string) {
  return (formGroup: FormGroup) => {
    const serviceCodeForm = formGroup.controls[codeFormControl];
    const serviceCode = formGroup.controls[codeFormControl].value;
    const conflictCode = formGroup.controls[conflict].value;

    if (serviceCodeForm.errors && !serviceCodeForm.errors.codeConflict) {
      return;
    }

    if (serviceCode === conflictCode) {
      serviceCodeForm.setErrors({ codeConflict: true });
    } else {
      serviceCodeForm.setErrors(null);
    }
  };



  // set error on matchingControl if validation fails
}
