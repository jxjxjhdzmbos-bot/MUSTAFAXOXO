# Firebase Security Rules

## Important: Configure These Rules Before Deploying

### Realtime Database Rules

Replace the default rules in Firebase Console > Realtime Database > Rules with:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "auth != null",
        ".write": "$uid === auth.uid"
      }
    },
    "usernames": {
      "$username": {
        ".read": "auth != null",
        ".write": "!data.exists() && auth != null"
      }
    },
    "rooms": {
      "$roomName": {
        ".read": "auth != null",
        ".write": "auth != null && (
          !data.exists() || 
          data.child('playerXId').val() === auth.uid || 
          data.child('playerOId').val() === auth.uid
        )"
      }
    }
  }
}
```

### Storage Rules

Replace the default rules in Firebase Console > Storage > Rules with:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /profile_images/{userId}.jpg {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Security Best Practices

1. **Never use test mode rules in production**
2. **Validate all user inputs on the client side**
3. **Implement server-side validation with Firebase Functions** (recommended for production)
4. **Enable Firebase Authentication email verification** (optional but recommended)
5. **Monitor Firebase Console for suspicious activity**
6. **Limit file upload sizes in Storage rules** (e.g., max 5MB for profile images)
7. **Rate limit API calls** using Firebase App Check (recommended)

## Additional Recommendations

- Add Firebase App Check to prevent abuse
- Implement proper error handling and logging
- Add analytics to monitor user behavior
- Consider adding crashlytics for crash reporting
- Implement proper session management
- Add data retention policies
