# Squawker Code

This is a exercise repository for the Squawker example app which is part of Udacity's Advanced Android course. The Squawker example app uses Firebase Cloud Message to receive Twitter-like messages, sent from [this server](https://squawkerfcmserver.udacity.com/), in real time. You can learn more about how to use this repository [here](https://classroom.udacity.com/courses/ud857/lessons/8b2a9d63-0ff5-48ff-90d3-a9855b701dae/concepts/41b82e3c-2797-46e5-8a66-684098ca8cbb).

- Everytime a message is sent from the Firebase console all devices with the app installed will receive a notification with the desired message!
- It's possible to Target groups of devices with topics they subscribe to, or single devices using device token,
get token from device using: String token = FirebaseInstanceId.getInstance().getToken();


FCM has documentation for both Android and setting up server code:

The Android documentation is [here](https://firebase.google.com/docs/cloud-messaging/android/client)

Documentation about how messages are sent from FCM to client is [here](https://firebase.google.com/docs/cloud-messaging/concept-options).

Detailed information about setting up an FCM server like the [Squawker udcity server](https://squawkerfcmserver.udacity.com/) is [here](https://firebase.google.com/docs/cloud-messaging/server)

You can check out [this blog post](https://firebase.googleblog.com/2016/08/sending-notifications-between-android.html) for an example of some Node.js code for setting up an FCM server. The library used for the Squawker server is called [fcm-node](https://www.npmjs.com/package/fcm-node).

If you're interested in learning more about Android and Firebase, consider taking Udacity's [Firebase in a Weekend](https://www.udacity.com/course/firebase-in-a-weekend-by-google-android--ud0352) course for Android. The class is free and walks you through the creation of a real time chat app with user accounts, photo sharing and more, using Firebase as a backend.
