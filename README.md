# CampusAlert - Academic Project

CampusAlert is a simplified Android application designed for campus safety, task organization, and classroom management. It was built using Java and follows the MVVM (Model-View-ViewModel) architectural pattern to ensure clean and maintainable code, specifically tailored for an HND Software Engineering academic project.

## Features
- **Task Management**: Create, edit, and track academic tasks or alerts.
- **Classroom Management**: Join classrooms via a 6-digit code and view details about your classes.
- **Campus Notifications**: Receive real-time broadcasts and safety alerts via Firebase Firestore.
- **Offline Support**: Local data persistence using Room Database.
- **Connectivity Awareness**: Real-time monitoring of internet connection with a dedicated error state UI.

---

## Technical Stack
- **Language**: Java
- **UI Framework**: Material Design 3 (M3)
- **Database**: Room (Local Persistence)
- **Cloud Backend**: Firebase Firestore (Remote Notifications)
- **Architecture**: MVVM
- **Navigation**: Bottom Navigation with Fragments

---

## Prerequisites
To run this project, you will need:
1. **Android Studio**: Jellyfish or newer.
2. **Java Development Kit (JDK)**: Version 11.
3. **Android Device/Emulator**: Running API 26 (Android 8.0) or higher.
4. **Firebase Project**: A project set up on the Firebase Console.

---

## Setup Instructions

### 1. Firebase Configuration
To enable the Notifications feature, you must link the app to your Firebase project:
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Create a new project named "CampusAlert".
3. Add an Android app with the package name `com.bless.task`.
4. Download the `google-services.json` file.
5. Move the `google-services.json` file into the `app/` directory of this project.
6. In `app/build.gradle.kts`, uncomment the line:
   ```kotlin
   // alias(libs.plugins.google.services)
   ```
   to look like:
   ```kotlin
   alias(libs.plugins.google.services)
   ```

### 2. Database Setup (Firestore)
1. In your Firebase Console, enable **Cloud Firestore**.
2. Create a collection named `campus_alerts`.
3. Add documents with the following fields:
   - `title` (string)
   - `message` (string)
   - `timestamp` (string)
   - `type` (string)

### 3. Build and Run
1. Open the project in Android Studio.
2. Let Gradle sync and download dependencies.
3. Click the **Run** button to install the app on your device/emulator.

---

## File Structure & Responsibilities

### Data Layer (`com.bless.task.data`)
- `Task.java`: Entity representing a student task (id, title, subject, due date, status).
- `Classroom.java`: Entity representing a classroom joined by the student.
- `CampusAlert.java`: Data model for notifications fetched from Firebase.
- `TaskDao.java` & `ClassroomDao.java`: Data Access Objects for Room queries.
- `TaskDatabase.java`: Main database configuration and migration handler.

### Repository Layer (`com.bless.task.repository`)
- `TaskRepository.java`: Manages data operations for Tasks (Room).
- `ClassroomRepository.java`: Manages data operations for Classrooms (Room).
- `FirebaseService.java`: Handles fetching real-time alerts from Firestore.

### ViewModel Layer (`com.bless.task.viewmodel`)
- `TaskViewModel.java`: Provides data to the Tasks UI and survives configuration changes.
- `ClassroomViewModel.java`: Provides classroom data to the UI.

### UI Layer (`com.bless.task.ui`)
- `TasksFragment.java`: Displays the list of tasks and handles CRUD operations.
- `ClassroomsFragment.java`: Displays joined classrooms and allows joining new ones.
- `NotificationsFragment.java`: Fetches and displays campus alerts from Firebase.
- `JoinClassroomBottomSheet.java`: A modal for entering classroom codes.

### Activities
- `SplashActivity.java`: Initial branding screen with a 2-second timer.
- `MainActivity.java`: The core host activity managing the Bottom Navigation and Fragment transitions.
- `AddTaskActivity.java`: Dedicated screen for creating and editing tasks.
- `ClassroomDetailActivity.java`: Displays detailed information about a specific classroom.

### Adapters (`com.bless.task.adapter`)
- `TaskAdapter.java`, `ClassroomAdapter.java`, `AlertAdapter.java`: Bridge classes that bind data lists to the RecyclerView UI components.

---

## Academic Defense Tips
- **MVVM**: Explain that this separates the Business Logic (ViewModel) from the UI (View), making the app easier to test and maintain.
- **Room**: Mention that this provides a robust abstraction over SQLite, ensuring data is saved even when the app is closed.
- **Firebase**: Highlight that this is used for server-side updates without needing to manage a custom backend API.
- **Simplicity**: Emphasize that the code avoids complex libraries (like Dagger/Hilt) to keep it understandable for student-level software maintenance.
