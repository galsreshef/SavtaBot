# SavtaBot

Created for the elderly who sometimes suffers from a lack of communication with the environment.

SavtaBot is an Android app, impleminting Google's DialogFlow and TTS services.

- Download [APK](https://github.com/galsreshef/SavtaBot/blob/master/SavtaBot%201.2%20Submit.apk) and Install SavtaBot directly on your Android device.
## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software and how to install them

Install [Android Studio](https://developer.android.com/studio) on your PC.


### Installing

Clone this repository and import into **Android Studio**.
```bash
https://github.com/galsreshef/SavtaBot.git
```
Go to **File -> New -> Project from Version Control -> Git**

## Run on Android

-	`APK` - Using Android Studio. Build -> Build Bundle(s) / APK(s) -> Build APK(s)
-	`Run directly` -  Connect Android device or use AVD. Run -> Run 

### Your own SavtaBot

- Create a new project in [DialogFlow](https://dialogflow.cloud.google.com).
- Go to your Agent settings and copy your unique *Client access token*.
- In MainActivity.java <line 81> past your access token.
- Run application.

## Authors

* **Gal Reshef S** - *Initial work* - [Gal Github](https://github.com/galsreshef)
* **Eliyahu Yakubov** - *Initial work* - [Eliyahu Github](https://github.com/EliYakubov7)
