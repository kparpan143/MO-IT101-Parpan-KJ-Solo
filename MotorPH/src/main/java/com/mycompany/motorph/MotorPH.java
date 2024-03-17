/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristine Parpan
 */
public class MotorPH {
    
    // Constant Variables
    public static final String EMPLOYEE_DETAILS_CSV = System.getProperty("user.dir") + "/src/main/resources/employee_details.csv";
    public static final String ATTENDANCE_CSV = System.getProperty("user.dir") + "/src/main/resources/attendance.csv";

    // Global Variables
    public static BufferedReader employeeDetailsReader = null;
    public static BufferedReader attendanceReader = null;
    public static String employee = "";
    public static int employeeNumber = 0;
    public static double hourlyRate = 0;
    public static int year = 0;
    public static boolean isYearInRecord = false;
    public static int monthNumber = 0;
    public static int monthTotalWeeks = 0;
    public static String month = "";
    public static double totalHoursWorked = 0;
    public static double basicSalary = 0;
    public static List<AbstractMap.SimpleEntry<Integer, List<String>>> weeklyAttendances;
    
    public static void main(String[] args) {
        while (true) {
            resetData();
            System.out.println("**************************************************");
            System.out.println("** Welcome to MotorPH's Employee Salary System **");
            
            readCSVFiles();
            getEmployeeNumber();
            
            System.out.println("");
            System.out.println("**************** Employee Details ****************");
            System.out.println("");
            
            if (!employee.isEmpty()) {
                viewEmployeeProfile();
                
                System.out.println("");
                System.out.println("*************** Salary Computation ***************");
                System.out.println("");

                getYear();
                if (year > (Year.now().getValue() - 5) && year <= Year.now().getValue()) {
                    getMonthNumber();
                    if (monthNumber >= 1 && monthNumber <= 12) {

                        month = new DateFormatSymbols().getMonths()[monthNumber - 1];
                        System.out.println("Entered Month: " + month);
                        System.out.println("Total Number of Weeks: " + monthTotalWeeks);
                        
                        getWeeklyAttendance();
                        if (!weeklyAttendances.isEmpty()) {
                            calculateSalary();
                        }
                        else {
                            System.out.println("No attendance record for " + month + " " + year);
                        }
                    }
                    else {
                        System.out.println("Invalid Month Number. Please Try Again.");
                    }
                }
                else {
                    System.out.println("Invalid Year. Please Try Again.");
                }
            }
            else {
                System.out.println("Employee Not Found. Please Try Again.");
            }
            
            System.out.println("");
            System.out.println("***************** End of Session *****************");
            System.out.println("**************************************************");
            System.out.println("");
        }
    }
           
    // Method used for reseting data used in the system
    public static void resetData () {
        employeeDetailsReader = null;
        attendanceReader = null;
        employee = "";
        employeeNumber = 0;
        hourlyRate = 0;
        year = 0;
        isYearInRecord = false;
        monthNumber = 0;
        monthTotalWeeks = 0;
        month = "";
        totalHoursWorked = 0;
        basicSalary = 0;
        weeklyAttendances = null;
    }
    
