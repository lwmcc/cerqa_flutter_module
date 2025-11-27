//
// GroupsAddView.swift
// cerqaiOS
//
// Add group screen - mirrors GroupsAddScreen from Android
//

import SwiftUI

struct GroupsAddView: View {
    @State private var groupName = ""
    @State private var selectedContacts: Set<String> = []
    @State private var searchText = ""
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 0) {
            // Group name input
            VStack(alignment: .leading, spacing: 8) {
                Text("Group Name")
                    .font(.headline)

                TextField("Enter group name", text: $groupName)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
            }
            .padding()

            Divider()

            // Selected contacts count
            if !selectedContacts.isEmpty {
                HStack {
                    Text("\(selectedContacts.count) contact(s) selected")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    Spacer()
                }
                .padding()
            }

            // Contact list
            List {
                ForEach(0..<15) { index in
                    let contactId = "contact_\(index)"
                    ContactSelectionRow(
                        userName: "Contact \(index + 1)",
                        phoneNumber: "+1 480-555-\(String(format: "%04d", index))",
                        isSelected: selectedContacts.contains(contactId)
                    )
                    .onTapGesture {
                        if selectedContacts.contains(contactId) {
                            selectedContacts.remove(contactId)
                        } else {
                            selectedContacts.insert(contactId)
                        }
                    }
                }
            }

            // Create button
            Button(action: {
                // Create group action
                dismiss()
            }) {
                Text("Create Group")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(groupName.isEmpty || selectedContacts.count < 2 ? Color.gray : Color.blue)
                    .cornerRadius(12)
            }
            .disabled(groupName.isEmpty || selectedContacts.count < 2)
            .padding()
        }
        .navigationTitle("New Group")
        .searchable(text: $searchText, prompt: "Search contacts")
    }
}

struct ContactSelectionRow: View {
    let userName: String
    let phoneNumber: String
    let isSelected: Bool

    var body: some View {
        HStack(spacing: 12) {
            // Selection indicator
            Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                .foregroundColor(isSelected ? .blue : .gray)
                .font(.title3)

            // Avatar
            Circle()
                .fill(Color.blue.opacity(0.3))
                .frame(width: 40, height: 40)
                .overlay(
                    Text(String(userName.prefix(1)))
                        .font(.title3)
                        .foregroundColor(.blue)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(userName)
                    .font(.headline)

                Text(phoneNumber)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()
        }
        .padding(.vertical, 4)
        .contentShape(Rectangle())
    }
}

