/*
 * MotorPH Payroll System
 * Group: Debuggers
 * * This program:
 * 1. Reads Employee and Attendance CSV files.
 * 2. Asks the user to login as Admin or Employee.
 * 3. Computes weekly salary minus SSS, PhilHealth, Pag-IBIG, and Tax.
 */

package src;

import java.util.*; 
import java.io.*;  

public class Motorph_phase1 {

    // This part stores employee details from the CSV file into arrays
    static String[] empId = new String[50];
    static String[] empFullName = new String[50];
    static double[] hourlyRate = new double[50];
    static String[] birthday = new String[50];
    
    // This variable remembers who is currently logged in
    static String loggedInUser = ""; 

    public static void main(String[] args) {
        // This code loads the employee data first before anything else
        readEmployeeData();

        Scanner scan = new Scanner(System.in);
        boolean systemRunning = true;

        while (systemRunning) {
            // This code handles the login process
            String role = login(scan);

            if (role.equals("FAILED")) {
                System.out.println("Exiting System...\n");
                break; 
            }

            // This code keeps the user inside the menu until they logout
            boolean loggedIn = true;
            while (loggedIn) {
                if (role.equals("ADMIN")) {
                    loggedIn = showAdminMenu(scan);
                } else if (role.equals("EMPLOYEE")) {
                    loggedIn = showEmployeeMenu(scan);
                }
            }
        }
        
        scan.close();
        System.out.println("System Shutdown Successful.");
    }


    // LOGIN AND MENU CODE


    // This code checks if the username and password are correct
    public static String login(Scanner scan) {
        int attempts = 3;
        System.out.println("\n\"----- MotorPH Login System -----\"");

        while (attempts > 0) {
            System.out.print("Username: ");
            String inputUser = scan.nextLine();

            System.out.print("Password: ");
            String inputPass = scan.nextLine();

            // This code is for Admin login
            if (inputUser.equals("payroll_staff") && inputPass.equals("12345")) {
                System.out.println("\nLogin Successful! Welcome, Administrator.");
                loggedInUser = "admin";
                return "ADMIN";
            } 
            // This code is for Employee login
            else if (inputUser.equals("employee") && inputPass.equals("12345")) {
                System.out.println("\nLogin Successful! Welcome, Employee.");
                loggedInUser = "10001"; 
                return "EMPLOYEE";
            } 
            else {
                attempts--;
                System.out.println("Invalid credentials. Attempts left: " + attempts + "\n");
            }
        }
        return "FAILED";
    }

    // This code shows the options for Admin users
    public static boolean showAdminMenu(Scanner scan) {
        System.out.println("\n===== MotorPH Admin Menu =====");
        System.out.println("[1] Compute Payroll for Employee");
        System.out.println("[2] View Employee List");
        System.out.println("[3] Logout");
        System.out.print("Select an option: ");
        
        String choice = scan.nextLine();
        
        switch (choice) {
            case "1":
                System.out.print("\nEnter Employee ID: ");
                String searchId = scan.nextLine();
                processPayroll(searchId);
                pause(scan);
                break;
            case "2":
                // This code prints all employees stored in the array
                for (int i = 0; i < empId.length; i++) {
                    if (empId[i] != null) {
                        System.out.println("ID: " + empId[i] + " | Name: " + empFullName[i]);
                    }
                }
                pause(scan);
                break;
            case "3":
                return false; // This code logs out the user
            default:
                System.out.println("Invalid choice.");
                pause(scan);
                break;
        }
        return true; 
    }

    // This code shows the options for Employee users
    public static boolean showEmployeeMenu(Scanner scan) {
        System.out.println("\n===== MotorPH Employee Menu =====");
        System.out.println("[1] View My Payroll");
        System.out.println("[2] Logout");
        System.out.print("Select an option: ");
        
        String choice = scan.nextLine();
        
        switch (choice) {
            case "1":
                processPayroll(loggedInUser); 
                pause(scan);
                break;
            case "2":
                return false; 
            default:
                System.out.println("Invalid choice.");
                pause(scan);
                break;
        }
        return true;
    }

    // This code just waits for the user to press Enter
    private static void pause(Scanner scan) {
        System.out.println("\nPress Enter to continue...");
        scan.nextLine();
    }

  
    // DATA PROCESSING CODE