    // Method used for reading CSV files
    public static void readCSVFiles() {
        try {
            employeeDetailsReader = new BufferedReader(new FileReader(EMPLOYEE_DETAILS_CSV));
            attendanceReader = new BufferedReader(new FileReader(ATTENDANCE_CSV));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MotorPH.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Method used for getting Employee Number
    public static void getEmployeeNumber () {
        System.out.print("Please Enter Employee Number: ");
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            employeeNumber = Integer.parseInt(inputReader.readLine());
            String employeeDetailsRow = "";
            
            while ((employeeDetailsRow = employeeDetailsReader.readLine()) != null) {
                String employeeDetails = employeeDetailsRow.replaceAll(",(?!(([^\"]*\"){2})*[^\"]*$)", ";x;");
                String[] splitEmployeeDetails = employeeDetails.split(",");

                if (Integer.parseInt(splitEmployeeDetails[0]) == employeeNumber) {
                    employee = employeeDetails;
                }
            }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    // Method used for View Employee Profile Process
    public static void viewEmployeeProfile () {
        String[] splitEmployeeDetails = employee.split(",");

        System.out.println("Employee Number: " + splitEmployeeDetails[0]);
        System.out.println("Employee Name: " + splitEmployeeDetails[2] + " " + splitEmployeeDetails[1]);
        System.out.println("Employee Birthday: " + splitEmployeeDetails[3]);

        // Save hourly rate and basic salary for Salary Calculations
        basicSalary = Double.parseDouble(cleanString(splitEmployeeDetails[splitEmployeeDetails.length - 6]));
        hourlyRate = Double.parseDouble(splitEmployeeDetails[splitEmployeeDetails.length - 1]);
        System.out.println("Basic Salary: " + basicSalary);
        System.out.println("Hourly Rate: " + hourlyRate);
    }
    
    // Method used for getting Year
    public static void getYear () {
        System.out.print("Enter Attendance Year: ");
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        try {
           year = Integer.parseInt(inputReader.readLine());
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    // Method used for getting Month Number
    public static void getMonthNumber () {
        System.out.print("Enter Month Number For Salary Computation (1-12): ");
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            
           monthNumber = Integer.parseInt(inputReader.readLine());
           Calendar calendar = Calendar.getInstance();
           calendar.set(Calendar.YEAR, year);
           calendar.set(Calendar.MONTH, monthNumber - 1);
           calendar.set(Calendar.DAY_OF_MONTH, 1);
           monthTotalWeeks = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
           
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    // Method used for calculating employee salary
    public static void calculateSalary () {
        System.out.println("------------------------------");
        System.out.println("Record: " + weeklyAttendances);
        
        for (int a = 1; a <= monthTotalWeeks; a++) {
            double weeklyHoursWorked = 0;
            boolean isRecordAdded = false;
            
            for (int i = 0; i < weeklyAttendances.size(); i++) {
                // check if record already exists in the list
                if (weeklyAttendances.get(i).getKey().equals(a)) {
                    isRecordAdded = true;
                    weeklyHoursWorked = calculateHoursWorked(weeklyAttendances.get(i), weeklyHoursWorked);
                    System.out.println("Week # " + weeklyAttendances.get(i).getKey() + " Total Hours: " + String.format("%.2f", weeklyHoursWorked));

                    calculateGrossWage (weeklyHoursWorked, weeklyAttendances.get(i).getKey());
                    calculateNetWage (weeklyHoursWorked, weeklyAttendances.get(i).getKey());

                    System.out.println("------------------------------");
                }
            }
            
            if (isRecordAdded == false) {
                System.out.println("Week # " + a + " Total Hours: 0.0");
                calculateGrossWage (0.0, a);
                calculateNetWage (0.0, a);
                System.out.println("------------------------------");
            }
        }
    }
    
    // Method used for calculating hours per week depending on the month number provided
    public static double calculateHoursWorked (AbstractMap.SimpleEntry<Integer, List<String>> weeklyAttendance, double weeklyHoursWorked) {
        for (int a = 0; a < weeklyAttendance.getValue().size(); a++) {
            String date = weeklyAttendance.getValue().get(a).split(",")[1];
            String timeIn = weeklyAttendance.getValue().get(a).split(",")[2];
            String timeOut = weeklyAttendance.getValue().get(a).split(",")[3];
            double hoursWorkedPerDay = getHoursWorkedPerDay(date, timeIn, timeOut);
            weeklyHoursWorked += hoursWorkedPerDay;
        }
        return weeklyHoursWorked;
    }
    
    // Method used for calculating the Weekly Gross Wage
    public static void calculateGrossWage (double weeklyHoursWorked, int week) {
        System.out.println("Week # " + week + " Gross Wage: " + String.format("%.2f", (weeklyHoursWorked * hourlyRate)));
    }
    
    // Method used for calculating the Weekly Net Wage
    public static void calculateNetWage (double weeklyHoursWorked, int week) {
        
        double grossWage = Double.parseDouble(String.format("%.2f", (weeklyHoursWorked * hourlyRate)));
        double sssContribution = Double.parseDouble(String.format("%.2f", (getSSSContribution() / monthTotalWeeks)));
        double philHealthContribution = Double.parseDouble(String.format("%.2f", (getPhilHealthContribution() / monthTotalWeeks)));
        double pagibigContribution = Double.parseDouble(String.format("%.2f", (getPagibigContribution() / monthTotalWeeks)));
        double witholdingTax = Double.parseDouble(String.format("%.2f", (getWitholdingTax() / monthTotalWeeks)));
        
        double netWage = grossWage - sssContribution - philHealthContribution - pagibigContribution - witholdingTax;
                
        System.out.println("Weekly SSS Contrib: " + sssContribution);
        System.out.println("Weekly PhilHealth Contrib: " + philHealthContribution);
        System.out.println("Weekly pagibig Contrib: " + pagibigContribution);
        System.out.println("Weekly Witholding Tax: " + witholdingTax);
        System.out.println("Week # " + week + " Net Wage: " + String.format("%.2f", (netWage)));
    }
    
    
    // Method used for getting weekly attendance depending on the selected month
    public static void getWeeklyAttendance () {  
        weeklyAttendances = new ArrayList<AbstractMap.SimpleEntry<Integer, List<String>>>();
        String attendanceRow = "";
        List<String> attendances = new ArrayList<String>();
        List<String> monthlyAttendances = new ArrayList<String>();
        
        try {
            while ((attendanceRow = attendanceReader.readLine()) != null) {
                String[] splitAttendance = attendanceRow.split(",");
                
                if (Integer.parseInt(splitAttendance[0]) == employeeNumber) {
                    attendances.add(attendanceRow);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MotorPH.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Get all attendances with same month and year
        for (int i = 0; i < attendances.size(); i++) {
            String date = attendances.get(i).split(",")[1];
            int dateMonth = Integer.parseInt(date.split("/")[0]);
            int dateYear = Integer.parseInt(date.split("/")[2]);
                    
            if(dateMonth == monthNumber && dateYear == year) {
                monthlyAttendances.add(attendances.get(i));
            }
        }
        
        // Get all attendances per week
        for (int i = 0; i < monthlyAttendances.size(); i++) {
            String date = monthlyAttendances.get(i).split(",")[1];
            
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(date.split("/")[2]));
            calendar.set(Calendar.MONTH,Integer.parseInt(date.split("/")[0]) - 1);
            calendar.set(Calendar.DATE, Integer.parseInt(date.split("/")[1]));
            int week = calendar.get(calendar.WEEK_OF_MONTH);
            // System.out.println("Date: " + date);
            // System.out.println("Week: " + week);
            
            if (weeklyAttendances.isEmpty()) {
                List<String> weeklyAttendance = new ArrayList<String>();
                weeklyAttendance.add(monthlyAttendances.get(i));
                
                weeklyAttendances.add(new AbstractMap.SimpleEntry<Integer, List<String>>(week, weeklyAttendance));
            }
            else {
                // check if record already exists in the list
                boolean isRecordAdded = false;
                
                for (int a = 0; a < weeklyAttendances.size(); a++) {
                    if (weeklyAttendances.get(a).getKey() == week) {
                        for (int b = 0; b < weeklyAttendances.get(a).getValue().size(); b++) {
                            String attendanceDate = weeklyAttendances.get(a).getValue().get(b).split(",")[1];
                            if (attendanceDate.equals(date)) {
                                isRecordAdded = true;
                            }
                        }
                        
                        if (isRecordAdded == false) {
                            weeklyAttendances.get(a).getValue().add(monthlyAttendances.get(i));
                            isRecordAdded = true;
                        }
                    }
                }
                
                if (isRecordAdded == false) {
                    List<String> weeklyAttendance = new ArrayList<String>();
                    weeklyAttendance.add(monthlyAttendances.get(i));

                    weeklyAttendances.add(new AbstractMap.SimpleEntry<Integer, List<String>>(week, weeklyAttendance));
                }
            }
        }
        // System.out.println("Record: " + weeklyAttendances);
    }
    
    // Method used for getting hours worked per day
    public static double getHoursWorkedPerDay (String date, String timeIn, String timeOut) {
        
        // Convert String date and time to LocalDateTime
        LocalDateTime localDateTimeIn = getDateTime(date, timeIn);
        LocalDateTime localDateTimeOut = getDateTime(date, timeOut);
        
        // Set Grace Period as indicated in the requirements
        LocalTime gracePeriod = LocalTime.of(8, 11);
        // Check if time in is beyond grace period
        if (localDateTimeIn.toLocalTime().isBefore(gracePeriod)){
            // reset time in to 8AM to remove deduction
            localDateTimeIn = getDateTime(date, "08:00");
        }
        
        // Calculate Time Difference between timein and timeout
        long timeDiff = localDateTimeIn.toLocalTime().until(localDateTimeOut.toLocalTime(), ChronoUnit.MINUTES);
        double timeDiffDouble = Double.parseDouble(Long.toString(timeDiff)) / 60;
        
        if (timeDiffDouble <= 0) {
            // return zero for invalid data
            return 0.00;
        }
        else {
            // return total hours worked - 1 hour of breaktime
            timeDiffDouble = Double.parseDouble(String.format("%.2f", timeDiffDouble -1));
            
            return timeDiffDouble;
        }
    }
    
    // Method used for converting String date and String time
    public static LocalDateTime getDateTime (String date, String time) {
        String[] splitDate = date.split("/");
        String[] splitTime = time.split(":");
        
        // Convert String date to LocalDate
        LocalDate localDate = LocalDate.of(Integer.parseInt(splitDate[2]), Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]));
        // Convert String time to LocalTime
        LocalTime localTime = LocalTime.of(Integer.parseInt(splitTime[0]), Integer.parseInt(splitTime[1]));
        // Combine date and time
        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
        
        return dateTime;
    }
    
    
    // Method used for removing unnecessary characters in a string
    public static String cleanString (String input) {
    
        input = input.replaceAll(";x;", "");
        input = input.replaceAll("\"", "");
        
        return input;
    }

    // Method used for getting SSS Contribution
    public static double getSSSContribution() {
        if (basicSalary < 3250) {
            return 135.00;
        }
        else if (basicSalary >= 3250 && basicSalary <= 3750) {
            return 157.50;
        }
        else if (basicSalary >= 3750 && basicSalary <= 4250) {
            return 180.00;
        }
        else if (basicSalary >= 4250 && basicSalary <= 4750) {
            return 202.50;
        }
        else if (basicSalary >= 4750 && basicSalary <= 5250) {
            return 225.00;
        }
        else if (basicSalary >= 5250 && basicSalary <= 5750) {
            return 247.50;
        }
        else if (basicSalary >= 5750 && basicSalary <= 6250) {
            return 270.00;
        }
        else if (basicSalary >= 6250 && basicSalary <= 6750) {
            return 292.50;
        }
        else if (basicSalary >= 6750 && basicSalary <= 7250) {
            return 315.00;
        }
        else if (basicSalary >= 7250 && basicSalary <= 7750) {
            return 337.50;
        }
        else if (basicSalary >= 7750 && basicSalary <= 8250) {
            return 360.00;
        }
        else if (basicSalary >= 8250 && basicSalary <= 8750) {
            return 382.50;
        }
        else if (basicSalary >= 8750 && basicSalary <= 9250) {
            return 405.00;
        }
        else if (basicSalary >= 9250 && basicSalary <= 9750) {
            return 427.50;
        }
        else if (basicSalary >= 9750 && basicSalary <= 10250) {
            return 450.00;
        }
        else if (basicSalary >= 10250 && basicSalary <= 10750) {
            return 472.50;
        }
        else if (basicSalary >= 10750 && basicSalary <= 11250) {
            return 495.00;
        }
        else if (basicSalary >= 11250 && basicSalary <= 11750) {
            return 517.50;
        }
        else if (basicSalary >= 11750 && basicSalary <= 12250) {
            return 540.00;
        }
        else if (basicSalary >= 12250 && basicSalary <= 12750) {
            return 562.50;
        }
        else if (basicSalary >= 12750 && basicSalary <= 13250) {
            return 585.00;
        }
        else if (basicSalary >= 13250 && basicSalary <= 13750) {
            return 607.50;
        }
        else if (basicSalary >= 13750 && basicSalary <= 14250) {
            return 630.00;
        }
        else if (basicSalary >= 14250 && basicSalary <= 14750) {
            return 652.50;
        }
        else if (basicSalary >= 14750 && basicSalary <= 15250) {
            return 675.00;
        }
        else if (basicSalary >= 15250 && basicSalary <= 15750) {
            return 697.50;
        }
        else if (basicSalary >= 15750 && basicSalary <= 16250) {
            return 720.00;
        }
        else if (basicSalary >= 16250 && basicSalary <= 16750) {
            return 742.50;
        }
        else if (basicSalary >= 16750 && basicSalary <= 17250) {
            return 765.00;
        }
        else if (basicSalary >= 17250 && basicSalary <= 17750) {
            return 787.50;
        }
        else if (basicSalary >= 17750 && basicSalary <= 18250) {
            return 810.00;
        }
        else if (basicSalary >= 18250 && basicSalary <= 18750) {
            return 832.50;
        }
        else if (basicSalary >= 18750 && basicSalary <= 19250) {
            return 855.00;
        }
        else if (basicSalary >= 19250 && basicSalary <= 19750) {
            return 877.50;
        }
        else if (basicSalary >= 19750 && basicSalary <= 20250) {
            return 900.00;
        }
        else if (basicSalary >= 20250 && basicSalary <= 20750) {
            return 922.50;
        }
        else if (basicSalary >= 20750 && basicSalary <= 21250) {
            return 945.00;
        }
        else if (basicSalary >= 21250 && basicSalary <= 21750) {
            return 967.50;
        }
        else if (basicSalary >= 21750 && basicSalary <= 22250) {
            return 990.00;
        }
        else if (basicSalary >= 22250 && basicSalary <= 22750) {
            return 1012.50;
        }
        else if (basicSalary >= 22750 && basicSalary <= 23250) {
            return 1035.00;
        }
        else if (basicSalary >= 23250 && basicSalary <= 23750) {
            return 1057.50;
        }
        else if (basicSalary >= 23750 && basicSalary <= 24250) {
            return 1080.00;
        }
        else if (basicSalary >= 24250 && basicSalary <= 24750) {
            return 1102.50;
        }
        else {
            return 1125.00;
        }
    }

    // Method used for getting PhilHealth Contribution
    public static double getPhilHealthContribution() {
        if (basicSalary <= 10000.00) {
            return 150.00;
        }
        else if (basicSalary > 10000 && basicSalary < 60000) {
            return (basicSalary * 0.03) / 2;
        }
        else {
            return 900;
        }
    }

    // Method used for getting Pagibig Contribution
    public static double getPagibigContribution() {
        if (basicSalary >= 1000 && basicSalary <= 1500) {
            return basicSalary * 0.01;
        }
        else if (basicSalary > 1500){
            if ((basicSalary * 0.02) <= 100) {
                return basicSalary * 0.02;
            }
            else {
                return 100;
            }
        }
        else {
            return 0.0;
        }
    }

    // Method used for getting Witholding Tax
    public static double getWitholdingTax() {
        if (basicSalary <= 20832){
            return 0.0;
        }
        else if (basicSalary >= 20833 && basicSalary < 33333) {
            return (basicSalary - 20833) * 0.2;
        }
        else if (basicSalary >= 33333 && basicSalary < 66667) {
            return ((basicSalary - 33333) * 0.25) + 2500;
        }
        else if (basicSalary >= 66667 && basicSalary < 166667) {
            return ((basicSalary - 66667) * 0.3) + 10833;
        }
        else if (basicSalary >= 166667 && basicSalary < 666667) {
            return ((basicSalary - 166667) * 0.32) + 40833.33;
        }
        else {
            return ((basicSalary - 666667) * 0.35) + 200833.33;
        }
    }
}
