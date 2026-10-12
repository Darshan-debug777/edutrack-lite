// External JavaScript for Experiment 4

document.addEventListener("DOMContentLoaded", () => {
  const marksInput = document.getElementById("marksInput");
  const updateBtn = document.getElementById("updateBtn");
  const toggleThemeBtn = document.getElementById("toggleThemeBtn");
  const displayMarks = document.getElementById("displayMarks");
  const studentStatusBadge = document.getElementById("studentStatusBadge");
  const resultMessage = document.getElementById("resultMessage");
  const card = document.querySelector(".card");

  // Update marks and calculate grade
  updateBtn.addEventListener("click", () => {
    const marks = parseFloat(marksInput.value);

    if (isNaN(marks) || marks < 0 || marks > 100) {
      resultMessage.style.color = "#ef4444";
      resultMessage.textContent = "Error: Marks must be a valid number between 0 and 100.";
      return;
    }

    displayMarks.textContent = `${marks} / 100`;

    if (marks >= 75) {
      studentStatusBadge.textContent = "Distinction";
      studentStatusBadge.style.backgroundColor = "#ecfdf5";
      studentStatusBadge.style.color = "#065f46";
      resultMessage.style.color = "#10b981";
      resultMessage.textContent = `Grade: A+ (First Class with Distinction). Percentage: ${marks}%.`;
    } else if (marks >= 50) {
      studentStatusBadge.textContent = "Passed";
      studentStatusBadge.style.backgroundColor = "#fffbeb";
      studentStatusBadge.style.color = "#92400e";
      resultMessage.style.color = "#f59e0b";
      resultMessage.textContent = `Grade: B (Second Class Pass). Percentage: ${marks}%.`;
    } else {
      studentStatusBadge.textContent = "Needs Attention";
      studentStatusBadge.style.backgroundColor = "#fef2f2";
      studentStatusBadge.style.color = "#991b1b";
      resultMessage.style.color = "#ef4444";
      resultMessage.textContent = `Grade: F (Failed). Percentage: ${marks}%. Remedial classes recommended.`;
    }
  });

  // Toggle Dark Theme on Card
  toggleThemeBtn.addEventListener("click", () => {
    card.classList.toggle("dark-theme");
  });
});
