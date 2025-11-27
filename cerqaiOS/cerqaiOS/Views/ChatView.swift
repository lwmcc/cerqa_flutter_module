//
// ChatView.swift
// cerqaiOS
//
// Chat screen - mirrors Android ChatScreen
//

import SwiftUI

struct ChatView: View {
    @State private var searchText = ""

    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                Image(systemName: "message.fill")
                    .font(.system(size: 60))
                    .foregroundColor(.gray)
                Text("No Chats Yet")
                    .font(.title2)
                    .fontWeight(.semibold)
                Text("Your conversations will appear here")
                    .font(.body)
                    .foregroundColor(.secondary)
            }
            .navigationTitle("Chats")
            .searchable(text: $searchText, prompt: "Search chats")
        }
    }
}

struct ChatRowView: View {
    let userName: String
    let lastMessage: String
    let timestamp: String
    let unreadCount: Int

    var body: some View {
        HStack(spacing: 12) {
            // Avatar
            Circle()
                .fill(Color.blue.opacity(0.3))
                .frame(width: 50, height: 50)
                .overlay(
                    Text(String(userName.prefix(1)))
                        .font(.title3)
                        .foregroundColor(.blue)
                )

            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(userName)
                        .font(.headline)
                    Spacer()
                    Text(timestamp)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                HStack {
                    Text(lastMessage)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .lineLimit(1)

                    Spacer()

                    if unreadCount > 0 {
                        Text("\(unreadCount)")
                            .font(.caption2)
                            .foregroundColor(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.blue)
                            .clipShape(Capsule())
                    }
                }
            }
        }
        .padding(.vertical, 4)
    }
}

