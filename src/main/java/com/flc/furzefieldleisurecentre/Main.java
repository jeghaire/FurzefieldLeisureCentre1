/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flc.furzefieldleisurecentre;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Mavi
 */
public class Main {

    private static final FLCSystem system = new FLCSystem();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            printMenu();

//            int choice = getIntInput("Select an option: ");
            Integer choice = getIntInputAllowCancel();
            if (choice == null) return;

            switch (choice) {
                case 1 -> bookLesson();
                case 2 -> changeOrCancelBooking();
                case 3 -> attendLesson();
                case 4 -> monthlyLessonReport();
                case 5 -> monthlyChampionReport();
                case 6 -> {
                    running = false;
                    System.out.println("Exiting system...");
                }
                case 99 -> showAllMembers();  // Secret
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n---- FurzeField Leisure Centre ----");
        System.out.println("1. Book a group exercise lesson");
        System.out.println("2. Change/Cancel a booking");
        System.out.println("3. Attend a lesson");
        System.out.println("4. Monthly lesson report");
        System.out.println("5. Monthly champion exercise report");
        System.out.println("6. Exit");
    }

    private static void printLine(int length) {
        System.out.println("-".repeat(length));
    }

    private static String getStringInput(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Operation cancelled. Returning to main menu...");
            return null;
        }

