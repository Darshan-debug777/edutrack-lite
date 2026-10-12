// Experiment 5: Form Validation Logic

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("regForm");
  const studentId = document.getElementById("studentId");
  const fullName = document.getElementById("fullName");
  const email = document.getElementById("email");
  const mobile = document.getElementById("mobile");
  const successBanner = document.getElementById("successBanner");

  // Regex rules
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const mobileRegex = /^[6-9]\d{9}$/; // 10-digit standard mobile number

  const validateField = (input, errorDiv, condition, errorMsg) => {
    if (!condition) {
      input.classList.add("invalid");
      input.classList.remove("valid");
      errorDiv.textContent = errorMsg;
      return false;
    } else {
      input.classList.remove("invalid");
      input.classList.add("valid");
      errorDiv.textContent = "";
      return true;
    }
  };

  // Real-time blur validation
  studentId.addEventListener("blur", () => {
    validateField(studentId, document.getElementById("studentIdErr"), studentId.value.trim().length >= 3, "Student ID must be at least 3 characters.");
  });

  fullName.addEventListener("blur", () => {
    validateField(fullName, document.getElementById("fullNameErr"), fullName.value.trim().length >= 2, "Full name must be at least 2 characters.");
  });

  email.addEventListener("blur", () => {
    validateField(email, document.getElementById("emailErr"), emailRegex.test(email.value.trim()), "Invalid email format (e.g. name@domain.com).");
  });

  mobile.addEventListener("blur", () => {
    validateField(mobile, document.getElementById("mobileErr"), mobileRegex.test(mobile.value.trim()), "Mobile number must be exactly 10 digits starting with 6-9.");
  });

  // Form submit event
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    successBanner.style.display = "none";

    const v1 = validateField(studentId, document.getElementById("studentIdErr"), studentId.value.trim().length >= 3, "Student ID is required (min 3 chars).");
    const v2 = validateField(fullName, document.getElementById("fullNameErr"), fullName.value.trim().length >= 2, "Full name is required.");
    const v3 = validateField(email, document.getElementById("emailErr"), emailRegex.test(email.value.trim()), "Please enter a valid email address.");
    const v4 = validateField(mobile, document.getElementById("mobileErr"), mobileRegex.test(mobile.value.trim()), "Please enter a valid 10-digit mobile number.");

    if (v1 && v2 && v3 && v4) {
      successBanner.textContent = `✓ Form Validated Successfully! Registered Student: ${fullName.value.trim()} (${studentId.value.trim()})`;
      successBanner.style.display = "block";
      form.reset();
      [studentId, fullName, email, mobile].forEach((el) => el.classList.remove("valid"));
    }
  });
});
