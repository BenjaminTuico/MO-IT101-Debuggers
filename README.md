# MO-IT101-Debuggers

Members:
Kurt Christian Loristo
Joanne Mae Guno
Rossiel Allijah Toriano
APRILYN BERNABE
Benjamin Tuico

Project Plan Guide Link : https://docs.google.com/spreadsheets/d/1qg2unXqCtltvTnk62PPeBxhtOdik9WtUWdMdQPLZO_0/edit?usp=sharing


OVERVIEW
The MotorPH Payroll System is a Java-based console application that automates employee payroll processing. It reads employee and attendance data from CSV files, allows login as Admin or Employee, and computes weekly salary including government deductions.

FEATURES

Login System

Supports Admin and Employee roles
Maximum of 3 login attempts
Default credentials:
Admin : payroll_staff / 12345
Employee : employee / 12345

Admin Functions

Compute payroll for any employee using Employee ID
View employee list
Logout

Employee Functions

View own payroll
Logout

DATA SOURCES

EmployeeData.csv

Employee ID
Full Name
Birthday
Hourly Rate

Attendance.csv

Employee ID
Time In
Time Out

PAYROLL COMPUTATION

Salary Calculation

Total hours are computed from attendance records
Includes a 10-minute grace period (8:00–8:10 AM)
Deducts 1 hour for lunch

Government Deductions

SSS

Based on salary bracket
Minimum: 135.00
Maximum: 1125.00
Otherwise: 4.5% of salary

PhilHealth

3% of monthly salary
Employee pays 50% share

Pag-IBIG

2% of salary
Maximum contribution: 100

Tax

No tax if ≤ 20,833
20% of excess over 20,833

Net Pay Formula
Net Pay = Gross Pay - (SSS + PhilHealth + Pag-IBIG + Tax)

KEY FUNCTIONALITIES

Reads CSV files using BufferedReader
Stores employee data in arrays (max 50 employees)
Computes total working hours from attendance logs
Converts time (HH:mm) into minutes
Displays payroll summary with deductions and net pay
