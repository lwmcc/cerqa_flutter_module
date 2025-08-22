import 'cerqa_app_api.dart';

class FlutterApi implements CerqaFlutterApi {
  @override
  List<Chat> sendChats(List<Chat> chats) {
    print("_FlutterApi ***** SEND CHATS");
    return chats;
  }

  @override
  void sendContacts(List<Contact> contacts) {
    // TODO: implement sendContacts
  }

  @override
  void sendGroups() {
    // TODO: implement sendGroups
  }

  @override
  void sendMessages() {
    // TODO: implement sendMessages
  }

}
