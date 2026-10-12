// Experiment 6: DOM Selection, Modification, Event Listeners, and setInterval Timer

document.addEventListener("DOMContentLoaded", () => {
  // DOM Selection
  const timerDisplay = document.getElementById("timerDisplay");
  const startBtn = document.getElementById("startBtn");
  const stopBtn = document.getElementById("stopBtn");
  const resetBtn = document.getElementById("resetBtn");

  const noticeInput = document.getElementById("noticeInput");
  const addNoticeBtn = document.getElementById("addNoticeBtn");
  const noticeList = document.getElementById("noticeList");

  // Timer Variables
  let secondsElapsed = 0;
  let timerId = null;

  function formatTime(totalSeconds) {
    const hrs = String(Math.floor(totalSeconds / 3600)).padStart(2, "0");
    const mins = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, "0");
    const secs = String(totalSeconds % 60).padStart(2, "0");
    return `${hrs}:${mins}:${secs}`;
  }

  // 1. Start Timer (setInterval)
  startBtn.addEventListener("click", () => {
    if (timerId !== null) return;

    timerId = setInterval(() => {
      secondsElapsed++;
      // DOM Content & Style update
      timerDisplay.textContent = formatTime(secondsElapsed);
      timerDisplay.style.color = secondsElapsed % 2 === 0 ? "#4f46e5" : "#0ea5e9";
    }, 1000);

    startBtn.disabled = true;
    stopBtn.disabled = false;
  });

  // 2. Stop/Pause Timer (clearInterval)
  stopBtn.addEventListener("click", () => {
    if (timerId !== null) {
      clearInterval(timerId);
      timerId = null;
      startBtn.disabled = false;
      stopBtn.disabled = true;
      timerDisplay.style.color = "#f59e0b"; // Warning amber on pause
    }
  });

  // 3. Reset Timer
  resetBtn.addEventListener("click", () => {
    if (timerId !== null) {
      clearInterval(timerId);
      timerId = null;
    }
    secondsElapsed = 0;
    timerDisplay.textContent = "00:00:00";
    timerDisplay.style.color = "#4f46e5";
    startBtn.disabled = false;
    stopBtn.disabled = true;
  });

  // 4. Element Creation: createElement and appendChild
  addNoticeBtn.addEventListener("click", () => {
    const text = noticeInput.value.trim();
    if (!text) return;

    const li = document.createElement("li");

    const span = document.createElement("span");
    span.textContent = text;

    const delBtn = document.createElement("button");
    delBtn.className = "btn-del";
    delBtn.textContent = "Remove";

    // 5. Element Removal: remove()
    delBtn.addEventListener("click", () => {
      li.remove();
    });

    li.appendChild(span);
    li.appendChild(delBtn);
    noticeList.appendChild(li);

    noticeInput.value = "";
    noticeInput.focus();
  });

  // Attach delete handlers to existing items
  document.querySelectorAll(".btn-del").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.target.closest("li").remove();
    });
  });
});
