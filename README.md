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
- 🔗 Save Links (Hook)
- 📂 Organize and Manage Hooks by Topics
- 📝 Provide Annotation Feature for Each Hook
- 📲 Shared links from other apps to Hook

  
---

## Preview



![hook2](https://github.com/user-attachments/assets/b7bc90d8-b7a3-4be4-8d69-93d337cc8a82) 



https://github.com/user-attachments/assets/61d1702f-3cc8-4e6d-9a07-811b828d3ef3




## Tech Stacks

| **Category**    | **Technology**           |
|-----------------|--------------------------|
| Language        | Kotlin                   |
| Architecture    | MVVM, Android App Architecture |
| UI              | XML, Material 3          |
| Concurrency     | Coroutines               |
| Database        | Room DAO                 |
| Navigation      | Jetpack Navigation       |
| App Design      | Modularization           |

🔧Paging

---

# Android Architecture Overview


This modular architecture ensures a clean and maintainable codebase, adhering to the MVVM (Model-View-ViewModel) design pattern.

```
[UI] ↔ [ViewModel] ↔ [Repository] ↔ [DAO] ↔ [Database]
```

ref.   
https://ppeper.github.io/android/repository-pattern/
https://medium.com/prnd/mvvm%EC%9D%98-viewmodel%EC%97%90%EC%84%9C-%EC%9D%B4%EB%B2%A4%ED%8A%B8%EB%A5%BC-%EC%B2%98%EB%A6%AC%ED%95%98%EB%8A%94-%EB%B0%A9%EB%B2%95-6%EA%B0%80%EC%A7%80-31bb183a88ce


---

## Branch Strategy
The branch strategy for this project follows a simplified Git Flow model, managed using **Sourcetree**.

### Main Branches
- **main**: Contains the production-ready code.
- **work**: Used for integrating features and staging before merging into `main`.

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


