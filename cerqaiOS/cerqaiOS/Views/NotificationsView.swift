//
// NotificationsView.swift
// cerqaiOS
//
// Notifications screen - mirrors Android NotificationScreen
//

import SwiftUI

struct NotificationsView: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                Image(systemName: "bell.fill")
                    .font(.system(size: 60))
                    .foregroundColor(.gray)
                Text("No Notifications")
                    .font(.title2)
                    .fontWeight(.semibold)
                Text("You're all caught up!")
                    .font(.body)
                    .foregroundColor(.secondary)
            }
            .navigationTitle("Notifications")
        }
    }
}

