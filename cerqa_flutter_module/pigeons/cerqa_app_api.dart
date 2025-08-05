import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
    PigeonOptions(
      dartOut: 'lib/src/chat.g.dart',
      dartOptions: DartOptions(),
      kotlinOut: '../app/src/main/java/com/mccartycarclub/pigeon/ChatHost.kt',
      kotlinOptions: KotlinOptions(package: 'com.mccartycarclub.pigeon',),
      dartPackageName: 'cerqa_flutter_module',
    )
)
class Contact {
  String? userName;
  String? phoneNumber;
  String? userId;
  String? avatarUri;
}

class Chat {
  String? userName;
  String? avatarUri;
}

@HostApi()
abstract class CerqaHostApi {
  void createChat(String receiverUserId);

  void deleteChat();

  void createGroup();

  void deleteGroup();

  void createMessage();

  void deleteMessage();

  void createGroupMessage();

  void deleteGroupMessage();
}

@FlutterApi()
abstract class CerqaFlutterApi {
  List<Chat> sendChats(List<Chat> chats);

  void sendContacts();

  void sendGroups();

  //TODO: change to sendConversation(), maybe
  void sendMessages();
}
