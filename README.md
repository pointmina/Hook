<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="150" height="150">
</p>

<div align="center">

### Hook !
##### Link Management Application

<img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=Android&logoColor=white"> 
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white"> 
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"> 
<br>
<br>

[![Google Play Store](https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg)](https://play.google.com/store/apps/details?id=com.hanto.hook)

</div>
---

## Features
- 🔗 Save Links (Hook)
- 📂 Organize and Manage Hooks by Topics
- 📝 Provide Annotation Feature for Each Hook
- 📲 Shared links from other apps to Hook

  
---

## Preview
![프리뷰 영어1](https://github.com/user-attachments/assets/6104adaa-b811-4320-87fe-e7e24b899841)
![프리뷰 영어2](https://github.com/user-attachments/assets/b65c4380-18e3-4f1c-95ac-a724abb075a8)



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


## Branch Strategy
The branch strategy for this project follows a simplified Git Flow model, managed using **Sourcetree**.

### Main Branches
- **main**: Contains the production-ready code.
- **work**: Used for integrating features and staging before merging into `main`.


# Android Architecture Overview


This modular architecture ensures a clean and maintainable codebase, adhering to the MVVM (Model-View-ViewModel) design pattern.

```
[UI] ↔ [ViewModel] ↔ [Repository] ↔ [DAO] ↔ [Database]
```

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


