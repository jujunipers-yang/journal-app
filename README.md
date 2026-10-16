# Daily Journal

**Author:** Julia Yang <br>
**Status:** Core functionality complete (Aug. 2026); UI styling in progress

## Overview

Daily Journal is a native Android app for keeping a private, photo-based diary. Each entry has a date, a text note, and any number of photos. I built it because the journal apps I'd tried limited photos on their free tiers, and I wanted photos to be the main part of an entry rather than an add-on.

It is built with Kotlin, Jetpack Compose, and Room, and everything is stored locally on the device. The functionality is complete, but the UI is still minimal and unstyled. Custom visuals are planned (see Features).

[Screenshots to be added]

---

## Why I Built This

I like documenting my life with pictures. A photo captures the happiness of a moment, and a few lines of text give it context and story. Looking back through old entries is one of my favorite ways to relive memories.

Every journal app I tried got in the way of that:

- **Feature clutter.** Mood tracking, sleep logs, habit streaks. I didn't want any of it taking up space.
- **Photos as an afterthought.** On free plans, photo support is usually capped hard (often **one photo per day**). One photo almost never covers the highlights of a whole day.

So I built what I wanted: a private diary closer to a personal photo feed, where the photos are the main content and the text is a supporting caption.

---

## Features

### Version 1.0 (current)

- Create, edit, and delete journal entries
- **Multiple photos per entry** (up to 20, configurable with a single constant), chosen with the Android Photo Picker
- Remove individual photos from an entry
- Material 3 date picker so entries always have a valid, picked date
- Fully offline: everything is stored locally on-device
- Photos are copied into app-private storage, so they persist reliably across app restarts and Android versions
- Tap any entry on the home screen to open and edit it

### Planned

- Hand-drawn custom UI, icons, and theme (I like drawing, and I want the app to feel personal)
- Calendar view of all entries
- "This day, one year ago" memories
- Instagram-style feed: scroll chronologically through past entries with every photo fully expanded
- Photo grid layout that wraps instead of scrolling in a single row
- Store dates as real timestamps rather than formatted strings (see Reflection)

---

## Under the Hood

**Architecture:** MVVM with a repository layer and manual dependency injection.

```
UI (Compose screens)
   |  observes StateFlow / Compose state
ViewModels (one per screen)
   |
Repositories (interface + offline implementation)
   |
Room DAOs  ->  SQLite (Entry, Photo tables)
```

- **Data model:** a one-to-many relationship. One `Entry` has many `Photo` rows, linked by a foreign key with `onDelete = CASCADE`, so deleting an entry cleans up its photos automatically. Storing photos in their own table is what makes "as many as you want" possible, with no fixed photo columns and no arbitrary cap in the schema.
- **Reactive UI:** DAOs return `Flow`, ViewModels expose `StateFlow`, and screens collect it, so the UI updates itself whenever the database changes.
- **Dependency injection:** an `AppContainer` created in a custom `Application` class builds the repositories once. A `ViewModelProvider.Factory` injects them into each ViewModel.
- **Navigation:** Navigation Compose with three destinations (Home, New Entry, Edit Entry). The edit screen receives the entry id as a route argument through `SavedStateHandle`.
- **Per-row photo loading:** each row on the home screen collects its own entry's photo stream. `LazyColumn` only composes visible rows, so photos are only queried for entries actually on screen. The photo-fetching function is passed down as a function reference, which keeps composables and previews independent of a real ViewModel.
- **Testing:** DAO tests run against an in-memory Room database to verify inserts, queries, updates, and deletes in isolation.

---

## Tech Stack

| Area | Tools |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 |
| Persistence | Room 3.0 (via KSP) |
| Async / state | Kotlin Coroutines, Flow, StateFlow |
| Navigation | Navigation Compose |
| Images | Coil 3 (`AsyncImage`), Android Photo Picker API |
| Build | Android Gradle Plugin 9.2, Gradle version catalog |

Min SDK 24.

---

## Reflection

Most of what I learned on this project came from debugging. These were the most interesting problems:

### 1. Photos vanished after restarting the app (a device-specific bug)

My phone is a Samsung Galaxy Note9 running **Android 10**, which gets the Photo Picker through Google's *backported* module rather than the native one.

- **Symptom:** photos displayed fine right after saving, but turned into blank boxes once the app was closed and reopened. Calling `takePersistableUriPermission()`, the documented fix, crashed the app with a `SecurityException`.
- **Cause:** the backported picker returned `content://media/external/file/...` URIs that don't support persistable permission grants. My app only ever had temporary read access, which expired when the process died.
- **Fix:** stop depending on someone else's permission grant. The moment a photo is picked, I copy its bytes into a file in the app's own internal storage and save that **local file path** in the database. The app now owns the data outright, so no permissions are involved, and the same approach works on every Android version.

Takeaway: a documented API doesn't always behave the same across devices, and here the more robust design was to remove the dependency instead of working around it.

### 2. Modern build tooling doesn't match older tutorials

I used newer versions of everything (Room 3.0, KSP, AGP 9), so a lot of tutorial code didn't apply directly. Things I tracked down:

- **`JournalDatabase_Impl does not exist`:** Room 3 requires KSP (not KAPT), and I was missing the compiler dependency entirely.
- **KSP and Kotlin version mismatch:** my KSP plugin was built for Kotlin 2.0.21 while the project used 2.2.10, which caused hundreds of "incompatible Kotlin version" errors in generated code. The fix was aligning the versions.
- **Transitive dependency conflict:** a newer Coil release pulled in `kotlin-stdlib 2.4.0`, which my compiler couldn't read. I resolved it by pinning Coil to a compatible version.
- **Room 3 schema/version crashes:** Room 3 refuses to open a database whose schema changed without a version bump or migration. I now bump the version on every entity change and use a destructive migration fallback while developing.

### 3. State management bugs in Compose

- **Two sources of truth:** I held the selected photos both in a local `remember` variable and in the ViewModel. They drifted out of sync, causing photos to save inconsistently and new selections to overwrite old ones. The fix was a single source of truth in the ViewModel.
- **Unintended state resets:** rebuilding the UI state object from scratch on each keystroke silently wiped the photo list. Switching to `.copy()` updates only the field that changed.
- **Stale form state:** without a real nav back stack, the ViewModel survived between visits, so the "new entry" form kept the previous entry's text until I reset it explicitly.
- **Syncing a one-to-many relationship on edit:** there's no single photo row to "update", so saving an edited entry deletes its existing photo rows and re-inserts the current list. That keeps the database matching the UI exactly, including removed photos.

### 4. Design decisions I'd revisit

- **Date storage:** dates are currently saved as formatted strings. That's fine for display, but string sorting isn't true chronological sorting. The next step is storing a timestamp and formatting it only for display, which also unlocks the calendar view.
- I chose per-row photo collection over merging all entries and photos into one combined `Flow`. It's simpler and loads lazily, but if I add features that need all photos up front (such as the feed view), I may refactor.

---

## How to Run

**Requirements:** Android Studio (recent stable), Android SDK 24+, a device or emulator.

```bash
git clone https://github.com/<your-username>/<repo-name>.git
```

1. Open the project in Android Studio and let Gradle sync.
2. Select a device or emulator and press **Run**.

**Note for older devices (Android 4.4 to 10):** the manifest includes the Google Play services entry that installs the backported Photo Picker module, so the picker works on older devices that have Play services.

---

## Credits

- Architecture patterns adapted from Google's [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course) and Inventory app codelabs.
- [Coil](https://coil-kt.github.io/coil/) for image loading
- [Android Jetpack](https://developer.android.com/jetpack) (Room, Navigation, Lifecycle)