        return input;
    }

    private static Integer getIntInputAllowCancel() {
        return getIntInputAllowCancel("Select option (0 to cancel): ");
    }

    private static Integer getIntInputAllowCancel(String message) {

        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Operation cancelled. Returning to main menu...");
                return null;
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void showAllMembers() {

        System.out.println("\n🔐 ADMIN MODE: Member List");

        var members = system.getMembers();  // Make sure FLCSystem has this getter

        if (members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }

        printLine(60);
        System.out.printf("%-10s | %-20s | %-10s%n", "Member ID", "Name", "Bookings");
        printLine(60);

        for (Member member : members) {
            System.out.printf("%-10s | %-20s | %-10d%n",
                    member.getMemberId(),
                    member.getName(),
                    member.getBookings().size());
        }

        printLine(60);
    }

    // ================= BOOK LESSON =================
    private static void bookLesson() {

        String memberId = getStringInput("Enter Member ID (0 to cancel): ");
        if (memberId == null) return;

        System.out.println("View timetable by:");
        System.out.println("1. Day");
        System.out.println("2. Exercise Type");

        List<Lesson> lessons = null;

        while (true) {

            Integer option = getIntInputAllowCancel("Select filter (0 to cancel): ");
            if (option == null) return;   // 0 cancels and goes back

            switch (option) {
                case 1 -> {
                    Day day = selectDay();
                    if (day == null) return;
                    lessons = system.getTimetable().getLessonsByDay(day);
                    break;
                }
                case 2 -> {
                    ExerciseType exerciseType = selectExerciseType();
                    if (exerciseType == null) return;
                    lessons = system.getTimetable().getLessonsByExercise(exerciseType);
                    break;
                }
                default -> {
                    System.out.println("Invalid option. Try again.");
                    continue;
                }
            }

            break; // exit loop once valid option chosen
        }

        displayTimetable(lessons);

        String lessonId = getStringInput("Enter Lesson ID (0 to cancel): ");
        if (lessonId == null) return;

        Booking booking = system.bookLesson(memberId, lessonId);

        if (booking != null) {
            System.out.println("Booking successful!");
            System.out.println("Booking ID: " + booking.getBookingId());
        } else {
            System.out.println("Booking failed.");
        }
    }

    private static void displayTimetable(List<Lesson> lessons) {

        System.out.println("\n--- TIMETABLE ---");

        printLine(80);

        // First header line (only last column has text)
        System.out.printf("%-9s  %-16s  %-11s  %-11s  %-6s | %-8s%n", "", "", "", "", "", "Available");

        // Second header line (full header)
        System.out.printf("%-8s | %-15s | %-10s | %-10s | %-6s | %-8s%n", "ID", "Exercise", "Day", "Time", "Price", "Spots");

        printLine(80);

        for (Lesson lesson : lessons) {

            int availableSpots = lesson.getCapacity() - lesson.getBookings().size();

            System.out.printf("%-8s | %-15s | %-10s | %-10s | £%-5.2f | %-6d%n", lesson.getLessonId(), lesson.getExerciseType(), lesson.getDay(), lesson.getTimeSlot(), lesson.getPrice(), availableSpots);
        }

        printLine(80);
    }

    // ================= CHANGE/CANCEL =================
    private static void changeOrCancelBooking() {
        String memberId = getStringInput("Enter Member ID (0 to cancel): ");
        if (memberId == null) return;

        Member member = system.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        // ✅ List current bookings
        List<Booking> bookings = member.getBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found for this member.");
            return;
        }

        Booking booking = selectBooking(member, bookings);
        if (booking == null) return;

        System.out.println("1. Change booking");
        System.out.println("2. Cancel booking");

//        int choice = getIntInput("Select: ");
        Integer choice = getIntInputAllowCancel();
        if (choice == null) return;

        if (choice == 1) {

            displayTimetable(system.getTimetable().getLessons());

            String newLessonId = getStringInput("Enter new Lesson ID (0 to cancel): ");
            if (newLessonId == null) return;

            Lesson newLesson = system.findLessonById(newLessonId);

            if (newLesson == null) {
                System.out.println("Lesson not found.");
                return;
            }

            boolean success = booking.changeLesson(newLesson);

            System.out.println(success ? "Booking changed." : "Change failed.");

        } else if (choice == 2) {

            boolean success = booking.cancelBooking();

            System.out.println(success ? "Booking cancelled." : "Cancel failed.");
        }
    }

    // ================= ATTEND =================
    private static void attendLesson() {

        String memberId = getStringInput("Enter Member ID (0 to cancel): ");
        if (memberId == null) return;

        // Find member in system
        Member member = system.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        // ✅ Step 1: List all active bookings
        List<Booking> bookings = member.getBookings().stream().filter(b -> b.getStatus() != BookingStatus.CANCELLED).toList();

        if (bookings.isEmpty()) {
            System.out.println("No active bookings found for this member.");
            return;
        }

        Booking booking = selectBooking(member, bookings);
        if (booking == null) return;

        if (booking.getStatus() == BookingStatus.ATTENDED) {
            System.out.println("This booking has already been attended.");
            return;
        }

        String review = getLineInput("Enter your review (0 to cancel): ");
        if (review == null) return;

        Integer rating = getRatingInput(
                "Enter rating (1=Very dissatisfied, 5=Very satisfied, 0 to cancel): ");

        if (rating == null) return;

        // ✅ Step 5: Call the existing Booking method
        boolean success = booking.attendLesson(rating, review);
        if (success) {
            System.out.println("Lesson attended successfully!");
            System.out.println("Status updated to: " + booking.getStatus());
        } else {
            System.out.println("Failed to attend lesson. Check booking status or rating.");
        }
    }

    // ================= REPORTS =================
    private static void monthlyLessonReport() {

        System.out.println("\n--- Monthly Lesson Report ---");

        printLine(80);
        System.out.printf("%-10s | %-12s | %-10s | %-10s | %-8s | %-10s%n", "Lesson ID", "Exercise", "Day", "Time", "Attended", "Avg Rating");
        printLine(80);

        for (Lesson lesson : system.getTimetable().getLessons()) {

            long attendedCount = lesson.getBookings().stream().filter(b -> b.getStatus() == BookingStatus.ATTENDED).count();

            System.out.printf("%-10s | %-12s | %-10s | %-10s | %-8d | %-10.1f%n", lesson.getLessonId(), lesson.getExerciseType(), lesson.getDay(), lesson.getTimeSlot(), attendedCount, lesson.calculateAverageRating());
        }

        printLine(80);
    }

