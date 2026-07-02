# FocusDrift – ADHD Focus & Body Double Coach

A production-ready Android app: Pomodoro focus timer, habit tracker with streaks, live body
doubling rooms, and an XP/coin reward loop — designed for ADHD brains.

- Package: `com.radonshadow.focusdrift`
- Min SDK 26, target SDK 35, Kotlin + Jetpack Compose (Material 3)
- Clean Architecture (`core` / `data` / `domain` / `service` / `worker` / `ui`) + MVVM + Hilt

The UI was implemented pixel-for-pixel from a Claude Design handoff bundle covering all 10 app
screens and the shared component library (Focus Orb states, habit cards, room cards, reward
cards, etc.) — see `ui/components` and `ui/screens`.

## Before you build

1. **Firebase**: `app/google-services.json` is a placeholder. Replace it with a real config from
   a Firebase project that has Realtime Database and Anonymous Authentication enabled. Body
   doubling rooms live at `rooms/{roomId}` — see `FirebaseBodyDoubleManager` for the exact shape,
   and lock down write access with rules so a participant can only write their own
   `rooms/{roomId}/participants/{uid}` node.
2. **Play Billing**: the subscription products referenced in `SubscriptionConstants`
   (`focusdrift_pro_monthly`, `focusdrift_pro_yearly`, `focusdrift_pro_lifetime`) must exist in
   the Play Console for `com.radonshadow.focusdrift` before the Pro screen can complete a
   purchase.
3. **Audio**: session-complete/break chimes currently play the device's default notification
   tone (see `SoundUtils`) rather than bundled audio, so the repo ships with no unlicensed sound
   assets. Swap in real `res/raw` chimes when you have licensed audio.
4. **Gradle**: `./gradlew assembleDebug` needs a local Android SDK (`ANDROID_HOME`/
   `local.properties`) — none is configured in this repo.

## Architecture notes

- The countdown lives in `FocusTimerService`, a foreground service — not a ViewModel coroutine —
  so it survives the app backgrounding. `TimerServiceConnection` binds to it and exposes the
  domain-level `FocusTimerController` interface.
- `CompleteSessionUseCase` is the single place that grants XP and coins; both the service (normal
  countdown finish) and the "done early" action funnel through it.
- Body-doubling room presence uses the Firebase anonymous-auth uid (`FirebaseAuthManager`), kept
  deliberately separate from the local single-profile progress record
  (`AppConstants.DEFAULT_USER_ID`) that Room persists — see the doc comments on
  `JoinRoomUseCase`/`LeaveRoomUseCase`/`CreateRoomUseCase`.
- Habit streaks: a day counts if completed before local midnight; `StreakProtectionWorker` warns
  at 8pm if a habit with an active streak isn't done yet, independent of each habit's own
  configurable reminder time (`HabitReminderWorker`).
