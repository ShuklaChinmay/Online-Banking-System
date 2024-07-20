import java.sql.*;
import java.util.Scanner;

public class online_banking_system {
    public static void main(String...k)throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_banking_system", "root", "chinmay@2004");
        int ch;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("\nWelcome To Your Banking System\n");
            System.out.println("1. Create a new Account \n" +
                    "2. Update Your Account\n" +
                    "3. Delete an Account\n" +
                    "4. Deposit Money\n"+
                    "5. Withdraw amount\n"+
                    "6. Balance Enquiry\n"+
                    "7. View All Account Holders\n"+
                    "8. Exit");
            System.out.print("Enter your choice = ");
            ch = sc.nextInt();
            sc.nextLine();

            switch(ch){
                case 1:  // create a new account
                    String q = create_account(sc);
                    PreparedStatement pst = con.prepareStatement(q);

                    int i = pst.executeUpdate();
                    if(i > 0){
                        System.out.print("\t\t Your account has been Created\n");
                    }
                    break;

                case 2:  // Update Your Account
                    System.out.print("Enter Your Account Number = ");
                    int accountNumber = sc.nextInt();
                    sc.nextLine();

                    String a = update(sc);
                    PreparedStatement ptv = con.prepareStatement(a);
                    switch (a) {
                        case "update bank set name = ? where account_number = ?":
                            System.out.print("Enter the new Name: ");
                            String newName = sc.nextLine();
                            ptv.setString(1, newName);
                            break;
                        case "update bank set mobile_number = ? where account_number = ?":
                            System.out.print("Enter the new Mobile Number: ");
                            long newMobileNumber = sc.nextLong();
                            sc.nextLine();
                            ptv.setLong(1, newMobileNumber);
                            break;
                        case "update bank set city = ? where account_number = ?":
                            System.out.print("Enter the new City: ");
                            String newCity = sc.nextLine();
                            ptv.setString(1, newCity);
                            break;
                    }
                    ptv.setInt(2, accountNumber);
                    int j = ptv.executeUpdate();
                    if(j > 0){
                        System.out.print("\t\t Your account has been Updated\n");
                    }
                    else{
                        System.out.println("Account Not Found");
                    }
                    break;

                case 3:     // Delete an account
                    System.out.print("Enter the account number = ");
                    int acc = sc.nextInt();
                    String delete = "delete from bank where account_number = ?";
                    PreparedStatement del = con.prepareStatement(delete);
                    del.setInt(1,acc);

                    int l = del.executeUpdate();
                    if(l > 0){
                        System.out.print("\t\t Your account has been Deleted\n");
                    }
                    else{
                        System.out.println("Account Not Found");
                    }
                    break;

                case 4: // Deposit Money
                    String depositQuery = depositMoney(sc);
                    PreparedStatement depositStmt = con.prepareStatement(depositQuery);
                    int w = depositStmt.executeUpdate();
                    if(w > 0){
                        System.out.print("\t\t Money has been deposited into your account\n");
                    }
                    else{
                        System.out.println("Account Not Found");
                    }
                    break;

                case 5: // Withdraw amount
                    withdraw_amount(sc, con);
                    break;

                case 6:     // Balance Enquiry
                    balanceEnquiry(sc, con);
                    break;

                case 7:      // View All Account Holders
                    viewAllAccountHolders(con);
                    break;

                case 8: // Exit
                    System.out.println("Exiting...");
                    break;




                default:
                    System.out.println("Enter Valid Choice");
            }
        } while(ch != 8);
        sc.close();
    }

    static String create_account(Scanner sc) {
        System.out.println("\n\tTo create a new account fill the details given below :-\n");
        System.out.print("Enter your name = ");
        String name = sc.nextLine();

        System.out.print("Enter your mobile number = ");
        long mobile_number = sc.nextLong();
        sc.nextLine();

        System.out.print("Enter the city = ");
        String city = sc.nextLine();

        System.out.print("Enter the amount = ");
        long amount = sc.nextLong();
        sc.nextLine();

        String q  = "insert into bank(name, mobile_number, city, amount) values('" + name + "', '" + mobile_number + "', '" + city + "', '" + amount + "')";
        return q;
    }

    static String update(Scanner sc){

        System.out.println("What you want to update :\n\t1. Name\n\t2. Mobile Number\n\t3. City");
        System.out.print("Enter Your Choice : ");
        int choice = sc.nextInt();
        sc.nextLine();
        String p = "";
        switch (choice) {
            case 1:
                p = "update bank set name = ? where account_number = ?";
                break;
            case 2:
                p = "update bank set mobile_number = ? where account_number = ?";
                break;
            case 3:
                p = "update bank set city = ? where account_number = ?";
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
        return p;
    }

    static String depositMoney(Scanner sc) {
        System.out.print("Enter Your Account Number = ");
        int accountNum = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter the amount to deposit = ");
        long depositAmount = sc.nextLong();
        sc.nextLine();
        String q = "update bank set amount = amount + " + depositAmount + " where account_number = " + accountNum;
        return q;
    }

    static void withdraw_amount(Scanner sc, Connection con)throws SQLException {
        System.out.print("Enter Your Account Number = ");
        int accountNumber = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Amount to Withdraw = ");
        long amount = sc.nextLong();
        sc.nextLine();

        // Check if the account exists and has sufficient balance
        String checkBalanceQuery = "SELECT amount FROM bank WHERE account_number = ?";
        PreparedStatement checkBalanceStmt = con.prepareStatement(checkBalanceQuery);
        checkBalanceStmt.setInt(1, accountNumber);
        ResultSet rs = checkBalanceStmt.executeQuery();

        if (((ResultSet) rs).next()) {
            long currentBalance = rs.getLong("amount");

            if (currentBalance >= amount) {
                String withdrawQuery = "UPDATE bank SET amount = amount - ? WHERE account_number = ?";
                PreparedStatement withdrawStmt = con.prepareStatement(withdrawQuery);
                withdrawStmt.setLong(1, amount);
                withdrawStmt.setInt(2, accountNumber);

                int k = withdrawStmt.executeUpdate();
                if (k > 0) {
                    System.out.print("\t\t Amount Withdrawn Successfully\n");
                }
            } else {
                System.out.print("\t\t Insufficient Balance\n");
            }
        } else {
            System.out.print("\t\t Account Not Found\n");
        }

    }

    static void balanceEnquiry(Scanner sc, Connection con) throws SQLException {
        System.out.print("Enter Your Account Number = ");
        int accountNumber = sc.nextInt();
        String query = "select amount from bank where account_number = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setInt(1, accountNumber);

        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            long balance = rs.getLong("amount");
            System.out.println("\t\t Your account balance is: " + balance);
        } else {
            System.out.println("\t\t Account not found.");
        }
    }

    static void viewAllAccountHolders(Connection con)throws SQLException {
        String query = "SELECT account_number, name, mobile_number, city, amount FROM bank";
        PreparedStatement pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        System.out.println("\nAccount Holders List:");
        System.out.println("Account Number | Name | Mobile Number | City | Amount");
        while (rs.next()) {
            int accountNumber = rs.getInt("account_number");
            String name = rs.getString("name");
            long mobileNumber = rs.getLong("mobile_number");
            String city = rs.getString("city");
            long amount = rs.getLong("amount");
            System.out.printf("%d | %s | %d | %s | %d\n", accountNumber, name, mobileNumber, city, amount);
        }
    }






















}