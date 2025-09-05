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

class Group {
  String? groudId;
  String? groupName;
  String? groupAvatarUri;
}

class Chat {
  String? chatId;
  String? userName;
  String? avatarUri;
  String? userId;
}

class Message {
  String? id;
  String? messageId;
  String? content;
  String? senderId;
  String? createdAt;
}

@HostApi()
abstract class CerqaHostApi {

  @async
  List<Chat> fetchChats();

  @async
  List<Message> fetchDirectMessages();

  @async
  bool createMessage(String message, String receiverUserId);

  void deleteMessage();

  void createChat(String receiverUserId);

  void deleteChat();

  bool doesGroupNameExist(String groupName);

  @async
  List<Group> fetchGroupChats();

  void fetchGroupConversation();

  void deleteGroupMessage();

  void fetchGroupMessage();

  void createGroupMessage();

  void createGroup(String groupName);

  void deleteGroup();

  @async
  List<Contact> fetchContacts();
}

@FlutterApi()
abstract class CerqaFlutterApi {
  //void sendChats(List<Chat> chats);
}
