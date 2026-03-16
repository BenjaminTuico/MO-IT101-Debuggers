package src; 
import java.util.*; // Import para magamit ang Scanner at iba pang utility tools
import java.io.*; // Import para magamit ang BufferedReader at FileReader para sa CSV

public class Motorph_phase1 {
    
    public static void main(String[] args) {
        // Gagawa ng mga "lalagyan" (arrays) para sa 50 na empleyado
        String[] empId = new String[50]; // Lalagyan ng mga ID
        String[] empName = new String[50]; // Lalagyan ng mga pangalan
        double[] hourlyRate = new double [50]; // Lalagyan ng sahod kada oras
        String[] birthday = new String[50]; // Lalagyan ng kaarawan
        
        // Babasahin ang CSV file at ilalagay ang laman sa mga arrays na ginawa sa taas
        readEmployeeData(empId, birthday, empName, hourlyRate);
        
        Scanner scan = new Scanner(System.in); // Para sa scanner
        System.out.println("Enter Employee ID: ");
        String searchId = scan.nextLine(); // Kukunin ang tinype na ID ng user
        
        // Ipapadala ang ID at ang mga arrays sa processPayroll para hanapin at i-compute ang sahod
        processPayroll(searchId, empId, birthday, empName, hourlyRate);
        
        scan.close();
    }

    // Method para i-compute ang oras ng trabaho base sa Time In at Time Out
    public static double calculateHrs(String timeIn, String timeOut) {
        int inMin = timeToMinutes(timeIn); // Gagawing "minutes" ang Time In
        int outMin = timeToMinutes(timeOut); // Gagawing "minutes" ang Time Out
        
        int startShift = timeToMinutes("08:00"); // Standard na oras ng pasok
        int gracePeriod = timeToMinutes("08:10"); // Hanggang 8:10 ay walang late
        int endShift = timeToMinutes("17:00"); // Standard na oras ng uwi
        
        int actualIn;
        int actualOut;
        
        // Check kung pasok sa grace period; kung hindi late 
        if (inMin <= gracePeriod) {
            actualIn = startShift; // Ituturing na 8:00 ang pasok
        } else {
            actualIn = inMin; // Kung lampas 8:10, ang actual time ang susundin
        }
        
        // Check kung maagang umuwi o nag-overtime; 17:00 ang limit para sa regular hours
        if (outMin > endShift) {
            actualOut = endShift;
        } else {
            actualOut = outMin;
        }
        
        // Computation: (Minutes Out - Minutes In - 60 mins lunch break) / 60 para maging hours
        double totalHours = (actualOut - actualIn - 60) / 60.0;
        return (totalHours < 0) ? 0 : totalHours; // Siguraduhin na hindi negative ang sagot
    }
    
    // Method para sa SSS deduction base sa sahod
    public static double computeSSS(double gross) {
        if (gross <= 3250) return 135.00; // Minimum na contribution
        if (gross >= 24750) return 1125.00; // Maximum na contribution
        return gross * 0.045; // 4.5% employee share para sa mga nasa gitna
    }

    // Method para sa PhilHealth (Sahod * 3% tapos hati ang employer at employee)
    public static double computePhilHealth(double gross) {
        return(gross * 0.03) * 0.50; // 50% ang share ng employee
    }

    // Method para sa Pag-IBIG deduction
    public static double computePagIbig(double gross) {
        double rate = (gross <= 1500) ? 0.01 : 0.02; // 1% kung maliit ang sahod, 2% kung malaki
        double contribution = gross * rate;
        return (contribution > 100) ? 100 : contribution; // Max na contribution ay 100
    }

    // Method para sa Withholding Tax base sa taxable income
    public static double computeTax(double taxable) {
        if (taxable <= 20833) return 0; // Walang tax kung maliit ang taxable income
        if (taxable <= 33332) return (taxable - 20833) * 0.20; // 20% tax bracket
        if (taxable <= 66666) return 2500 + (taxable - 33333) * 0.25; // 25% tax bracket
        return (taxable - 66667) * 0.30 + 10833; // 30% tax bracket
    }
    
    // Ginagawang total minutes ang HH:mm format (Halimbawa 1:30 = 90 mins)
    public static int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // Method para basahin ang CSV file at ilagay ang data sa arrays
    public static void readEmployeeData(String[] id, String[] bday, String[] name, double[] rate) {
        try (BufferedReader br = new BufferedReader(new FileReader("EmployeeData.csv"))) {
            String line = br.readLine(); // Nilalagpasan ang unang linya (headers)
            int i = 0;
            while ((line = br.readLine()) != null && i < id.length) {
                // Hinihiwalay ang data gamit ang comma (regex para iwas error sa quotes)
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                id[i] = data[0].replaceAll("\"", "").trim(); // ID column
                name[i] = data[1].replaceAll("\"", "").trim(); // Name column
                bday[i] = data[3].replaceAll("\"", "").trim(); // Birthday column
                
                String rateRaw = data[18].replaceAll("\"", "").trim(); // Sahod column
                if (!rateRaw.isEmpty()) {
                    rate[i] = Double.parseDouble(rateRaw); // Ginagawang number ang sahod
                }
                i++;
            }
        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage()); // Error message kung may problema sa file
        }
    }

    // Method para hanapin ang empleyado at i-calculate lahat ng deductions
    public static void processPayroll(String id, String[] ids, String[] bdays, String[] names, double[] rates) {
        double hourlyRate = 0;
        String name = "";
        String bday = "";

        // Loop para hanapin kung saang index yung ID na tinype ng user
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != null && ids[i].equals(id)) {
                hourlyRate = rates[i];
                name = names[i];
                bday = bdays[i];
                break;
            }
        }
        
        // Kung walang nahanap na pangalan, ititigil ang program
        if (name.isEmpty()) {
            System.out.println("Employee not found.");
            return;
        }
        
        double weeklyHours = 40.0; // Assumption na 40hours ang trabaho
        double grossWeekly = weeklyHours * hourlyRate; // Total sahod bago ang bawas
        
     // Week 8: Net Wage Calculation (Applying deductions)
        double estimatedMonthly = grossWeekly * 4; 
        double sssWeekly = computeSSS(estimatedMonthly) / 4;
        double phWeekly = computePhilHealth(estimatedMonthly) / 4;
        double piWeekly = computePagIbig(estimatedMonthly) / 4;
        
        // Tax is based on taxable income
        double taxableWeekly = grossWeekly - (sssWeekly + phWeekly + piWeekly);
        double taxWeekly = computeTax(taxableWeekly * 4) / 4;

        double netWeekly = grossWeekly - (sssWeekly + phWeekly + piWeekly + taxWeekly);

        // Week 5 presentation of results
        displaySummary(name, bday, grossWeekly, sssWeekly, phWeekly, piWeekly, taxWeekly, netWeekly);
    }

    // Method para i-print ang payroll summary
    public static void displaySummary(String name, String bday, double gross, double sss, double ph, double pi, double tax, double net) {
        System.out.println("\n----- MotorPH Weekly Payroll Summary -----"); 
        System.out.println("Employee Name:  " + name);
        System.out.println("Birthday:       " + bday);
        System.out.printf("Weekly Gross:   %.2f\n", gross); 
        System.out.printf("SSS:            %.2f\n", sss);
        System.out.printf("PhilHealth:     %.2f\n", ph);
        System.out.printf("Pag-IBIG:       %.2f\n", pi);
        System.out.printf("Tax:            %.2f\n", tax);
        System.out.printf("WEEKLY NET PAY: %.2f\n", net); 
        System.out.println("----------------------------------\n");
    }
}
