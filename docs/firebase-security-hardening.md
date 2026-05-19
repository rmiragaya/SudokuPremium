# Firebase Security Hardening

## Firestore Rules

Rules live in `firestore.rules` and are wired by `firebase.json`.

Current intent:

- Only authenticated Firebase users can read/write.
- A user can only access `users/{uid}` where `uid == request.auth.uid`.
- User root documents accept only compact profile/entitlement fields.
- Stats documents accept only the aggregate fields currently written by the app.
- Completed game documents can be created by the owning user, but not updated or deleted from the client.
- All other collections/documents are denied.

Manual deploy, after selecting the right Firebase project:

```powershell
firebase deploy --only firestore:rules --project sudoku-mentor-dev
firebase deploy --only firestore:rules --project sudoku-premium-rmiragaya
```

If the Firebase CLI is not configured, paste the same rules manually in:

Firebase Console -> Firestore Database -> Rules.

## App Check

App Check is configured in Firebase Console, not from this repository.

Recommended rollout:

1. Firebase Console -> App Check -> Apps.
2. Select the Android app for `ropa.miragaya.sudokumentor`.
3. Register Play Integrity as the provider.
4. Keep enforcement off at first.
5. Release an internal/closed build and verify App Check request metrics.
6. Enable enforcement for Firestore only after valid traffic is visible.
7. Repeat separately for the dev Firebase project if needed.

Do not enable enforcement before verifying closed/internal tester traffic, otherwise Firestore writes can start failing for real users.
