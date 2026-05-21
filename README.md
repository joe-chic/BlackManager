# BlackManager

BlackManager is a premium, native Android task and project management application built in Kotlin. It combines the power of a highly relational offline database using Room with seamless authentication and integration via the Notion API.

---

## 🚀 Key Features

*   **Offline-First Architecture:** Powered by a relational local SQLite database via Jetpack Room, offering complete offline capability.
*   **Notion Integration:** Secure token-based authentication via OkHttp directly validating credentials against the official Notion API (`/v1/users/me`).
*   **Rich Task Management:** 
    *   Full CRUD lifecycle support for tasks and projects.
    *   Subtask hierarchy support (parent-child task relationships).
    *   Flexible metadata including start dates, deadlines, custom statuses ("Sin empezar", "En curso", "Listo"), and priorities ("Baja", "Media", "Alta").
*   **Dynamic Tagging & Assignees:** Many-to-many relationship mapping enabling users to assign multiple team members and filter tags to tasks using interactive Material Chip elements.
*   **Modern Material UI:** Clean, responsive design utilizing AutoCompleteTextView dropdowns, Time/DatePicker dialogs, and ViewBinding for smooth interactions.

---

## 🛠️ Tech Stack & Architecture

This application is built using the **MVVM (Model-View-ViewModel)** architectural pattern to ensure clean separation of concerns, scalability, and testability.

*   **Language:** Kotlin
*   **UI Layer:** Jetpack Navigation, ViewBinding, Custom RecyclerView Adapters utilizing `ListAdapter` and `DiffUtil` for optimized list rendering.
*   **Asynchronous Processing:** Kotlin Coroutines (structured concurrency with `Dispatchers.IO` and `Dispatchers.Main`).
*   **Local Database:** Room Database with custom DAOs and foreign key enforcement (`PRAGMA foreign_keys=ON`).
*   **Network Client:** OkHttp for fast, reliable HTTP requests.

---

## 📁 Codebase Structure

The project follows a modular and clean packaging structure:

```text
app/src/main/java/jonathan/humphreys/blackmanager/
├── data/
│   ├── dao/          # Database access interfaces (TaskDao, UserDao, etc.)
│   ├── database/     # AppDatabase instance, Singleton pattern, initial seed data
│   └── entity/       # SQLite schemas and relational data classes (Tasks, Projects, Users)
└── ui/
    ├── activities/   # MainActivity hosting fragment views
    ├── adapters/     # ListAdapter implementations for RecyclerViews
    ├── fragments/    # LoginFragment, TasksFragment, SettingsFragment
    └── viewmodels/   # ViewModel & ViewModelFactory (lifecycle-aware state holders)
```

---

## 🔧 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   JDK 11 / JDK 17
*   Android SDK 35 (Compile SDK), SDK 24 (Minimum SDK)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/joe-chic/BlackManager.git
   ```
2. Open the project in Android Studio.
3. Allow Gradle to sync and download all dependencies.
4. Run the project on an emulator or a physical device.

### Notion Setup (Optional)
To link your Notion workspace:
1. Go to [Notion My Integrations](https://www.notion.so/my-integrations) and create an integration.
2. Copy the **Internal Integration Token**.
3. Open the **BlackManager** app, go to Settings, click on **Iniciar Sesión** and input your token.

---

## 📄 License
This project is open-source and available under the MIT License.
