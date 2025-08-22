import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../src/chat.g.dart';

class DirectConversationScreen extends StatelessWidget {
  const DirectConversationScreen({super.key, required this.header});

  final String header;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(header)),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: 20,
              itemBuilder: (context, index) {
                return ListTile(title: Text('Message ${index + 1}'));
              },
            ),
          ),

          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              decoration: InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Send a message',
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class GroupConversationScreen extends StatelessWidget {
  const GroupConversationScreen({super.key, required this.header});

  final String header;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(header)),
      body: Column(
        children: [
          Expanded(
            child: true
                ? Center(
                    child: Text(
                      'No chats yet...',
                      style: TextStyle(color: Colors.grey),
                    ),
                  )
                : ListView.builder(
                    itemCount: 0,
                    itemBuilder: (context, index) {
                      return ListTile(title: Text('test'));
                    },
                  ),
          ),

          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              decoration: InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Send a message',
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class NewChatScreen extends StatelessWidget {
  const NewChatScreen({
    super.key,
    required this.header,
    required this.contacts,
  });

  final String header;
  final List<Contact> contacts;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(header)),
      body: ListView.builder(
        itemCount: contacts.length,
        itemBuilder: (BuildContext context, int index) {
          return ListTile(
            title: Text(contacts[index].userName ?? "No name found"),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute<void>(
                  builder: (context) => DirectConversationScreen(
                    header: contacts[index].userName ?? "Chat",
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

class NewGroupChatScreen extends StatelessWidget {
  const NewGroupChatScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('New Group Chat')),
      body: Column(),
    );
  }
}
