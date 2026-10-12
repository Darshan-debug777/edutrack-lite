# Experiment 7: React Components using `useState` & `useEffect`

## Objective
To demonstrate core React hooks: `useState` for local component state management (handling attendance records and calculations) and `useEffect` for lifecycle side-effects (setting up and cleaning up timer intervals).

## Source File
- [`index.html`](index.html) — Standalone React component using Babel.

## Concepts Demonstrated
1. **`useState`**:
   - `const [attended, setAttended] = useState(...)`
   - Functional state updates: `setAttended((prev) => prev + 1)`
2. **`useEffect`**:
   - Mounting side-effects with empty dependency array `[]`.
   - Guaranteed cleanup function returning `clearInterval(interval)`.
3. **Derived State**: Calculating attendance percentage on each re-render without redundant state.

## How to Run
Open `index.html` in any web browser or execute:
```powershell
Start-Process "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC\experiments\exp7_react_state_effect\index.html"
```
