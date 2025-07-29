package com.vaultguard.cli;

import java.util.List;
import java.util.Scanner;

import com.vaultguard.controller.PasswordManager;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public class CLI {
    private PasswordManager passwordManager;
    private Scanner scanner;
    private boolean isLoggedIn = false;
    private String currentUser = "";

    public CLI() {
        passwordManager = new PasswordManager("vaults"); 
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
        System.out.print("\nType register, login, or exit: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "register":
                handleRegister();
                break;
            case "login":
                handleLogin();
                break;
            case "exit":
                System.out.println("\nGoodbye!");
                System.exit(0);
            default:
                System.out.println("\nInvalid option. Try again.");
        }
    }

private void handleRegister() {
    try {
        System.out.print("\nEnter new username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine().trim();

        boolean result = passwordManager.register(username, password);
        if (result) {
            System.out.println("\nRegistration Successful!");
            isLoggedIn = true;
            currentUser = username;
        } else {
            System.out.println("\nRegistration Failed.");
        }
    } catch (Exception e) {
        System.out.println("\nAn error occurred during registration: " + e.getMessage());
    }
}

    private void handleLogin() {
        try {
            System.out.print("\nEnter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            boolean result = passwordManager.login(username, password);
            if (result) {
                System.out.println("\nLogin Successful!");
                isLoggedIn = true;
                currentUser = username;
            } else {
                System.out.println("\nLogin Failed.");
            }
        } catch (Exception e) {
            System.out.println("\nAn error occurred during login: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        System.out.println("\nLogged in as " + currentUser);
        System.out.print("Choose an action (enter 'help' for all commands): ");
        String cmd = scanner.nextLine().trim();

        switch (cmd) {
            case "view":
                handleView();
                break;
            case "add":
                handleAdd();
                break;
            case "edit":
                handleEdit();
                break;
            case "copy":
                handleCopy();
                break;
            case "delete":
                handleDelete();
                break;
            case "logout":
                handleLogout();
                break;
            case "help":
                showHelp();
                break;
            case "exit":
                System.out.println("\nGoodbye!");
                System.exit(0);
            default:
                System.out.println("\nInvalid command. Type 'help' to see all options.");
        }
    }

    private void handleView() {
        try {
            List<String> keyNames = passwordManager.getVaultKeyNames();

            if (keyNames.isEmpty()) {
                System.out.println("\nNo keys found.");
                return;
            }

            System.out.println("\nYour keys:");
            for (int i = 0; i < keyNames.size(); i++) {
                System.out.println((i + 1) + ". " + keyNames.get(i));
            }
        } catch (Exception e) {
            System.out.println("\nFailed to retrieve keys: " + e.getMessage());
        }
    }


    private void handleAdd() {
        try {
            System.out.print("\nEnter key name: ");
            String keyName = scanner.nextLine().trim();

            System.out.println("\nChoose password option:");
            System.out.println("1. Generate a strong password");
            System.out.println("2. Enter your own password");
            System.out.print("Enter choice (1 or 2): ");
            String option = scanner.nextLine().trim();

            String keyValue;
            if (option.equals("1")) {
                keyValue = passwordManager.generateStrongPassword();
                System.out.println("\nGenerated password: " + keyValue);
            } else if (option.equals("2")) {
                System.out.print("\nEnter password: ");
                keyValue = scanner.nextLine().trim();
            } else {
                System.out.println("\nInvalid choice. Aborting.");
                return;
            }

            boolean success = passwordManager.addKey(keyName, keyValue);
            if (success) {
                System.out.println("\nKey added successfully.");
            } else {
                System.out.println("\nFailed to add key. It may already exist or be invalid.");
            }
        } catch (Exception e) {
            System.out.println("\nAn error occurred while adding the key: " + e.getMessage());
        }
    }

    private void handleEdit() {
        try {
            System.out.print("\nEnter the name of the key you want to edit: ");
            String keyName = scanner.nextLine().trim();

            System.out.println("\nChoose new password option:");
            System.out.println("1. Generate a strong password");
            System.out.println("2. Enter your own password");
            System.out.print("Enter choice (1 or 2): ");
            String option = scanner.nextLine().trim();

            String newValue;
            if (option.equals("1")) {
                newValue = passwordManager.generateStrongPassword();
                System.out.println("\nGenerated new password: " + newValue);
            } else if (option.equals("2")) {
                System.out.print("\nEnter new password: ");
                newValue = scanner.nextLine().trim();
            } else {
                System.out.println("\nInvalid choice. Aborting.");
                return;
            }

            boolean success = passwordManager.editKey(keyName, newValue);
            if (success) {
                System.out.println("\nKey updated successfully.");
            } else {
                System.out.println("\nFailed to update key. It may not exist or the input was invalid.");
            }
        } catch (Exception e) {
            System.out.println("\nAn error occurred while editing the key: " + e.getMessage());
        }
    }


    private void handleCopy() {
        try {
            System.out.print("\nEnter the name of the key you want to copy to clipboard: ");
            String keyName = scanner.nextLine().trim();

            String value = passwordManager.getKeyValue(keyName);
            if (value == null) {
                System.out.println("\nFailed to retrieve the key. It may not exist or is restricted.");
                return;
            }

            StringSelection selection = new StringSelection(value);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

            System.out.println("\nPassword for \"" + keyName + "\" has been copied to your clipboard.");
        } catch (Exception e) {
            System.out.println("\nAn error occurred while copying the key: " + e.getMessage());
        }
    }

    private void handleDelete() {
        try {
            System.out.print("\nEnter the name of the key you want to delete: ");
            String keyName = scanner.nextLine().trim();

            System.out.print("Are you sure you want to delete \"" + keyName + "\"? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("yes")) {
                System.out.println("\nDeletion cancelled.");
                return;
            }

            boolean success = passwordManager.deleteKey(keyName);
            if (success) {
                System.out.println("\nKey deleted successfully.");
            } else {
                System.out.println("\nFailed to delete key. It may not exist.");
            }
        } catch (Exception e) {
            System.out.println("\nAn error occurred while deleting the key: " + e.getMessage());
        }
    }


    private void handleLogout() {
        isLoggedIn = false;
        currentUser = "";
        passwordManager.logout();
        System.out.println("\nYou have been logged out.");
    }

    private void showHelp() {
        System.out.println("\nAvailable commands: view, add, edit, copy, delete, logout, help, exit");
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }
}
