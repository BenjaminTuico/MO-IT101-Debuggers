/*
 * MotorPH Payroll System
 * Group: Debuggers
 * Date: March 23, 2026
 *
 * Description:
 * This program reads employee and attendance data from CSV files,
 * computes payroll based on actual worked hours,
 * and applies government deductions (SSS, PhilHealth, Pag-IBIG, Tax).
 *
 * Features:
 * - Login system
 * - Attendance-based payroll computation
 * - Government deductions
 * - Payroll summary display
 */

package src;

import java.util.*; // Import utilities such as Scanner for user input
import java.io.*;  // Import classes for reading CSV files (BufferedReader, FileReader)

public class Motorph_phase1 {

    // Handles user login authentication with limited attempts
    public static boolean login() {
        Scanner scan = new Scanner(System.in);
        String userName = "employee";
        String pass = "1234";
        int attempts = 3;

        System.out.println("\"----- MotorPH Login System -----\"");

        // Loop until login is successful or attempts are exhausted
        while (attempts > 0) {
            System.out.println("Username: ");
            String inputUser = scan.next();

            System.out.println("Password: ");
            String inputPass = scan.next();

            // Validate credentials
            if (inputUser.equals(userName) && inputPass.equals(pass)) {
                System.out.println("Login Successful! Welcome " + userName);
                System.out.println("");
                return true;
            } else {
                attempts--;
                System.out.println("Invalid credentials. Attempts left: " + attempts + "\n");
            }
        }

        // If all attempts fail
        System.out.println("Too many failed attempts. Access Denied.");
        return false;
    }

    public static void main(String[] args) {

        // Execute login system
        if (!login()) {
            System.exit(0);
        }

        // Arrays to store up to 50 employee records
        String[] empId = new String[50];         // Stores employee IDs
        String[] empFullName = new String[50];   // Stores employee full names
        double[] hourlyRate = new double[50];    // Stores hourly rates
        String[] birthday = new String[50];      // Stores employee birthdays

        // Load employee data from CSV file into arrays
        readEmployeeData(empId, birthday, empFullName, hourlyRate);

        Scanner scan = new Scanner(System.in);

        // Prompt user to enter employee ID
        System.out.println("Enter Employee ID: ");
        String searchId = scan.nextLine();

        // Process payroll for the selected employee
        processPayroll(searchId, empId, birthday, empFullName, hourlyRate);

        scan.close();
    }

