# MUSTAFAXOXO - XO Game (Tic-Tac-Toe)

An Android application for XO (Tic-Tac-Toe) game with offline and online modes.

## Features

### Offline Modes
- **Player vs Player (PvP)**: Play against another player on the same device
- **Player vs AI (PvAI)**: Play against AI with three difficulty levels:
  - **Easy**: AI makes random moves
  - **Medium**: AI blocks opponent's winning moves
  - **Hard**: AI uses minimax algorithm for optimal play

### Online Mode
- **Firebase Realtime Database Integration**: Real-time multiplayer gameplay
- **Create Room**: Create a game room with a custom name
- **Join Room**: Join an existing game room by name
- **Real-time Synchronization**: Moves are synchronized in real-time
- **Disconnect Handling**: Game handles player disconnections gracefully

### Authentication
- **Google Sign-In**: Sign in with Gmail account only
- **User Profile**: Choose unique username and upload profile image
- **Firebase Storage**: Profile images stored securely in Firebase Storage

### About Section
- **Developer**: مصطفى عايد
- **Version**: 1.0

## Technologies Used

- **Language**: Java
- **Framework**: AndroidX
- **Firebase**: Realtime Database, Storage, Authentication
- **Google Sign-In**: OAuth 2.0
- **Image Loading**: Glide

## Setup Instructions

### Prerequisites
1. Android Studio (Arctic Fox or later)
2. JDK 8 or later
3. Firebase account

### Firebase Setup

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one

2. **Add Android App to Firebase**:
   - Package name: `com.mustafa.xogame`
   - Download `google-services.json`
   - Replace the placeholder `app/google-services.json` with your downloaded file

3. **Enable Firebase Services**:
   - **Authentication**: Enable Google Sign-In provider
   - **Realtime Database**: Create database in test mode
   - **Storage**: Create storage bucket

4. **Configure Google Sign-In**:
   - In Firebase Console, go to Authentication > Sign-in method
   - Enable Google provider
   - Copy the Web client ID
   - Replace `YOUR_WEB_CLIENT_ID_HERE` in `app/src/main/res/values/strings.xml` with your Web client ID

5. **Configure Firebase Security Rules**:
   - See `FIREBASE_SECURITY.md` for required Database and Storage security rules
   - **IMPORTANT**: Apply these rules before deploying to production

### Building the App

1. Clone the repository:
   ```bash
   git clone https://github.com/jxjxjhdzmbos-bot/MUSTAFAXOXO.git
   cd MUSTAFAXOXO
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

## Project Structure

```
app/src/main/java/com/mustafa/xogame/
├── ai/
│   └── AIPlayer.java          # AI logic for different difficulty levels
├── firebase/
│   ├── FirebaseHelper.java    # Firebase authentication and storage helper
│   └── GameRoomManager.java   # Online game room management
├── models/
│   ├── GameBoard.java         # Game board logic
│   ├── GameRoom.java          # Online game room model
│   └── User.java              # User profile model
├── AboutActivity.java         # About screen
├── GameActivity.java          # Main game screen
├── MainActivity.java          # Main menu
├── ModeSelectionActivity.java # Game mode selection
├── OnlineMenuActivity.java    # Online mode menu
└── ProfileSetupActivity.java  # User profile setup
```

## Security Features

- **Input Validation**: Usernames and room names are validated (alphanumeric, 3-20 characters)
- **File Size Limits**: Profile images limited to 5MB
- **Firebase Security Rules**: Comprehensive rules for Database and Storage (see FIREBASE_SECURITY.md)
- **Modern Android APIs**: Uses ActivityResultLauncher instead of deprecated methods
- **Authentication Required**: All online features require Google Sign-In

## Important Notes

1. **Firebase Configuration Required**: 
   - Replace `app/google-services.json` with your actual Firebase configuration
   - Update `default_web_client_id` in strings.xml with your Web Client ID
   
2. **Firebase Security Rules**:
   - Apply the security rules from `FIREBASE_SECURITY.md`
   - Never use test mode rules in production
   
3. **Network Requirements**:
   - Online mode requires active internet connection
   - Real-time synchronization requires stable connection

## Game Rules

- Classic Tic-Tac-Toe rules
- 3x3 grid
- Players alternate turns placing X or O
- First player to get 3 in a row (horizontal, vertical, or diagonal) wins
- If all cells are filled with no winner, it's a draw

## License

This project is created by مصطفى عايد.

## Version

1.0
