//
// TestUsers.swift
// cerqaiOS
//
// Test user data matching Android testUser functions
//

import Foundation

struct TestUser {
    let username: String
    let email: String
    let password: String
    let phone: String
    let firstName: String
    let lastName: String

    // Test User 1 - matches Android testUser1
    static let larry = TestUser(
        username: "LarryM",
        email: "larry@cerqa.net",
        password: "Test1234!",
        phone: "+14808104545",
        firstName: "Larry",
        lastName: "McCarty"
    )

    // Test User 2 - matches Android testUser2
    static let lebron = TestUser(
        username: "KingJames",
        email: "admin@cerqa.com",
        password: "Test1234!",
        phone: "+14805554545",
        firstName: "Lebron",
        lastName: "James"
    )

    // Quick access list
    static let allTestUsers = [larry, lebron]
}

// Extension for easy sign up
extension TestUser {
    func signUpAttributes() -> [String: String] {
        return [
            "email": email,
            "phone_number": phone,
            "given_name": firstName,
            "family_name": lastName
        ]
    }
}

// MARK: - Usage Example
/*
 // Sign up test user 1
 let testUser = TestUser.larry
 await authManager.signUp(
     username: testUser.username,
     password: testUser.password,
     email: testUser.email,
     phoneNumber: testUser.phone
 )

 // Or use directly in UI
 TextField("Username", text: .constant(TestUser.larry.username))
 */
