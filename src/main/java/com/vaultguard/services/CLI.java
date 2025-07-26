package com.vaultguard.services;

import java.util.Scanner;

public class CLI {
    private PasswordManager passwordManager;
    private Scanner scanner;
    private boolean isLoggedIn = false;
    private String currentUser = null;

    public CLI() {
        passwordManager = new PasswordManager(); 
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to VaultGuard!");

        while (true) {
            if (!isLoggedIn) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\n[1] Register\n[2] Login\n[3] Exit");
        System.out.print("Select an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
            case "register":
                handleRegister();
                break;
            case "2":
            case "login":
                handleLogin();
                break;
            case "3":
            case "exit":
                System.out.println("Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid option. Try again.");
        }
    }

    private void handleRegister() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine().trim();
        // TODO: call passwordManager.register(username, password)
        System.out.println("[Mock] Registered " + username + ". You can now log in.");
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        // TODO: boolean success = passwordManager.login(username, password)
        boolean success = true; // Mock result
        if (success) {
            System.out.println("Login successful! Welcome, " + username);
            isLoggedIn = true;
            currentUser = username;
        } else {
            System.out.println("Login failed. Try again.");
        }
    }

    private void showMainMenu() {
        System.out.println("\n[1] View Keys\n[2] Add Key\n[3] Edit Key\n[4] Copy Key\n[5] Delete Key\n[6] Logout\n[7] Help");
        System.out.print("Choose an action: ");
        String cmd = scanner.nextLine().trim();

        switch (cmd) {
            case "1":
            case "view":
                handleView();
                break;
            case "2":
            case "add":
                handleAdd();
                break;
            case "3":
            case "edit":
                handleEdit();
                break;
            case "4":
            case "copy":
                handleCopy();
                break;
            case "5":
            case "delete":
                handleDelete();
                break;
            case "6":
            case "logout":
                handleLogout();
                break;
            case "7":
            case "help":
                showHelp();
                break;
            default:
                System.out.println("Invalid command. Type 'help' to see all options.");
        }
    }

    // Placeholder methods 
    private void handleView()   { System.out.println("[Mock] Displaying all your keys... (call passwordManager.viewKeys())"); }
    private void handleAdd()    { System.out.println("[Mock] Adding a new key... (call passwordManager.addKey())"); }
    private void handleEdit()   { System.out.println("[Mock] Editing a key... (call passwordManager.editKey())"); }
    private void handleCopy()   { System.out.println("[Mock] Copying a key... (call passwordManager.copyKey())"); }
    private void handleDelete() { System.out.println("[Mock] Deleting a key... (call passwordManager.deleteKey())"); }
    private void handleLogout() {
        isLoggedIn = false;
        currentUser = null;
        // TODO: passwordManager.logout();
        System.out.println("You have been logged out.");
    }

    private void showHelp() {
        System.out.println("Available commands: view, add, edit, copy, delete, logout, help");
    }

    // Main method to launch CLI app (optional)
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }
}
