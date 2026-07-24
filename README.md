<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" width="120" alt="Hook App Icon">
</p>

<h1 align="center">Hook</h1>

<p align="center">
Save links instantly. Organize with tags. Find them anytime.
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.hanto.hook">
    <img src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg" width="180" alt="Google Play">
  </a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=Android&logoColor=white">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white">
</p>

---

# Overview

Hook is a bookmark manager that lets you save links directly from the Android Share Sheet.

Instead of losing links in chats, notes, or browser bookmarks, Hook keeps them organized with tags, notes, and fast search.

---

# Screenshots

<p align="center">
  <img src="preview/hook-screenshot-1_eng.png" width="24%">
  <img src="preview/hook-screenshot-2_eng.png" width="24%">
  <img src="preview/hook-screenshot-4_eng.png" width="24%">
  <img src="preview/hook-screenshot-5_eng.png" width="24%">
</p>

---

# Features

- 🔗 Save links directly from Android Share Sheet
- 🖼️ Automatically fetch webpage metadata & thumbnails
- 🏷️ Organize bookmarks with tags
- 📝 Add notes to saved links
- 📌 Pin important links

---

# Technical Highlights

- Clean Architecture + MVVM
- UseCase-based business logic
- Room + Flow reactive data handling
- Jsoup-based Open Graph metadata parsing
- Debounce + in-memory search optimization
- Hilt dependency injection

---

# Tech Stack

| Category | Stack |
|----------|-------|
| Language | Kotlin |
| Architecture | Clean Architecture, MVVM |
| Async | Coroutines, Flow |
| UI | XML, ViewBinding, Material 3 |
| Database | Room |
| DI | Hilt |
| Network | Retrofit, OkHttp |
| Parsing | Jsoup |
| Image | Glide |
| Test | JUnit4, MockK, Turbine |

---

# Architecture

```
Presentation
     │
     ▼
Domain (UseCases)
     │
     ▼
Data (Repository)
     ├── Room
     └── Retrofit / Jsoup
```

---

# Project Structure

```
app
├── presentation
├── domain
├── data
├── di
└── common
```

---

# License

This project is licensed under the MIT License.
