package src; 
import java.util.*; // Import para magamit ang Scanner at iba pang utility tools
import java.io.*; // Import para magamit ang BufferedReader at FileReader para sa CSV

public class Motorph_phase1 {
    
	public static boolean login() {
		Scanner scan = new Scanner(System.in);
		String userName = "employee";
		String pass = "1234";
		int attempts = 3;
		
		System.out.println("\"----- MotorPH Login System -----\"");
		
		while (attempts > 0) {
			System.out.println("Username: ");
			String inputUser = scan.next();
			System.out.println("Password: ");
			String inputPass = scan.next();
			
			if (inputUser.equals(userName) && inputPass.equals(pass)) {
				System.out.println("Login Successful! Welcome " + userName);
		        System.out.println("");
				return true;
			} else {
				attempts--;
				System.out.println("Invalid credentials. Attempts left: " + attempts + "\n");
				
			}
		}
		System.out.println("Too many failed attempts. Access Denied.");
	    return false;
	}
    public static void main(String[] args) {
//    	Code for Log in
    	if (!login()) {
    		System.exit(0);
    	}
        // Gagawa ng mga "lalagyan" (arrays) para sa 50 na empleyado
        String[] empId = new String[50]; // Lalagyan ng mga ID
        String[] empFullName = new String[50]; // Lalagyan ng mga pangalan
        double[] hourlyRate = new double [50]; // Lalagyan ng sahod kada oras
        String[] birthday = new String[50]; // Lalagyan ng kaarawan
        
        // Babasahin ang CSV file at ilalagay ang laman sa mga arrays na ginawa sa taas
        readEmployeeData(empId, birthday, empFullName, hourlyRate);
        
        Scanner scan = new Scanner(System.in); // Para sa scanner
        System.out.println("Enter Employee ID: ");
        String searchId = scan.nextLine(); // Kukunin ang tinype na ID ng user
        
        // Ipapadala ang ID at ang mga arrays sa processPayroll para hanapin at i-compute ang sahod
        processPayroll(searchId, empId, birthday, empFullName, hourlyRate);
        
        scan.close();
    }
    
    private static double calculateHrsFromCSV(String searchID) {
		double totalHours = 0;
		try (BufferedReader br = new BufferedReader(new FileReader("Attendance.csv"))){
			String line = br.readLine();
			while ((line = br.readLine()) != null ){
				String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                String id = data[0].replaceAll("\"", "").trim();
                
                if (id.equals(searchID)) {
                	String date = data[3].replaceAll("\"", "").trim();
                	if (date.contains("/06/2024")) {
                		String timeIn = data[4].replaceAll("\"", "").trim(); // Adjust column index if needed
                        String timeOut = data[5].replaceAll("\"", "").trim();
                        if(!timeIn.equals("0:00") && !timeOut.equals("0:00")) {
                        	totalHours += calculateHrs(timeIn, timeOut);
                        }
                	}
                	

                	}
				}
			} catch (Exception e) {
				System.out.println("Error reading Attendance: " + e.getMessage());
			}
//		return totalHours / 4.0;
		return totalHours;
	}

    // Method para i-compute ang oras ng trabaho base sa Time In at Time Out
    public static double calculateHrs(String timeIn, String timeOut) {
    	if (timeIn.equals("0:00") || timeOut.equals("0:00")) return 0;
       
    	int inMin = timeToMinutes(timeIn); // Gagawing "minutes" ang Time In
        int outMin = timeToMinutes(timeOut); // Gagawing "minutes" ang Time Out
        
        int startShift = timeToMinutes("08:00"); // Standard na oras ng pasok
        int gracePeriod = timeToMinutes("08:05"); // Hanggang 8:10 ay walang late
        int endShift = timeToMinutes("17:00");
        
        int actualIn = (inMin <= gracePeriod) ? startShift : inMin;
        
        if (actualIn < startShift) actualIn = startShift;
        if (outMin > endShift) outMin = endShift;
        if (outMin < actualIn) return 0;
        
//        int actualOut = (outMin > endShift) ? endShift : outMin;
        
     // Computation (Out - In)
        double totalMinutes = outMin - actualIn - 60;
        double hours = totalMinutes / 60.0;
        
//        int totalMinutesWorked = outMin - actualIn;
        
//        double hours = (actualOut - actualIn - 60) / 60.0;
        return (hours < 0) ? 0 : hours;


    }
    
