import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAccount {

    private final long accountNumber;

    // Constructor for existing accounts (with account number)
    public BankAccount(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    // Method to create a new account
    // Method to create a new account with a random account number between 2000 and 50000
    public static long createAccount(String holderName, double balance) {
        // Generate a random account number between 2000 and 50000
        long accountNumber = 2000 + (long) (Math.random() * (50000 - 2000 + 1));

        String query = "INSERT INTO accounts (account_number, holder_name, balance) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, accountNumber);  // Set the randomly generated account number
            stmt.setString(2, holderName);    // Set the holder's name
            stmt.setDouble(3, balance);       // Set the initial balance
            stmt.executeUpdate();

            System.out.println("Account Created Successfully! Account Number: " + accountNumber);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return accountNumber;  // Return the generated account number
    }


    // Method to deposit money into account
    public void deposit(double amount) {
        String query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setLong(2, accountNumber);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deposited " + amount + " successfully!");
                logTransaction("Deposit", amount);
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Method to withdraw money from account
    public void withdraw(double amount) {
        String query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setLong(2, accountNumber);
            stmt.setDouble(3, amount);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Withdrawn " + amount + " successfully!");
                logTransaction("Withdraw", amount);
            } else {
                System.out.println("Insufficient balance or account not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Log the transaction to the database
    private void logTransaction(String type, double amount) {
        String query = "INSERT INTO transactions(account_number, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, accountNumber);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Failed to log transaction: " + e.getMessage());
        }
    }

    // Method to display account details
    public static void displayAccountDetails(long accountNumber) {
        String query = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Account Number: " + rs.getLong("account_number"));
                System.out.println("Holder Name: " + rs.getString("holder_name"));
                System.out.println("Balance: " + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Additional helper method for account validation (optional)
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
}
