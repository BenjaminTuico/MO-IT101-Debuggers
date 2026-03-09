# MO-IT101-Debuggers

Members:
Kurt Christian Loristo
Joanne Mae Guno
Rossiel Allijah Toriano
APRILYN BERNABE
Benjamin Tuico

Project Plan Guide Link : https://docs.google.com/spreadsheets/d/1qg2unXqCtltvTnk62PPeBxhtOdik9WtUWdMdQPLZO_0/edit?usp=sharing


Program Description: MotorPH Payroll System (Phase 1)
The MotorPH Phase 1 system is a Java-based console application designed to automate the calculation of weekly employee wages and government-mandated deductions. It transitions from manual record-keeping to a digital process by following these core steps:

1. Data Initialization & File Reading

Upon execution, the system initializes arrays to store information for up to 50 employees. It uses the readEmployeeData method to parse an external CSV file (EmployeeData.csv), extracting critical fields such as Employee ID, Name, Birthday, and Hourly Rate.

2. Employee Search & Identification

The system prompts the user to input a specific Employee ID. It then performs a linear search through the stored arrays to locate the corresponding employee's profile. If the ID is not found, the system provides an error message and terminates the process.

3. Wage & Hours Logic

While the current version uses a standard 40-hour workweek assumption for payroll processing, the system includes logic (calculateHrs) to determine actual work duration. It accounts for:

Grace Periods: Deducting lates if the "Time In" is past 08:10 AM.

Lunch Breaks: Automatically subtracting 60 minutes from the total duration.

Shift Limits: Capping regular hours at 05:00 PM.

4. Automated Deduction & Tax Calculation

The system calculates the Net Pay by applying Philippine-standard contribution formulas:

SSS: Based on a tiered gross income matrix.

PhilHealth: Calculated at 3% of gross, split 50/50 between employer and employee.

Pag-IBIG: Tiered percentage (1% or 2%) capped at a maximum of 100 pesos.

Withholding Tax: Applied using a progressive tax bracket after deducting mandatory contributions.

5. Summary Presentation

Finally, the displaySummary method generates a formatted Weekly Payroll Summary. This provides a clear breakdown of the Gross Pay, individual deductions (SSS, PhilHealth, Pag-IBIG, Tax), and the final Weekly Net Pay for the user.
