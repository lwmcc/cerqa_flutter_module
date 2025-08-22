Added flutter screen for Chat. Will add for Profile, and Inbox. The Chat screen is using mock data for now, I have added the Pigeon tool for Android, Flutter communication. I am working on wiring this up at the moment so that I can fetch chats, and groups from Amplify and send them to the Flutter UI.

[chat_ui_with_mock_data.webm](https://github.com/user-attachments/assets/b2755c96-8bc5-4b9a-a098-00f3b872d930)

App MainScreen click through to ContactsScreen and then to SearchContactsScreen. Will remove that second search field in order to implement the first one. Also, I will either add the phone numbers on search screen becauase when a contact has more than one phone number they will have more than one entry in the list, or I will show contact once and have a phone number chooser when inviting to connect that way.

[cerqa-vid.webm](https://github.com/user-attachments/assets/accba938-4d98-4507-a810-44cd0b920676)

The iOS screenshot is on the left, and the other is the Android screenshot.

<img width="200" alt="cerqa-ios" src="https://github.com/user-attachments/assets/34ddaa20-c2dc-447c-a287-e1cba69eecb1" />
<img width="200" alt="cerqa-android" src="https://github.com/user-attachments/assets/d1533587-07e7-47c0-8356-3f0f2ee30f00" />

This is a full stack android and AWS Amplify app. Amplify is the client to the AppSync GraphQL APIs, DynamoDb, and to other AWS services.  When completed, this app will be part communicatin tool, part navigation tool etc. The code is written in kotlin, with a small amount of code in TypeScript for the db schema.

My CarClub(Cerqa App) is going to be an Android and iOS, KMM, <s>CMP</s>, Flutter cross platform app. I started with Android as I am an Android developer, and integrated some 3rd party APIs. These are AWS Amplify, <s>OpenSearch</s>, Lambdas, etc. For real time messaging, the app is using Ably with Firebase Cloud Messaging. The UI is Compose, Swift, and Flutter, so I figured that if the app will be multiplatform then it would be a good idea to use KMM, and <s>Compose Multiplatform</s>.

At this point the Android app has quite a few GraphQL queries and mutations to handle data for contacts functionality. Some of these include creating a contact, and viewing contacts. The contacts screen shows a list, which can include current contacts, contacts that the user has sent an invitation to connect, and invitations to connect that the user has received. With <s>OpenSearch</s>, the user table has been ingested and that data can be searchen for a people that the app user knows, and who are also using the app. At this point OpenSearch appears to be expensive so that service may be removed for a simple GraphQL query. I will no longer use OpenSearch, it is too expensive. I love the service and it is flawless, just out of reach because of cost.

Now that I have some functionality working, <s>I have started to integrate a cross platform project and will start adapting the Amplify authentication flow by moving it over to this project and implementing that for iOS version login</s>. I will implement Amplify Authentication on Android, and on iOS because auth is not compatible with KMM.
# cerqa_flutter_module

A new Flutter project.

## Getting Started

For help getting started with Flutter development, view the online
[documentation](https://flutter.dev/).

For instructions integrating Flutter modules to your existing applications,
see the [add-to-app documentation](https://flutter.dev/to/add-to-app).
