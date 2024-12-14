<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="150" height="150">
</p>

<div align="center">

### Hook !
##### Link Management Application
<br>
<img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=Android&logoColor=white"> 
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white"> 
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"> 
</div>

---

## Features
- 🌟 Save Links (Hook)
- 📂 Organize and Manage Hooks by Topics
- 📝 Provide Annotation Feature for Each Hook
---

## Tech Stacks

| **Category** | **Technology** |
|--------------|----------------|
| Architecture | MVVM, Android App Architecture |
| UI           | XML, Material 3 |
| Database     | Room DAO       |
| Navigation   | Jetpack Navigation |
| Language     | Kotlin         |
| Concurrency  | Coroutines     |
| App Design   | Modularization |

---

# Android Architecture Overview

```
[UI] ↔ [ViewModel] ↔ [Repository] ↔ [DAO] ↔ [Database]
```

---

## Components

### **UI**
- Represents the visual layer (e.g., `Activity` or `Fragment`).
- Directly interacts with the `ViewModel`.

### **ViewModel**
- Acts as the bridge between the UI and data layers.
- Requests data from the `Repository` and prepares it for display.
- Maintains data during configuration changes (e.g., screen rotation).

### **Repository**
- Manages data sources and provides a clean API for data access.
- Mediates between `DAO` (local database) and remote sources (e.g., APIs).

### **DAO (Data Access Object)**
- Defines methods to access the database.
- Abstracts SQL queries into method calls.

### **Database**
- Local data storage, typically implemented with Room.
- Stores app data persistently.

---

## Data Flow

1. **UI → ViewModel**
   - User actions (e.g., button clicks) trigger requests to the `ViewModel`.

2. **ViewModel → Repository**
   - The `ViewModel` forwards data requests to the `Repository`.

3. **Repository → DAO**
   - The `Repository` calls the `DAO` to fetch or update data in the database.

4. **DAO → Database**
   - The `DAO` performs the actual database operations.

5. **Repository → ViewModel**
   - The `Repository` processes the data (e.g., applying business logic) and returns it to the `ViewModel`.

6. **ViewModel → UI**
   - The `ViewModel` provides the processed data to the UI for rendering.

---

## Benefits
- **Separation of Concerns**: Clear distinction between UI, business logic, and data layers.
- **Scalability**: Easy to extend or modify each layer independently.
- **Testability**: Each component can be unit-tested in isolation.

---

## Diagram

```mermaid
graph TD
    UI["UI (Activity/Fragment)"] -->|"User Actions"| ViewModel
    ViewModel -->|"Data Requests"| Repository
    Repository -->|"Access Database"| DAO
    DAO -->|"SQL Operations"| Database
    Database -->|"Fetch/Store Data"| DAO
    DAO -->|"Processed Data"| Repository
    Repository -->|"Provide Data"| ViewModel
    ViewModel -->|"Update UI"| UI
```

---

## Example Use Case
1. A user clicks a button on the UI.
2. The `ViewModel` captures the event and requests data from the `Repository`.
3. The `Repository` fetches data from the local database using the `DAO`.
4. The database returns the requested data to the `DAO`.
5. The `Repository` processes the data and sends it back to the `ViewModel`.
6. The `ViewModel` updates the UI with the retrieved data.

---

This modular architecture ensures a clean and maintainable codebase, adhering to the MVVM (Model-View-ViewModel) design pattern.
