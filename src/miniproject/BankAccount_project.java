package miniproject;

import java.io.*;
import java.util.*;

class BankAccount {
    private String accountHolderName;
    private String accountNumber;
    private double balance;

    public BankAccount(String name, String accNumber, double balance) {
        this.accountHolderName = name;
        this.accountNumber = accNumber;
        this.balance = balance;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("✅ Successfully deposited ₹" + amount);
        } else {
            System.out.println("❌ Invalid deposit amount!");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("✅ Successfully withdrawn ₹" + amount);
        } else if (amount > balance) {
            System.out.println("❌ Insufficient balance!");
        } else {
            System.out.println("❌ Invalid withdrawal amount!");
        }
    }

    public void displayAccountInfo() {
        System.out.println("\n--- Account Information ---");
        System.out.println("Account Holder: " + accountHolderName);
        System.out.println("Account Number: " + accountNumber);
        System.out.printf("Balance: ₹%.2f%n", balance);
    }

    public String toFileString() {
        return accountHolderName + "," + accountNumber + "," + balance;
    }

    public static BankAccount fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            String name = parts[0];
            String accNo = parts[1];
            double bal = Double.parseDouble(parts[2]);
            return new BankAccount(name, accNo, bal);
        }
        return null;
    }
}

public class BankAccount_project {
    private static final String FILE_NAME = "accounts.txt";

    // Load all accounts from file
    private static List<BankAccount> loadAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return accounts;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                BankAccount acc = BankAccount.fromFileString(line);
                if (acc != null) accounts.add(acc);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return accounts;
    }

    // Save all accounts to file
    private static void saveAccounts(List<BankAccount> accounts) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (BankAccount acc : accounts) {
                bw.write(acc.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    // Find account by account number
    private static BankAccount findAccount(List<BankAccount> accounts, String accNumber) {
        for (BankAccount acc : accounts) {
            if (acc.getAccountNumber().equals(accNumber)) return acc;
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<BankAccount> accounts = loadAccounts();

        System.out.println("===== Welcome to the Multi-User Bank System =====");

        while (true) {
            System.out.println("\n1. Create New Account");
            System.out.println("2. Login to Existing Account");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter your name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter a new account number: ");
                    String newAccNo = sc.nextLine();

                    // Check if account number already exists
                    if (findAccount(accounts, newAccNo) != null) {
                        System.out.println("Account number already exists! Try again.");
                        break;
                    }

                    BankAccount newAccount = new BankAccount(name, newAccNo, 0.0);
                    accounts.add(newAccount);
                    saveAccounts(accounts);
                    System.out.println("Account created successfully!");
                    break;

                case 2:
                    System.out.print("Enter your account number: ");
                    String accNo = sc.nextLine();
                    BankAccount current = findAccount(accounts, accNo);

                    if (current == null) {
                        System.out.println("Account not found!");
                        break;
                    }

                    System.out.println("\nWelcome back, " + current.getAccountHolderName() + "!");
                    boolean logout = false;

                    while (!logout) {
                        System.out.println("\n===== BANK MENU =====");
                        System.out.println("1. Deposit");
                        System.out.println("2. Withdraw");
                        System.out.println("3. Check Balance");
                        System.out.println("4. Display Account Info");
                        System.out.println("5. Logout");
                        System.out.print("Enter your choice: ");

                        int option;
                        try {
                            option = sc.nextInt();
                            sc.nextLine();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input! Please enter a number.");
                            sc.nextLine();
                            continue;
                        }

                        switch (option) {
                            case 1:
                                System.out.print("Enter deposit amount: ");
                                try {
                                    double dep = sc.nextDouble();
                                    sc.nextLine();
                                    current.deposit(dep);
                                    saveAccounts(accounts);
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid amount!");
                                    sc.nextLine();
                                }
                                break;

                            case 2:
                                System.out.print("Enter withdrawal amount: ");
                                try {
                                    double wit = sc.nextDouble();
                                    sc.nextLine();
                                    current.withdraw(wit);
                                    saveAccounts(accounts);
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid amount!");
                                    sc.nextLine();
                                }
                                break;

                            case 3:
                                System.out.printf("Current Balance: ₹%.2f%n", current.getBalance());
                                break;

                            case 4:
                                current.displayAccountInfo();
                                break;

                            case 5:
                                saveAccounts(accounts);
                                logout = true;
                                System.out.println("Logged out successfully!");
                                break;

                            default:
                                System.out.println("Invalid choice! Try again.");
                        }
                    }
                    break;

                case 3:
                    saveAccounts(accounts);
                    System.out.println("Thank you for using the Bank System. Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
