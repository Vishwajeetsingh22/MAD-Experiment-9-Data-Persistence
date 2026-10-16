# Implementation Plan - SQLite Login Page & Database Persistence

Based on the provided screenshots, the user wants a clean Login Activity/Screen where user credentials (username and password) are saved directly into a local SQLite database (`LoginDatabase.db`, table `users`), matching the exact structure from their reference screenshots.

## User Review Required

> [!IMPORTANT]
> - Database Name: `LoginDatabase.db`
> - Table Name: `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `username` TEXT, `password` TEXT)
> - UI Style: Clean centered Card with "Login" heading, input fields, and purple accent button.

## Open Questions

- None. The requirements are clear from the screenshots and description.

## Proposed Changes

### Database & UI Components

#### [MODIFY] [DatabaseHelper.kt](file:///C:/Users/admin/AndroidStudioProjects/PersistX_Exp9/app/src/main/java/com/example/persistx_exp_9/DatabaseHelper.kt)
- Update SQLite database name to `LoginDatabase.db`.
- Create table `users` with columns `id`, `username`, and `password`.
- Implement `insertUser(username: String, password: String): Boolean`.

#### [MODIFY] [activity_main.xml](file:///C:/Users/admin/AndroidStudioProjects/PersistX_Exp9/app/src/main/res/layout/activity_main.xml)
- Design the Login card layout matching the reference screenshot:
  - Title: "Login"
  - `etUsername` (Username / Email input)
  - `etPassword` (Password input)
  - `btnLogin` (Purple action button)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/admin/AndroidStudioProjects/PersistX_Exp9/app/src/main/java/com/example/persistx_exp_9/MainActivity.kt)
- Bind `etUsername`, `etPassword`, `btnLogin`, and `DatabaseHelper`.
- Handle login click: validate inputs, insert user into SQLite database, and show Toast confirmation.

## Verification Plan

### Automated Tests
- Run `app:assembleDebug` via Gradle to verify successful compilation.

### Manual Verification
- Deploy the app to emulator/device, enter username and password, tap Login, and verify record creation in Android Studio's **Database Inspector**.
