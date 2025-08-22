import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'lib/src/chat.g.dart',
    dartOptions: DartOptions(),
    kotlinOut: '../app/src/main/java/com/mccartycarclub/pigeon/ChatHost.kt',
    kotlinOptions: KotlinOptions(package: 'com.mccartycarclub.pigeon'),
    dartPackageName: 'cerqa_flutter_module',
  ),
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

  @async
  List<Chat> fetchChats();

  void fetchDirectConversation(String receiverUserId);

  void createMessage();

  void deleteMessage();

  void createChat(String receiverUserId);

  void deleteChat();

  bool doesGroupNameExist(String groupName);

  void fetchGroupChats();

  void fetchGroupConversation();

  void deleteGroupMessage();

  void fetchGroupMessage();

  void createGroupMessage();

  void createGroup(String groupName);

  void deleteGroup();

  List<Contact> fetchContacts();
}

@FlutterApi()
abstract class CerqaFlutterApi {
  //void sendChats(List<Chat> chats);
}
