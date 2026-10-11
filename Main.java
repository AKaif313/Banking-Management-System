import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final String DATA_FILE = "banking_data.dat";

    private static ArrayList<BankAccount> accounts = new ArrayList<>();
    private static ArrayList<String> transactions = new ArrayList<>();

    static class BankAccount implements Serializable {
        private static final long serialVersionUID = 1L;

        int accountNumber;
        String holderName;
        double balance;

        BankAccount(int accountNumber, String holderName, double balance) {
            this.accountNumber = accountNumber;
            this.holderName = holderName;
            this.balance = balance;
        }
    }

    public static void main(String[] args) {
        loadData();

        boolean running = true;

        System.out.println("==================================");
        System.out.println("    BANKING MANAGEMENT SYSTEM");
        System.out.println("==================================");

        while (running) {
            System.out.println("\n1. Create Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Search Account");
            System.out.println("4. Deposit Money");
            System.out.println("5. Withdraw Money");
            System.out.println("6. Transfer Money");
            System.out.println("7. Transaction History");
            System.out.println("8. Save and Exit");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> viewAccounts();
                case 3 -> searchAccount();
                case 4 -> depositMoney();
                case 5 -> withdrawMoney();
                case 6 -> transferMoney();
                case 7 -> transactionHistory();
                case 8 -> {
                    saveData();
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Enter 1-8.");
            }
        }

        sc.close();
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                double value = Double.parseDouble(input);

                if (Double.isFinite(value)) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Invalid number
            }

            System.out.println("Please enter a valid amount.");
        }
    }

    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    private static BankAccount findAccount(int accountNumber) {
        for (BankAccount account : accounts) {
            if (account.accountNumber == accountNumber) {
                return account;
            }
        }

        return null;
    }

    private static String currentTime() {
        return LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    private static void createAccount() {
        System.out.println("\n--- CREATE ACCOUNT ---");

        int number = readInt("Enter account number: ");

        if (number <= 0) {
            System.out.println("Account number must be positive.");
            return;
        }

        if (findAccount(number) != null) {
            System.out.println("Account number already exists.");
            return;
        }

        String name = readText("Enter account holder name: ");
        double balance = readDouble("Enter opening balance: ");

        if (balance < 0) {
            System.out.println("Balance cannot be negative.");
            return;
        }

        BankAccount account = new BankAccount(number, name, balance);
        accounts.add(account);

        transactions.add(currentTime() + " | Account created: "
                + number + " | Opening balance: Rs. " + balance);

        saveData();

        System.out.println("Account created successfully!");
        System.out.println("Account Number: " + number);
        System.out.println("Account Holder: " + name);
        System.out.printf("Balance: Rs. %.2f%n", balance);
    }

    private static void viewAccounts() {
        System.out.println("\n--- ALL ACCOUNTS ---");

        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        System.out.printf("%-15s %-25s %12s%n",
                "Account No.", "Holder Name", "Balance");

        for (BankAccount account : accounts) {
            System.out.printf("%-15d %-25s Rs. %9.2f%n",
                    account.accountNumber,
                    account.holderName,
                    account.balance);
        }
    }

    private static void searchAccount() {
        System.out.println("\n--- SEARCH ACCOUNT ---");

        int number = readInt("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("Account Number: " + account.accountNumber);
        System.out.println("Holder Name: " + account.holderName);
        System.out.printf("Balance: Rs. %.2f%n", account.balance);
    }

    private static void depositMoney() {
        System.out.println("\n--- DEPOSIT MONEY ---");

        int number = readInt("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        double amount = readDouble("Enter deposit amount: ");

        if (amount <= 0 || !Double.isFinite(account.balance + amount)) {
            System.out.println("Enter a valid positive amount.");
            return;
        }

        account.balance += amount;

        transactions.add(currentTime() + " | Deposit | Account: "
                + number + " | Amount: Rs. " + amount);

        saveData();

        System.out.printf("Deposit successful. New balance: Rs. %.2f%n",
                account.balance);
    }

    private static void withdrawMoney() {
        System.out.println("\n--- WITHDRAW MONEY ---");

        int number = readInt("Enter account number: ");
        BankAccount account = findAccount(number);

        if (account == null) {
            System.out.println("Account not found.");
            return;
        }

        double amount = readDouble("Enter withdrawal amount: ");

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > account.balance) {
            System.out.println("Insufficient balance.");
            return;
        }

        account.balance -= amount;

        transactions.add(currentTime() + " | Withdrawal | Account: "
                + number + " | Amount: Rs. " + amount);

        saveData();

        System.out.printf("Withdrawal successful. Remaining balance: Rs. %.2f%n",
                account.balance);
    }

    private static void transferMoney() {
        System.out.println("\n--- TRANSFER MONEY ---");

        int senderNumber = readInt("Enter sender account number: ");
        BankAccount sender = findAccount(senderNumber);

        if (sender == null) {
            System.out.println("Sender account not found.");
            return;
        }

        int receiverNumber = readInt("Enter receiver account number: ");
        BankAccount receiver = findAccount(receiverNumber);

        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }

        if (senderNumber == receiverNumber) {
            System.out.println("Sender and receiver must be different.");
            return;
        }

        double amount = readDouble("Enter transfer amount: ");

        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }

        if (amount > sender.balance) {
            System.out.println("Insufficient balance.");
            return;
        }

        if (!Double.isFinite(receiver.balance + amount)) {
            System.out.println("Transfer amount is too large.");
            return;
        }

        sender.balance -= amount;
        receiver.balance += amount;

        transactions.add(currentTime() + " | Transfer | From: "
                + senderNumber + " | To: " + receiverNumber
                + " | Amount: Rs. " + amount);

        saveData();

        System.out.println("Money transferred successfully!");
        System.out.printf("Sender balance: Rs. %.2f%n", sender.balance);
        System.out.printf("Receiver balance: Rs. %.2f%n", receiver.balance);
    }

    private static void transactionHistory() {
        System.out.println("\n--- TRANSACTION HISTORY ---");

        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
            return;
        }

        for (String transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {

            out.writeObject(accounts);
            out.writeObject(transactions);

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadData() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {

            accounts = (ArrayList<BankAccount>) in.readObject();
            transactions = (ArrayList<String>) in.readObject();

        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
            accounts = new ArrayList<>();
            transactions = new ArrayList<>();
        }
    }
}