private static void monthlyChampionReport() {

    System.out.println("\n--- Monthly Champion Exercise Report ---");

    var lessons = system.getTimetable().getLessons();

    // Create summary list
    record ExerciseSummary(ExerciseType type, double income, long attended) {}

    List<ExerciseSummary> summaries = new java.util.ArrayList<>();

    for (ExerciseType type : ExerciseType.values()) {

        double totalIncome = lessons.stream()
                .filter(l -> l.getExerciseType() == type)
                .mapToDouble(Lesson::calculateIncome)
                .sum();

        long totalAttended = lessons.stream()
                .filter(l -> l.getExerciseType() == type)
                .flatMap(l -> l.getBookings().stream())
                .filter(b -> b.getStatus() == BookingStatus.ATTENDED)
                .count();

        summaries.add(new ExerciseSummary(type, totalIncome, totalAttended));
    }

    // ✅ Sort by income (highest first)
    summaries.sort((a, b) -> Double.compare(b.income(), a.income()));

    printLine(60);
    System.out.printf("%-5s | %-15s | %-12s | %-12s%n",
            "Rank", "Exercise", "Income", "Total Attended");
    printLine(60);

    int rank = 1;

    for (ExerciseSummary summary : summaries) {

        System.out.printf("%-5d | %-15s | £%-11.2f | %-12d%n",
                rank,
                summary.type().getDisplayName(),
                summary.income(),
                summary.attended());

        rank++;
    }

    printLine(60);

    // ✅ Show Champion
    if (!summaries.isEmpty()) {
        ExerciseSummary champion = summaries.get(0);
        System.out.println("\n🏆 Champion Exercise: "
                + champion.type().getDisplayName()
                + " (£" + String.format("%.2f", champion.income()) + ")");
    }
}

    // ================= DISPLAYS =================
    private static void displayBookingList(List<Booking> bookings) {
        System.out.println("\n--- CURRENT BOOKINGS ---");
        printLine(68);
        System.out.printf("%-10s | %-15s | %-10s | %-10s | %-10s%n", "Booking ID", "Exercise", "Day", "Time", "Status");
        printLine(68);

        for (Booking b : bookings) {
            System.out.printf("%-10s | %-15s | %-10s | %-10s | %-10s%n", b.getBookingId(), b.getLesson().getExerciseType(), b.getLesson().getDay(), b.getLesson().getTimeSlot(), b.getStatus());
        }

        printLine(68);
    }

    private static String getLineInput(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Operation cancelled. Returning to main menu...");
            return null;
        }

        return input;
    }

    private static Integer getRatingInput(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Operation cancelled. Returning to main menu...");
                return null;
            }

            try {
                int rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5) {
                    return rating;
                } else {
                    System.out.println("Rating must be between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static ExerciseType selectExerciseType() {

        ExerciseType[] types = ExerciseType.values();

        System.out.println("\nSelect Exercise Type:");
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName());
        }
        System.out.println("0. Cancel");

        while (true) {
            Integer choice = getIntInputAllowCancel("Select option: ");
            if (choice == null) return null;

            if (choice >= 1 && choice <= types.length) {
                return types[choice - 1];
            }

            System.out.println("Invalid option. Try again.");
        }
    }

    private static Day selectDay() {

        Day[] days = Day.values();

        System.out.println("\nSelect Day:");
        for (int i = 0; i < days.length; i++) {
            System.out.println((i + 1) + ". " + days[i]);
        }
        System.out.println("0. Cancel");

        while (true) {
            Integer choice = getIntInputAllowCancel();
            if (choice == null) continue;

            if (choice == 0) return null;

            if (choice >= 1 && choice <= days.length) {
                return days[choice - 1];

            }

            System.out.println("Invalid option. Try again.");
        }
    }

    private static Booking selectBooking(Member member, List<Booking> bookings) {

        displayBookingList(bookings);

        String bookingId = getStringInput("Enter Booking ID (0 to cancel): ");
        if (bookingId == null) return null;

        Booking booking = member.findBookingById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found.");
            return null;
        }

        return booking;
    }
}

