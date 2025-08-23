import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../src/chat.g.dart';

class DirectMessageScreen extends StatefulWidget {
  final String header;

  const DirectMessageScreen({super.key, required this.header});

  @override
  State<DirectMessageScreen> createState() => _DirectMessageScreen();
}

class _DirectMessageScreen extends State<DirectMessageScreen> {
  late final String header;

  @override
  void initState() {
    super.initState();
    header = widget.header;
  }

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
                  builder: (context) => DirectMessageScreen(
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
  final List<Contact> contacts;

  const NewGroupChatScreen({super.key, required this.contacts});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: NewGroupChat(contacts: contacts));
  }
}

class NewGroupChat extends StatefulWidget {
  final List<Contact> contacts;

  const NewGroupChat({super.key, required this.contacts});

  @override
  State<NewGroupChat> createState() => _NewGroupChatScreenState();
}

class _NewGroupChatScreenState extends State<NewGroupChat> {
  final Set<Contact> selectedContacts = {};

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('New Group'),
        actions: selectedContacts.isNotEmpty
            ? [
          IconButton(
            icon: const Icon(Icons.navigate_next),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute<void>(
                  builder: (context) =>
                      CreateGroupNameScreen(header: 'Group Name',),
                ),
              );
            },
          ),
        ]
            : null,
      ),
      body: ListView.builder(
        itemCount: widget.contacts.length,
        itemBuilder: (context, index) {
          final contact = widget.contacts[index];
          final isSelected = selectedContacts.contains(contact);

          return CheckboxListTile(
            title: Text(contact.userName ?? "Name not found"),
            value: isSelected,
            onChanged: (bool? value) {
              setState(() {
                if (value == true) {
                  selectedContacts.add(contact);
                } else {
                  selectedContacts.remove(contact);
                }
              });
            },
          );
        },
      ),
    );
  }
}

class CreateGroupNameScreen extends StatelessWidget {
  const CreateGroupNameScreen({super.key, required this.header});

  final String header;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(header)),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              decoration: InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'Name your group',
              ),
            ),
          ),
        ],
      ),
    );
  }
}
