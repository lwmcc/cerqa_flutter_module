//
// ContactsTestView.swift
// cerqaiOS
//
// Test view to verify the shared Kotlin ContactsViewModel integration
//

import SwiftUI
import Shared
import Combine

/// Swift wrapper to observe Kotlin StateFlows and bridge them to SwiftUI
@MainActor
class SharedContactsViewModelWrapper: ObservableObject {
    private let kotlinViewModel: ContactsViewModel
    @Published var contacts: [Contact] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?

    private var cancellables = Set<AnyCancellable>()

    init() {
        // Get the ContactsViewModel from Koin
        self.kotlinViewModel = KoinKt.koin.get(objCClass: ContactsViewModel.self) as! ContactsViewModel

        // Observe the Kotlin StateFlows
        observeContacts()
        observeLoading()
        observeError()
    }

    private func observeContacts() {
        Task {
            for await contacts in kotlinViewModel.contacts {
                self.contacts = contacts as! [Contact]
            }
        }
    }

    private func observeLoading() {
        Task {
            for await loading in kotlinViewModel.isLoading {
                self.isLoading = loading.boolValue
            }
        }
    }

    private func observeError() {
        Task {
            for await error in kotlinViewModel.error {
                self.errorMessage = error
            }
        }
    }

    func fetchContacts() {
        kotlinViewModel.fetchContacts()
    }

    func searchContacts(query: String) -> [Contact] {
        return kotlinViewModel.searchContacts(query: query) as! [Contact]
    }

    func clearError() {
        kotlinViewModel.clearError()
    }
}

struct ContactsTestView: View {
    @StateObject private var viewModel = SharedContactsViewModelWrapper()
    @State private var searchText = ""
    @State private var showingError = false

    var body: some View {
        NavigationView {
            ZStack {
                if viewModel.isLoading {
                    VStack(spacing: 20) {
                        ProgressView()
                            .scaleEffect(1.5)
                        Text("Loading contacts...")
                            .font(.headline)
                            .foregroundColor(.secondary)
                    }
                } else if viewModel.contacts.isEmpty {
                    VStack(spacing: 20) {
                        Image(systemName: "person.2.slash")
                            .font(.system(size: 64))
                            .foregroundColor(.secondary)

                        Text("No Contacts")
                            .font(.title2)
                            .fontWeight(.semibold)

                        Text("Your contacts will appear here once you add them")
                            .font(.body)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 40)

                        Button(action: {
                            viewModel.fetchContacts()
                        }) {
                            Label("Retry", systemImage: "arrow.clockwise")
                                .padding(.horizontal, 20)
                                .padding(.vertical, 10)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                        }
                        .padding(.top, 20)
                    }
                } else {
                    List {
                        ForEach(filteredContacts, id: \.id) { contact in
                            ContactTestRow(contact: contact)
                        }
                    }
                    .listStyle(.insetGrouped)
                }
            }
            .navigationTitle("Contacts Test")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        viewModel.fetchContacts()
                    }) {
                        Image(systemName: "arrow.clockwise")
                    }
                    .disabled(viewModel.isLoading)
                }
            }
            .searchable(text: $searchText, prompt: "Search contacts")
            .alert("Error", isPresented: $showingError) {
                Button("OK") {
                    viewModel.clearError()
                }
            } message: {
                Text(viewModel.errorMessage ?? "An error occurred")
            }
            .onChange(of: viewModel.errorMessage) { newValue in
                showingError = newValue != nil
            }
            .onAppear {
                // Fetch contacts when view appears
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

struct ContactTestRow: View {
    let contact: Contact

    var body: some View {
        HStack(spacing: 12) {
            // Avatar circle
            Circle()
                .fill(Color.blue.opacity(0.2))
                .frame(width: 50, height: 50)
                .overlay(
                    Text(avatarInitial)
                        .font(.title3)
                        .fontWeight(.semibold)
                        .foregroundColor(.blue)
                )

            VStack(alignment: .leading, spacing: 4) {
                Text(displayName)
                    .font(.headline)

                if let phone = contact.phone {
                    Text(phone)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                } else if let email = contact.email {
                    Text(email)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }

                if let userName = contact.userName {
                    Text("@\(userName)")
                        .font(.caption)
                        .foregroundColor(.blue)
                }
            }

            Spacer()
        }
        .padding(.vertical, 8)
    }

    private var displayName: String {
        if let name = contact.name {
            return name
        }
        return "\(contact.firstName) \(contact.lastName)"
    }

    private var avatarInitial: String {
        let name = contact.firstName.isEmpty ? contact.lastName : contact.firstName
        return String(name.prefix(1).uppercased())
    }
}

#Preview {
    ContactsTestView()
}
