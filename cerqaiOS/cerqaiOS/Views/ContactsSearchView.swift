//
// ContactsSearchView.swift
// cerqaiOS
//
// Contacts search screen - mirrors ContactsSearchScreen from Android
//

import SwiftUI

struct ContactsSearchView: View {
    @State private var searchText = ""
    @State private var searchResults: [SearchContact] = []

    var body: some View {
        List {
            if searchText.isEmpty {
                ContentUnavailableView(
                    "Search Contacts",
                    systemImage: "magnifyingglass",
                    description: Text("Enter a name or phone number to search")
                )
            } else {
                ForEach(filteredContacts) { contact in
                    SearchContactRowView(contact: contact)
                }
            }
        }
        .navigationTitle("Search Contacts")
        .searchable(text: $searchText, prompt: "Search by name or phone")
    }

    var filteredContacts: [SearchContact] {
        // Placeholder search logic
        if searchText.isEmpty {
            return []
        }

        return (0..<5).map { index in
            SearchContact(
                id: "\(index)",
                userName: "Result \(index + 1)",
                phoneNumber: "+1 480-555-\(String(format: "%04d", index))",
                isAppUser: index % 2 == 0
            )
        }
    }
}

struct SearchContact: Identifiable {
    let id: String
    let userName: String
    let phoneNumber: String
    let isAppUser: Bool
}

struct SearchContactRowView: View {
    let contact: SearchContact

    var body: some View {
        HStack(spacing: 12) {
            Circle()
                .fill(Color.blue.opacity(0.3))
                .frame(width: 40, height: 40)
                .overlay(
                    Text(String(contact.userName.prefix(1)))
                        .font(.title3)
                        .foregroundColor(.blue)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(contact.userName)
                    .font(.headline)

                Text(contact.phoneNumber)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()

            if contact.isAppUser {
                Button(action: {
                    // Add contact action
                }) {
                    Text("Add")
                        .font(.subheadline)
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(Color.blue)
                        .cornerRadius(8)
                }
            } else {
                Button(action: {
                    // Invite action
                }) {
                    Text("Invite")
                        .font(.subheadline)
                        .foregroundColor(.blue)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.blue, lineWidth: 1)
                        )
                }
            }
        }
        .padding(.vertical, 4)
    }
}

