**MO-IT101 – Debuggers**
**MotorPH Payroll System**

**Members**
Kurt Christian Loristo
Joanne Mae Guno
Rossiel Allijah Toriano
Aprilyn Bernabe
Benjamin Tuico

**Project Plan Guide:**
https://docs.google.com/spreadsheets/d/1qg2unXqCtltvTnk62PPeBxhtOdik9WtUWdMdQPLZO_0/edit?usp=sharing

**Test Case from Group 5**
https://docs.google.com/spreadsheets/d/1FP4Ax12468OmXph-5pAAa4RY_5EvqfSCi66pqYWQfGI/edit?usp=sharing

**Overview**

The MotorPH Payroll System is a Java-based console application designed to automate employee payroll processing. It reads employee and attendance data from CSV files, supports user login (Admin or Employee), and calculates weekly salary including government deductions.

  **Features**
Login System
Supports Admin and Employee roles
Maximum of 3 login attempts
Default credentials:
  Admin: payroll_staff / 12345
  Employee: employee / 12345

**Admin Functions**
Compute payroll for any employee using Employee ID
View employee list
Logout

  **Employee Functions**
View personal payroll
Logout

  **Data Sources**
  
**EmployeeData.csv**
Contains:
- Employee ID
- Full Name
- Birthday
- Hourly Rate

**Attendance.csv**
Contains:
- Employee ID
- Time In
- Time Out

  **Payroll Computation**
**Salary Calculation**
- Total working hours are computed from attendance records
- Includes 10-minute grace period (8:00–8:10 AM)
- Automatically deducts 1 hour for lunch

**Government Deductions**
**SSS**
- Minimum: ₱135.00
- Maximum: ₱1125.00
- Otherwise: 4.5% of salary
**PhilHealth**
-3% of monthly salary
-Employee pays 50% share
**Pag-IBIG**
- 2% of salary
- Maximum contribution: ₱100
**Tax**
- No tax if salary ≤ ₱20,833
- 20% of excess over ₱20,833
**Net Pay Formula**
- Net Pay = Gross Pay - (SSS + PhilHealth + Pag-IBIG + Tax)

**Key Functionalities**
- Reads CSV files using BufferedReader
- Stores employee data in arrays (max 50 employees)
- Computes total working hours from attendance logs
- Converts time (HH:mm) into minutes
- Displays payroll summary with deductions and net pay

**How to Run the Program**
1. Requirements
Java JDK
A terminal / command prompt
CSV files:
- EmployeeData.csv
- Attendance.csv

2. Project Setup

Make sure your folder structure looks like this:

ProjectFolder/
│
├── src/
│   └── Motorph_phase1.java
│
├── EmployeeData.csv
├── Attendance.csv

3. Compile the Program
Open your terminal and navigate to the project folder, then run:
- javac src/Motorph_phase1.java

4. Run the Program
- java src.Motorph_phase1

5. Login

Use the default credentials:

Admin
Username: payroll_staff
Password: 12345
Employee
Username: employee
Password: 12345

6. Notes
Make sure the CSV files are in the same directory where you run the program

If files are missing or incorrectly formatted, the system may show:

Error reading file.
