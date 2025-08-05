import 'package:cerqa_flutter_module/src/chat.g.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  CerqaFlutterApi.setUp(_CerqaFlutterApi());
  runApp(const MyApp());
}

final GoRouter _router = GoRouter(
  routes: <RouteBase>[
    GoRoute(
      path: '/',
      builder: (BuildContext context, GoRouterState state) {
        //return const ChatHomeScreen();
        //return const HomeScreen();
        return const MyHomePage(title: 'Chat Home');
      },
      routes: <RouteBase>[
        GoRoute(
          path: 'conversation',
          name: 'conversation',
          builder: (BuildContext context, GoRouterState state) {
            return const ConversationScreen();
          },
        ),
      ],
    ),
  ],
);

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(routerConfig: _router);
    /*    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or press Run > Flutter Hot Reload in a Flutter IDE). Notice that the
        // counter didn't reset back to zero; the application is not restarted.
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );*/
  }
}

/// TabBar
class ChatHomeTabBar extends StatelessWidget {
  const ChatHomeTabBar({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      initialIndex: 0,
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Chat'),
          actions: [
            IconButton(
              icon: const Icon(Icons.add),
              onPressed: () {
                print("Main ***** CREATE CHAT");
                // My action
              },
            ),
          ],
          bottom: const TabBar(
            tabs: <Widget>[
              Tab(icon: Icon(Icons.chat_bubble_outline)),
              Tab(icon: Icon(Icons.group_outlined)),
            ],
          ),
        ),
        body: const TabBarView(
          children: <Widget>[
            Center(child: Text("Contacts")),
            Center(child: Text("Groups")),
          ],
        ),
      ),
    );
  }
}

// Will use ChatHomeScreen not HomeScreen
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Default Home')),
      body: Center(
        child: ElevatedButton(
          child: const Text('Default Home'),
          onPressed: () {
            context.goNamed('Default Home Screen');
          },
        ),
      ),
    );
  }
}

class ChatHomeScreen extends StatelessWidget {
  const ChatHomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: ChatHomeTabBar());
  }
}

class ConversationScreen extends StatelessWidget {
  const ConversationScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Conversation')),
      body: Center(
        child: ElevatedButton(
          child: const Text('Conversation'),
          onPressed: () {},
        ),
      ),
    );
  }
}

class GroupChatScreen extends StatelessWidget {
  const GroupChatScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Group Chat')),
      body: Center(
        child: ElevatedButton(
          child: const Text('Open route'),
          onPressed: () {
            // Navigate to second route when tapped.
          },
        ),
      ),
    );
  }
}

class CreateGroupScreen extends StatelessWidget {
  const CreateGroupScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Create New Group')),
      body: Center(
        child: ElevatedButton(
          child: const Text('Create Group'),
          onPressed: () {
            // Navigate to second route when tapped.
          },
        ),
      ),
    );
  }
}

class InboxScreen extends StatelessWidget {
  const InboxScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Conversation')),
      body: Center(
        child: ElevatedButton(
          child: const Text('Open route'),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
    );
  }
}

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: Center(
        child: ElevatedButton(
          child: const Text('Open route'),
          onPressed: () {
            // Navigate to second route when tapped.
          },
        ),
      ),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage>
    with SingleTickerProviderStateMixin {
  int _counter = 0;
  int _currentTabIndex = 0;

  final bool _isListenerAttached = false;

  late TabController _tabController;

  void _incrementCounter() {
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      _counter++;
    });
  }

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _tabController.addListener(() {
      if (!_tabController.indexIsChanging) {
        setState(() {
          _currentTabIndex = _tabController.index;
        });
      }
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('${widget.title} (Tab $_currentTabIndex)'),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: () {
              switch (_currentTabIndex) {
                case 0:
                  print("Main ***** CREATE CHAT");
                case 1:
                  print("Main ***** CREATE GROUP");
                default:
                  print("TAB ERROR");
              }
            },
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(icon: Icon(Icons.chat_bubble_outline)),
            Tab(icon: Icon(Icons.group_outlined)),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          ListView(
            padding: const EdgeInsets.all(16),
            children: const [
              ListTile(leading: Icon(Icons.chat), title: Text('Chat item 1')),
              ListTile(leading: Icon(Icons.chat), title: Text('Chat item 2')),
              ListTile(leading: Icon(Icons.chat), title: Text('Chat item 3')),
            ],
          ),
          ListView(
            padding: const EdgeInsets.all(16),
            children: const [
              ListTile(leading: Icon(Icons.chat), title: Text('Chat item 1')),
              ListTile(leading: Icon(Icons.chat), title: Text('Chat item 2')),
              ListTile(leading: Icon(Icons.chat), title: Text('Chat item 3')),
            ],
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}

class _CerqaFlutterApi implements CerqaFlutterApi {
  @override
  List<Chat> sendChats(List<Chat> chats) {
    for (var chat in chats) {
      print("_CerqaFlutterApi ***** sendChats() USER NAME ${chat.userName}");
      print("_CerqaFlutterApi ***** sendChats() URI ${chat.avatarUri}");
    }

    return chats;
  }

  @override
  void sendContacts() {
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
