//
// HomeView.swift
// cerqaiOS
//
// Home screen - mirrors Android MainScreen
//

import SwiftUI

struct HomeView: View {
    @State private var searchText = ""

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 24) {
                    // App branding
                    VStack(spacing: 12) {
                        Image(systemName: "car.fill")
                            .font(.system(size: 80))
                            .foregroundColor(.blue)

                        Text("Car Club")
                            .font(.largeTitle)
                            .fontWeight(.bold)

                        Text("Welcome! Your home screen")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    .padding(.top, 40)

                    // Placeholder content
                    VStack(spacing: 16) {
                        InfoCard(
                            icon: "person.2.fill",
                            title: "Contacts",
                            description: "Manage your contacts"
                        )

                        InfoCard(
                            icon: "person.3.fill",
                            title: "Groups",
                            description: "Create and manage groups"
                        )

                        InfoCard(
                            icon: "message.fill",
                            title: "Messages",
                            description: "Chat with your contacts"
                        )
                    }
                    .padding(.horizontal)
                }
            }
            .navigationTitle("Home")
            .searchable(text: $searchText, prompt: "Search")
        }
    }
}

struct InfoCard: View {
    let icon: String
    let title: String
    let description: String

    var body: some View {
        HStack(spacing: 16) {
            Image(systemName: icon)
                .font(.system(size: 32))
                .foregroundColor(.blue)
                .frame(width: 60, height: 60)
                .background(Color.blue.opacity(0.1))
                .clipShape(Circle())

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                Text(description)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            Spacer()
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 8, x: 0, y: 2)
    }
}

