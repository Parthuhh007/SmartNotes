# Smart Notes

Kotlin Android app for creating, editing, deleting, and searching notes. Notes are persisted locally with **Room** (SQLite), filtered by category, and searched by title or body.

Open this folder in **Android Studio** (`File > Open`) and let Gradle sync.

## Requirements

- Android Studio Ladybug / Koala or newer
- JDK 17
- Android SDK 35
- Device or emulator running API 26+

## Run

1. Open `SmartNotes` in Android Studio.
2. Wait for Gradle sync (the IDE can generate `gradle/wrapper/gradle-wrapper.jar` if it is missing: `File > New > New Project` is not needed; use `File > Sync Project with Gradle Files`, or run `gradle wrapper` once).
3. Click **Run** on an emulator or phone.

## What is implemented

- Create, edit, and delete notes
- Full-text search on title and content
- Category chips: All, Personal, Work, Study, Ideas, Other
- Pin notes to the top of the list
- Room database file `smart_notes.db` for offline persistence
- MVVM + Kotlin Flow so the list updates as soon as the database changes

## Project layout (Android Studio)

```
SmartNotes/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/smartnotes/app/
│       │   │   ├── MainActivity.kt
│       │   │   ├── SmartNotesApp.kt
│       │   │   ├── data/
│       │   │   │   ├── local/          # Room database + DAO
│       │   │   │   ├── model/          # Note entity + categories
│       │   │   │   └── repository/
│       │   │   └── ui/
│       │   │       ├── notes/          # list, search, filters
│       │   │       ├── editor/         # create / edit
│       │   │       ├── components/
│       │   │       └── theme/
│       │   └── res/
│       ├── test/                       # JVM unit tests
│       └── androidTest/                # instrumented tests
├── gradle/libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

## Architecture

```
UI (Compose) → ViewModel → Repository → Room DAO → SQLite
```

The UI never talks to SQLite directly. `NoteDao` runs filtered queries:

- empty search + **All** → every note
- search query → `LIKE` on title and content
- category chip → `category = :category`
