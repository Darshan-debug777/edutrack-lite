# Experiment 8: Props, State, Reusable Components & React Router

## Objective
To demonstrate component reusability, parent-to-child data flow via `props`, upward communication through callback events, client-side routing, and centralized state synchronization.

## Source File
- [`index.html`](index.html) — Standalone React application with reusable `CourseCard` and route navigation.

## Concepts Demonstrated
1. **Props (Read-only data flow)**: Passing `code`, `title`, `credits`, `instructor`, and callback `onToggleEnroll` into `CourseCard`.
2. **State Management**: Managing active route and array of enrolled course identifiers.
3. **Reusable Component**: Single `CourseCard` rendered in both Catalog view and Enrolled Courses view.
4. **Client-side Routing**: Switching views dynamically based on route state without page reload.

## How to Run
Open in any browser or run:
```powershell
Start-Process "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC\experiments\exp8_props_router\index.html"
```
