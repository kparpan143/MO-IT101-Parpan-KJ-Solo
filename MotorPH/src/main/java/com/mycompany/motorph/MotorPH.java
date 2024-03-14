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
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public static int monthNumber = 1;
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

                getMonthNumber();

                if (monthNumber >= 1 && monthNumber <= 12) {

                    month = new DateFormatSymbols().getMonths()[monthNumber - 1];
                    System.out.println("Entered Month: " + month);
                    getWeeklyAttendance();
                    calculateHoursWorked();
                }
                else {
                    System.out.println("Invalid Month Number. Please Try Again.");
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
        monthNumber = 1;
        month = "";
        totalHoursWorked = 0;
        basicSalary = 0;
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
    
    // Method used for getting Month Number
    public static void getMonthNumber () {
        System.out.print("Enter Month Number For Salary Computation (1-12): ");
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        try {
           monthNumber = Integer.parseInt(inputReader.readLine());
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    // Method used for calculating hours per week depending on the month number provided
    private static void calculateHoursWorked () {
        
        for (int i = 0; i < weeklyAttendances.size(); i++) {
            // System.out.println("Week: " + weeklyAttendances.get(i).getKey());
            // System.out.println("Record: " + weeklyAttendances.get(i).getValue());
            double weeklyHoursWorked = 0;
            
            for (int a = 0; a < weeklyAttendances.get(i).getValue().size(); a++) {
                String date = weeklyAttendances.get(i).getValue().get(a).split(",")[1];
                String timeIn = weeklyAttendances.get(i).getValue().get(a).split(",")[2];
                String timeOut = weeklyAttendances.get(i).getValue().get(a).split(",")[3];
                double hoursWorkedPerDay = getHoursWorkedPerDay(date, timeIn, timeOut);
                
               //  System.out.println("Hours Worked For Date: " + date + " - " + hoursWorkedPerDay);
                weeklyHoursWorked += hoursWorkedPerDay;
            }
            System.out.println("Total Hours Worked For Week: " + weeklyAttendances.get(i).getKey() + " - " + weeklyHoursWorked);
            calculateGrossWage (weeklyHoursWorked, weeklyAttendances.get(i).getKey());
            calculateNetWage (weeklyHoursWorked, weeklyAttendances.get(i).getKey());
        }
    }
    
    // Method used for calculating the Weekly Gross Wage
    public static void calculateGrossWage (double weeklyHoursWorked, int week) {
        System.out.println("Week # " + week + " Gross Wage: " + String.format("%.2f", (weeklyHoursWorked * hourlyRate)));
    }
    
    // Method used for calculating the Weekly Net Wage
    public static void calculateNetWage (double weeklyHoursWorked, int week) {
        System.out.println("Week # " + week + " Net Wage: " + String.format("%.2f", (weeklyHoursWorked * hourlyRate)));
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
        
        // Get all attendances with same month
        for (int i = 0; i < attendances.size(); i++) {
            String date = attendances.get(i).split(",")[1];
            int dateMonth = Integer.parseInt(date.split("/")[0]);
            
            if(dateMonth == monthNumber) {
                monthlyAttendances.add(attendances.get(i));
            }
        }
        
        // Get all attendances per week
        for (int i = 0; i < monthlyAttendances.size(); i++) {
            String date = monthlyAttendances.get(i).split(",")[1];
            String timeIn = monthlyAttendances.get(i).split(",")[2];
            
            LocalDateTime ldt = getDateTime(date, timeIn);

            Calendar cal = Calendar.getInstance();
            cal.setTime(Date.from(ldt.toInstant(ZoneOffset.UTC)));
            int week = cal.get(cal.WEEK_OF_MONTH);
            
            if (weeklyAttendances.isEmpty()) {
                List<String> weeklyAttendance = new ArrayList<String>();
                weeklyAttendance.add(monthlyAttendances.get(i));
                
                weeklyAttendances.add(new AbstractMap.SimpleEntry<Integer, List<String>>(week, weeklyAttendance));
            }
            else {
                for (int a = 0; a < weeklyAttendances.size(); a++) {
                    if (weeklyAttendances.get(a).getKey() == week) {
                        // check if record already exists in the list
                        boolean isRecordAdded = false;
                        for (int b = 0; b < weeklyAttendances.get(a).getValue().size(); b++) {
                            String attendanceDate = weeklyAttendances.get(a).getValue().get(b).split(",")[1];
                            if (attendanceDate.equals(date)) {
                                isRecordAdded = true;
                            }
                        }
                        
                        if (isRecordAdded == false) {
                            weeklyAttendances.get(a).getValue().add(monthlyAttendances.get(i));
                        }
                    }
                    else {
                        if (a == (weeklyAttendances.size() - 1)) {
                            List<String> weeklyAttendance = new ArrayList<String>();
                            weeklyAttendance.add(monthlyAttendances.get(i));

                            weeklyAttendances.add(new AbstractMap.SimpleEntry<Integer, List<String>>(week, weeklyAttendance));
                        }
                    }
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
}
