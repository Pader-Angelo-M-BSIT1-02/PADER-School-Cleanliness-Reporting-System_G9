import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class Report implements Comparable<Report> {
    int id;
    String building;
    String location;
    String status;

    public Report(int id, String building, String location, String status) {
        this.id = id;
        this.building = building;
        this.location = location;
        this.status = status != null ? status : "Pending";
    }

    @Override
    public String toString() {

        return String.format("%-4s | %-4s | %-20s | %-10s",
            "ID:" + id, building, location, status);
    }

    @Override
    public int compareTo(Report other) {
        return Integer.compare(this.id, other.id);
    }

    public boolean matchesBuilding(String bldg) {
        return this.building.equalsIgnoreCase(bldg.trim());
    }
}

public class ReportingSystem {
    private static ArrayList<Report> reports = new ArrayList<>();
    private static int reportCounter = 1;
    private static Scanner scanner = new Scanner(System.in);

    private static final String ADMIN_PASSWORD = System.getenv("ADMIN_PASS") != null
        ? System.getenv("ADMIN_PASS") : "clean";

    public static void main(String[] args) {
        System.out.println(" SCHOOL CLEANLINESS REPORTING SYSTEM ");

        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    studentMenu();
                    break;
                case "2":
                    adminMenu();
                    break;
                default:
                    System.out.println(" Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n");
        System.out.println("SELECT USER TYPE:");
        System.out.println("1.  Student Menu");
        System.out.println("2.  Admin Menu");
        System.out.print("Choice: ");
    }

    private static void studentMenu() {
        System.out.println("\n STUDENT MENU ");
        System.out.println("1. Report Dirty Area");
        System.out.println("2. View All Reports");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            reportDirtyArea();
        } else if ("2".equals(choice)) {
            studentViewReportsByBuilding();
        } else {
            System.out.println(" Invalid choice.");
        }
    }

    private static void reportDirtyArea() {
        String building = selectBuilding();
        if (building == null) {
            System.out.println(" Invalid building selection.");
            return;
        }
        
        System.out.print("Enter Location of Dirty Area: ");
        String location = scanner.nextLine().trim();


        boolean hasRecentReport = false;
        for (Report r : reports) {
            if (r.matchesBuilding(building) && r.location.equalsIgnoreCase(location) && "Pending".equalsIgnoreCase(r.status)) {
                System.out.println("  Recent pending report exists for '" + building + " " + location + "':");
                System.out.println("   " + r);
                System.out.print("Still want to submit new report? (y/n): ");
                if (!"y".equalsIgnoreCase(scanner.nextLine().trim())) {
                    return;
                }
                hasRecentReport = true;
                break;
            }
        }
        Report newReport = new Report(reportCounter++, building, location, "Pending");
        reports.add(newReport);
        System.out.println(" REPORT #" + newReport.id + " SUBMITTED SUCCESSFULLY");
        System.out.println(" " + newReport);
        if (hasRecentReport) {
            System.out.println(" Duplicate report submitted.");
        }
    }

    private static void studentViewReportsByBuilding() {
        String building = selectBuilding();
        if (building == null) {
            System.out.println(" Invalid building selection.");
            return;
        }
        System.out.println("\n REPORTS for Building: " + building + " (" + countReportsByBuilding(building) + ") ");
        boolean found = false;
        for (Report r : reports) {
            if (r.matchesBuilding(building)) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) {
            System.out.println(" No reports found for building " + building + ".");
        }
    }

    private static int countReportsByBuilding(String building) {
        int count = 0;
        for (Report r : reports) {
            if (r.matchesBuilding(building)) count++;
        }
        return count;
    }

    private static String selectBuilding() {
        System.out.println("SELECT BUILDING");
        System.out.println("1. MS");
        System.out.println("2. PH");
        System.out.println("3. MW");
        System.out.println("4. WN");
        System.out.print("Select building: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1": return "MS";
            case "2": return "PH";
            case "3": return "MW";
            case "4": return "WN";
            default: return null;
        }
    }

    private static void adminMenu() {
        System.out.println("\n ADMIN MENU ");
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine().trim();

        if (ADMIN_PASSWORD.equals(password)) {
            System.out.println("ACCESS GRANTED! Welcome Admin");
            adminActions();
        } else {
            System.out.println("ACCESS DENIED! Wrong password.");
        }
    }

    private static void adminActions() {
        System.out.println("\n ADMIN DASHBOARD (" + reports.size() + " reports) ");
        System.out.println("1. View All Reports");
        System.out.println("2. Mark as Cleaned");
        System.out.println("3. Delete Report");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                viewAllReports();
                break;
            case "2":
                markAsCleaned();
                break;
            case "3":
                deleteReport();
                break;
            default:
                System.out.println(" Invalid choice. Please try again.");
        }
    }

    private static void viewAllReports() {
        if (reports.isEmpty()) {
            System.out.println(" No reports found.");
            return;
        }
        Collections.sort(reports);
        System.out.println("\n ALL REPORTS ");
        System.out.printf("%-4s | %-4s | %-20s | %-10s%n", "ID", "BLD", "LOCATION", "STATUS");
        System.out.println("-".repeat(50));
        for (Report r : reports) {
            System.out.println(r);
        }
    }

    private static void markAsCleaned() {
        if (reports.isEmpty()) {
            System.out.println(" No reports available.");
            return;
        }
        viewAllReports();
        System.out.print("Enter Report ID to mark as cleaned: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean found = false;
            for (Report r : reports) {
                if (r.id == id) {
                    if ("Cleaned".equalsIgnoreCase(r.status)) {
                        System.out.println(" Report #" + id + " is already marked as cleaned.");
                    } else {
                        r.status = "Cleaned";
                        System.out.println(" Report #" + id + " marked as cleaned.");
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(" Report ID not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Invalid input! Please enter a valid number.");
        }
    }

    private static void deleteReport() {
        if (reports.isEmpty()) {
            System.out.println(" No reports available.");
            return;
        }
        viewAllReports();
        System.out.print("Enter Report ID to delete: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            boolean removed = reports.removeIf(r -> r.id == id);
            if (removed) {
                System.out.println(" Report #" + id + " deleted successfully!");
                renumberReports();
            } else {
                System.out.println(" Report ID not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println(" Invalid input! Please enter a valid number.");
        }
    }

    private static void renumberReports() {
        if (reports.isEmpty()) return;
        
        Collections.sort(reports);
        int newId = 1;
        
        for (Report r : reports) {
            if (r.id != newId) {
                r.id = newId;
            }
            newId++;
        }
        
        reportCounter = newId;
        System.out.println(" Reports renumbered successfully!");
    }
}