    // Method para sa SSS deduction base sa sahod
    public static double computeSSS(double gross) {
        if (gross <= 3250) return 135.00; // Minimum na contribution
        if (gross >= 24750) return 1125.00; // Maximum na contribution
        return gross * 0.045; // 4.5% employee share para sa mga nasa gitna
    }

    // Method para sa PhilHealth (Sahod * 3% tapos hati ang employer at employee)
    public static double computePhilHealth(double gross) {
//        return(gross * 0.03) * 0.50; // 50% ang share ng employee
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

    // Method para sa Pag-IBIG deduction
    public static double computePagIbig(double gross) {
    	double rate;
    	
    	if(gross <= 1500) {
    		rate = 0.01;
    	} else {
    		rate = 0.02;
    	}
    	
    	double contribution = gross * rate;
    	
    	if (contribution > 100) {
    		contribution = 100;
    	}
        return contribution;
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
    public static void readEmployeeData(String[] id, String[] bday, String[] fullName, double[] rate) {
        try (BufferedReader br = new BufferedReader(new FileReader("EmployeeData.csv"))) {
            String line = br.readLine(); // Nilalagpasan ang unang linya (headers)
            int i = 0;
            while ((line = br.readLine()) != null && i < id.length) {
                // Hinihiwalay ang data gamit ang comma (regex para iwas error sa quotes)
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (data.length > 18) {
                    id[i] = data[0].replaceAll("\"", "").trim();
                    fullName[i] = data[2].replace("\"", "").trim() + " " + data[1].replace("\"", "").trim();
                    bday[i] = data[3].replaceAll("\"", "").trim();

                    // Column S = Index 18
                    String hourlyRateStr = data[18].replaceAll("\"", "").replaceAll(",", "").trim();
                    rate[i] = Double.parseDouble(hourlyRateStr);
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading CSV: " + e.getMessage()); // Error message kung may problema sa file
        }
    }

    // Method para hanapin ang empleyado at i-calculate lahat ng deductions
    public static void processPayroll(String id, String[] ids, String[] bdays, String[] names, double[] rates) {
    	int index = -1;

        // Loop para hanapin kung saang index yung ID na tinype ng user
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != null && ids[i].equals(id)) {
            	index = i;
                break;
            }
        }
        
        // Kung walang nahanap na pangalan, ititigil ang program
        if (index == -1) {
            System.out.println("Employee not found.");
            return;
        }
        
        double totalHours = calculateHrsFromCSV(id);
        double grossWage = totalHours * rates[index]; // Total sahod bago ang bawas
        
     // Week 8: Net Wage Calculation (Applying deductions)
        double estimatedMonthly = grossWage * 4; 
        double sss = computeSSS(estimatedMonthly) / 4;
        double ph = computePhilHealth(estimatedMonthly) / 4;
        double pi = computePagIbig(estimatedMonthly) / 4;
        
        // Tax is based on taxable income
        double taxableWeekly = grossWage - (sss+ ph + pi);
        double tax = computeTax(taxableWeekly * 4) / 4;

        double net = grossWage - (sss + ph+ pi + tax);

        // Week 5 presentation of results
        displaySummary(names[index], bdays[index],totalHours, grossWage, sss, ph, pi, tax, net);
    }

    // Method para i-print ang payroll summary
    public static void displaySummary(String name, String bday, double hrs ,double gross, double sss, double ph, double pi, double tax, double net) {
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