    // Reads attendance CSV file and computes total worked hours for a specific employee
    private static double calculateHrsFromCSV(String searchID) {
        double totalHours = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("Attendance.csv"))) {

            String line = br.readLine(); // Skip header row

            // Read each record in the file
            while ((line = br.readLine()) != null) {

                // Split CSV while handling quoted values
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                String id = data[0].replaceAll("\"", "").trim();

                // Match employee ID
                if (id.equals(searchID)) {

                    String date = data[3].replaceAll("\"", "").trim();

                    // Filter records for June 2024
                    if (date.contains("/06/2024")) {

                        String timeIn = data[4].replaceAll("\"", "").trim();
                        String timeOut = data[5].replaceAll("\"", "").trim();

                        // Ignore invalid time entries
                        if (!timeIn.equals("0:00") && !timeOut.equals("0:00")) {
                            totalHours += calculateHrs(timeIn, timeOut);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading Attendance: " + e.getMessage());
        }

        return totalHours;
    }

    // Computes working hours based on time-in and time-out rules
    public static double calculateHrs(String timeIn, String timeOut) {

        // Ignore invalid entries
        if (timeIn.equals("0:00") || timeOut.equals("0:00")) return 0;

        int inMin = timeToMinutes(timeIn);
        int outMin = timeToMinutes(timeOut);

        int startShift = timeToMinutes("08:00");   // Standard start time
        int gracePeriod = timeToMinutes("08:05");  // Grace period for late arrivals
        int endShift = timeToMinutes("17:00");     // Standard end time

        // Apply grace period rule
        int actualIn = (inMin <= gracePeriod) ? startShift : inMin;

        // Enforce shift boundaries
        if (actualIn < startShift) actualIn = startShift;
        if (outMin > endShift) outMin = endShift;
        if (outMin < actualIn) return 0;

        // Compute total worked minutes minus 1-hour lunch break
        double totalMinutes = outMin - actualIn - 60;
        double hours = totalMinutes / 60.0;

        return (hours < 0) ? 0 : hours;
    }

    // Computes SSS contribution based on salary brackets
    public static double computeSSS(double gross) {
        if (gross <= 3250) return 135.00;
        if (gross >= 24750) return 1125.00;
        return gross * 0.045;
    }

    // Computes PhilHealth contribution based on monthly salary estimate
    public static double computePhilHealth(double gross) {
        double monthlyGross = gross * 4;
        double contribution;

        if (monthlyGross <= 1000) {
            contribution = 300.00;
        } else if (monthlyGross >= 90000) {
            contribution = 900.00;
        } else {
            contribution = monthlyGross * 0.03;
        }

        return (contribution * 0.50) / 4;
    }

    // Computes Pag-IBIG contribution with a capped value
    public static double computePagIbig(double gross) {
        double rate = (gross <= 1500) ? 0.01 : 0.02;

        double contribution = gross * rate;

        if (contribution > 100) {
            contribution = 100;
        }

        return contribution;
    }

    // Computes withholding tax based on taxable income
    public static double computeTax(double taxable) {
        if (taxable <= 20833) return 0;
        if (taxable <= 33332) return (taxable - 20833) * 0.20;
        if (taxable <= 66666) return 2500 + (taxable - 33333) * 0.25;
        return (taxable - 66667) * 0.30 + 10833;
    }

    // Converts time in HH:mm format into total minutes
    public static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // Reads employee data from CSV file and stores it into arrays
    public static void readEmployeeData(String[] id, String[] bday, String[] fullName, double[] rate) {
        try (BufferedReader br = new BufferedReader(new FileReader("EmployeeData.csv"))) {

            String line = br.readLine(); // Skip header row
            int i = 0;

            while ((line = br.readLine()) != null && i < id.length) {

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length > 18) {
                    id[i] = data[0].replaceAll("\"", "").trim();
                    fullName[i] = data[2].replace("\"", "").trim() + " " +
                                  data[1].replace("\"", "").trim();
                    bday[i] = data[3].replaceAll("\"", "").trim();

                    String hourlyRateStr = data[18]
                            .replaceAll("\"", "")
                            .replaceAll(",", "")
                            .trim();

                    rate[i] = Double.parseDouble(hourlyRateStr);
                    i++;
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
    }

    // Finds employee and computes payroll including deductions
    public static void processPayroll(String id, String[] ids, String[] bdays, String[] names, double[] rates) {

        int index = -1;

        // Search for employee ID in the array
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != null && ids[i].equals(id)) {
                index = i;
                break;
            }
        }

        // If employee is not found
        if (index == -1) {
            System.out.println("Employee not found.");
            return;
        }

        // Compute total hours and gross wage
        double totalHours = calculateHrsFromCSV(id);
        double grossWage = totalHours * rates[index];

        // Estimate deductions (currently based on weekly-to-monthly conversion)
        double estimatedMonthly = grossWage * 4;

        double sss = computeSSS(estimatedMonthly) / 4;
        double ph = computePhilHealth(estimatedMonthly) / 4;
        double pi = computePagIbig(estimatedMonthly) / 4;

        double taxableWeekly = grossWage - (sss + ph + pi);
        double tax = computeTax(taxableWeekly * 4) / 4;

        double net = grossWage - (sss + ph + pi + tax);

        // Display payroll summary
        displaySummary(names[index], bdays[index], totalHours, grossWage, sss, ph, pi, tax, net);
    }

    // Displays formatted payroll summary
    public static void displaySummary(String name, String bday, double hrs, double gross,
                                      double sss, double ph, double pi, double tax, double net) {

        System.out.println("\n----- MotorPH Weekly Payroll Summary -----");
        System.out.println("Employee Name:  " + name);
        System.out.println("Birthday:       " + bday);
        System.out.printf("Total Hours:    %.2f\n", hrs);
        System.out.printf("Weekly Gross:   %.2f\n", gross);
        System.out.println("----------------------------------\n");

        System.out.printf("SSS:            %.2f\n", sss);
        System.out.printf("PhilHealth:     %.2f\n", ph);
        System.out.printf("Pag-IBIG:       %.2f\n", pi);
        System.out.printf("Tax:            %.2f\n", tax);
        System.out.printf("WEEKLY NET PAY: %.2f\n", net);
        System.out.println("----------------------------------\n");
    }
}