    // This code reads the EmployeeData.csv file and fills up our arrays
    public static void readEmployeeData() {
        try (BufferedReader br = new BufferedReader(new FileReader("EmployeeData.csv"))) {
            String line = br.readLine(); 
            int i = 0;

            while ((line = br.readLine()) != null && i < empId.length) {
                // This code splits the CSV line into parts
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length > 18) {
                    empId[i] = data[0].replaceAll("\"", "").trim();
                    empFullName[i] = data[2].replace("\"", "").trim() + " " + data[1].replace("\"", "").trim();
                    birthday[i] = data[3].replaceAll("\"", "").trim();

                    // This code converts the hourly rate string into a number
                    String hourlyRateStr = data[18].replaceAll("\"", "").replaceAll(",", "").trim();
                    hourlyRate[i] = Double.parseDouble(hourlyRateStr);
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading file.");
        }
    }

    // This code is the main process to compute for sweldo and deductions
    public static void processPayroll(String id) {
        int index = -1;

        // This code looks for the employee index using the ID
        for (int i = 0; i < empId.length; i++) {
            if (empId[i] != null && empId[i].equals(id)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("Employee not found.");
            return;
        }

        // This code gets the total hours from the attendance file
        double totalHours = calculateHrsFromCSV(id);
        double grossWage = totalHours * hourlyRate[index];

        // This code computes for government deductions
        double estimatedMonthly = grossWage * 4;
        double sss = computeSSS(estimatedMonthly) / 4;
        double ph = computePhilHealth(estimatedMonthly) / 4;
        double pi = computePagIbig(estimatedMonthly) / 4;

        // This code computes for the withholding tax
        double taxableWeekly = grossWage - (sss + ph + pi);
        double tax = computeTax(taxableWeekly * 4) / 4;

        double net = grossWage - (sss + ph + pi + tax);

        displaySummary(empFullName[index], birthday[index], totalHours, grossWage, sss, ph, pi, tax, net);
    }

    // This code reads the Attendance.csv to count how many hours the employee worked
    private static double calculateHrsFromCSV(String searchID) {
        double totalHours = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("Attendance.csv"))) {
            String line = br.readLine(); 
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data[0].replaceAll("\"", "").trim().equals(searchID)) {
                    totalHours += calculateHrs(data[4].replaceAll("\"", ""), data[5].replaceAll("\"", ""));
                }
            }
        } catch (Exception e) { }
        return totalHours;
    }

    // This code computes hours worked and deducts 1 hour for lunch
    public static double calculateHrs(String timeIn, String timeOut) {
        if (timeIn.equals("0:00") || timeOut.equals("0:00")) return 0;

        int inMin = timeToMinutes(timeIn);
        int outMin = timeToMinutes(timeOut);

        // This code applies the 10-minute grace period
        int actualIn = (inMin <= timeToMinutes("08:10")) ? timeToMinutes("08:00") : inMin;

        // This code computes the result and subtracts 60 minutes for lunch
        double totalMinutes = outMin - actualIn - 60;
        return (totalMinutes / 60.0 < 0) ? 0 : totalMinutes / 60.0;
    }

 
    // DEDUCTION CALCULATORS

    // This code computes for SSS contribution
    public static double computeSSS(double gross) {
        if (gross <= 3250) return 135.00;
        if (gross >= 24750) return 1125.00;
        return gross * 0.045;
    }

    // This code computes for PhilHealth (half share)
    public static double computePhilHealth(double gross) {
        double monthlyGross = gross * 4;
        return (monthlyGross * 0.03 * 0.50) / 4;
    }

    // This code computes for Pag-IBIG (max 100)
    public static double computePagIbig(double gross) {
        double contribution = gross * 0.02;
        return (contribution > 100) ? 100 : contribution;
    }

    // This code computes for the BIR Tax
    public static double computeTax(double taxable) {
        if (taxable <= 20833) return 0;
        return (taxable - 20833) * 0.20;
    }

    // This code converts "08:30" into 510 minutes
    public static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // This code prints the final payroll result on the screen
    public static void displaySummary(String name, String bday, double hrs, double gross,
                                      double sss, double ph, double pi, double tax, double net) {
    	System.out.println("\n--- MotorPH Payroll Summary ---");
        System.out.println("Name: " + name);
        System.out.println("Birthday: " + bday);
        System.out.printf("Total Hours: %.2f\n", hrs);
        System.out.printf("Gross Pay: %.2f\n", gross);

        System.out.println("\n--- Deductions ---");
        System.out.printf("SSS: PHP %.2f\n", sss);
        System.out.printf("PhilHealth: %.2f\n", ph);
        System.out.printf("Pag-IBIG: %.2f\n", pi);
        System.out.printf("Tax: %.2f\n", tax);

        double totalDeductions = sss + ph + pi + tax;

        System.out.println("");
        System.out.printf("Total Deductions: %.2f\n", totalDeductions);
        System.out.printf("Net Pay: %.2f\n", net);
  
    }
}
