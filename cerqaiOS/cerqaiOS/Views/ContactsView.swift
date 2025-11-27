//
// ContactsView.swift
// cerqaiOS
//
// Contacts screen - mirrors ContactsScreen from Android
//

import SwiftUI

struct ContactsView: View {
    @StateObject private var viewModel = ContactsViewModel()
    @State private var searchText = ""

    var body: some View {
        Group {
            if viewModel.isLoading {
                ProgressView("Loading contacts...")
            } else if viewModel.contacts.isEmpty {
                ContentUnavailableView(
                    "No Contacts",
                    systemImage: "person.2",
                    description: Text("Add contacts to start chatting")
                )
            } else {
                List {
                    ForEach(filteredContacts) { contact in
                        ContactRowView(
                            userName: contact.displayName,
                            phoneNumber: contact.phoneNumber ?? "",
                            avatarInitial: String(contact.displayName.prefix(1))
                        )
                    }
                }
            }
        }
        .navigationTitle("Contacts")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                NavigationLink(destination: ContactsSearchView()) {
                    Image(systemName: "magnifyingglass")
                }
            }
        }
        .searchable(text: $searchText, prompt: "Search contacts")
        .onAppear {
            if viewModel.contacts.isEmpty {
                viewModel.fetchContacts()
            }
        }
    }

    var filteredContacts: [Contact] {
        if searchText.isEmpty {
            return viewModel.contacts
        }
        return viewModel.searchContacts(query: searchText)
    }
}

struct ContactRowView: View {
    let userName: String
    let phoneNumber: String
    let avatarInitial: String

    var body: some View {
        HStack(spacing: 12) {
            // Avatar
            Circle()
                .fill(Color.green.opacity(0.3))
                .frame(width: 50, height: 50)
                .overlay(
                    Text(avatarInitial)
                        .font(.title3)
                        .foregroundColor(.green)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(userName)
                    .font(.headline)

                Text(phoneNumber)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
}

