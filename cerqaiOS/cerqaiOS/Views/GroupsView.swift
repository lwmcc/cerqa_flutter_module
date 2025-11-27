//
// GroupsView.swift
// cerqaiOS
//
// Groups screen - mirrors GroupsScreen from Android
//

import SwiftUI

struct GroupsView: View {
    @State private var searchText = ""

    var body: some View {
        List {
            ForEach(0..<8) { index in
                GroupRowView(
                    groupName: "Group \(index + 1)",
                    memberCount: Int.random(in: 3...20),
                    lastActivity: "\(index + 1)h ago"
                )
            }
        }
        .navigationTitle("Groups")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                NavigationLink(destination: GroupsAddView()) {
                    Image(systemName: "plus")
                }
            }
        }
        .searchable(text: $searchText, prompt: "Search groups")
    }
}

struct GroupRowView: View {
    let groupName: String
    let memberCount: Int
    let lastActivity: String

    var body: some View {
        HStack(spacing: 12) {
            // Group avatar
            Circle()
                .fill(Color.purple.opacity(0.3))
                .frame(width: 50, height: 50)
                .overlay(
                    Image(systemName: "person.3.fill")
                        .foregroundColor(.purple)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(groupName)
                    .font(.headline)

                HStack {
                    Text("\(memberCount) members")
                        .font(.subheadline)
                        .foregroundColor(.secondary)

                    Text("•")
                        .foregroundColor(.secondary)

                    Text(lastActivity)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
}

