# Experiment 5: Registration Form Validation

## Objective
To demonstrate client-side form validation for required fields, email format verification using Regular Expressions, and 10-digit mobile number validation before submission.

## Source Files
- [`registration_validation.html`](registration_validation.html) — Registration form layout.
- [`validator.js`](validator.js) — Validation logic using event listeners (`blur`, `submit`) and regex testing.

## Concepts Demonstrated
1. **Required Field Checks**: Ensures Student ID and Name are not blank.
2. **Email Regex**: `/^[^\s@]+@[^\s@]+\.[^\s@]+$/` validates domain structure.
3. **Mobile Regex**: `/^[6-9]\d{9}$/` ensures exactly 10 digits beginning with permitted telecom prefixes.
4. **Dynamic Feedback**: Real-time error messages, `.invalid` and `.valid` input borders, and success banner display.

## How to Run
Open in any browser or run:
```powershell
Start-Process "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC\experiments\exp5_form_validation\registration_validation.html"
```
