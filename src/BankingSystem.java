import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Banking System ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Display Account Details");
            System.out.println("5. Show Last 5 Transactions");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();  // Clear the invalid input
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter holder's name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter initial deposit amount: ");
                    double deposit = scanner.nextDouble();
                    long accountNumber = BankAccount.createAccount(name, deposit);
                    if (accountNumber != -1) {
                        System.out.println("Your account number is: " + accountNumber);
                    } else {
                        System.out.println("Failed to create account.");
                    }
                    break;


                case 2:
                    System.out.print("Enter account number: ");
                    long accountNumberDeposit = scanner.nextLong();

                    if (accountExists(accountNumberDeposit)) {
                        System.out.print("Enter deposit amount: ");
                        double depositAmount = scanner.nextDouble();
                        BankAccount accountDeposit = new BankAccount(accountNumberDeposit);
                        accountDeposit.deposit(depositAmount);
                    } else {
                        System.out.println("Account does not exist.");
                    }
                    break;

                case 3:
                    System.out.print("Enter account number: ");
                    long accountNumberWithdraw = scanner.nextLong();
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    BankAccount accountWithdraw = new BankAccount(accountNumberWithdraw);
                    accountWithdraw.withdraw(withdrawAmount);
                    break;

                case 4:
                    System.out.print("Enter account number to view details: ");
                    long accountNumberDetails = scanner.nextLong();
                    BankAccount.displayAccountDetails(accountNumberDetails);
                    break;

                case 5:
                    System.out.print("Enter account number: ");
                    long accountNumberHistory = scanner.nextLong();

                    if (!accountExists(accountNumberHistory)) {
                        System.out.println("Account does not exist.");
                        break;
                    }

                    showLastFiveTransactions(accountNumberHistory);
                    break;

                case 6:
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Checks if the account exists in the database
    public static boolean accountExists(long accountNumber) {
        String query = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // Displays the last 5 transactions for a given account number
    public static void showLastFiveTransactions(long accountNumber) {
        String query = "SELECT type, amount, timestamp FROM transactions WHERE account_number = ? ORDER BY timestamp DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Last 5 Transactions ---");
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String time = rs.getTimestamp("timestamp").toString();
                System.out.println(type + " of " + amount + " at " + time);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
