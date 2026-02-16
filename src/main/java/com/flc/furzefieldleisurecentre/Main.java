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
    private static final ReportService reportService = new ReportService(system);
    private static final BookingService bookingService = new BookingService(system);

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            printMenu();

            Integer choice = getIntInputAllowCancel();
            if (choice == null) return;

            switch (choice) {
                case 1 -> bookLesson();
                case 2 -> changeOrCancelBooking();
                case 3 -> attendLesson();
                case 4 -> reportService.generateMonthlyLessonReport();
                case 5 -> reportService.generateMonthlyChampionReport();
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

        var members = system.getMembers();
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

        List<Lesson> lessons;

        while (true) {
            Integer option = getIntInputAllowCancel("Select filter (0 to cancel): ");
            if (option == null) return;

            switch (option) {
                case 1 -> {
                    Day day = selectDay();
                    if (day == null) return;
                    lessons = system.getTimetable().getLessonsByDay(day);
                }
                case 2 -> {
                    ExerciseType type = selectExerciseType();
                    if (type == null) return;
                    lessons = system.getTimetable().getLessonsByExercise(type);
                }
                default -> {
                    System.out.println("Invalid option. Try again.");
                    continue;
                }
            }
            break;
        }

        displayTimetable(lessons);

        String lessonId = getStringInput("Enter Lesson ID (0 to cancel): ");
        if (lessonId == null) return;

        Booking booking = bookingService.bookLesson(memberId, lessonId);
        if (booking != null) {
            System.out.println("Booking successful! Booking ID: " + booking.getBookingId());
        } else {
            System.out.println("Booking failed. See messages above.");
        }
    }

    private static void displayTimetable(List<Lesson> lessons) {
        System.out.println("\n--- TIMETABLE ---");
        printLine(80);
        System.out.printf("%-8s | %-15s | %-10s | %-10s | %-6s | %-8s%n", "ID", "Exercise", "Day", "Time", "Price", "Spots");
        printLine(80);

        for (Lesson lesson : lessons) {
            int available = lesson.getCapacity() - lesson.getBookings().size();
            System.out.printf("%-8s | %-15s | %-10s | %-10s | £%-5.2f | %-6d%n",
                    lesson.getLessonId(),
                    lesson.getExerciseType(),
                    lesson.getDay(),
                    lesson.getTimeSlot(),
                    lesson.getPrice(),
                    available);
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

        List<Booking> bookings = member.getBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found for this member.");
            return;
        }

        Booking booking = selectBooking(member, bookings);
        if (booking == null) return;

        System.out.println("1. Change booking");
        System.out.println("2. Cancel booking");

        Integer choice = getIntInputAllowCancel();
        if (choice == null) return;

        if (choice == 1) {
            displayTimetable(system.getTimetable().getLessons());
            String newLessonId = getStringInput("Enter new Lesson ID (0 to cancel): ");
            if (newLessonId == null) return;

            boolean success = bookingService.changeBooking(booking, newLessonId);
            System.out.println(success ? "Booking changed successfully." : "Change failed.");
        } else if (choice == 2) {
            boolean success = bookingService.cancelBooking(booking);
            System.out.println(success ? "Booking cancelled successfully." : "Cancel failed.");
        }
    }

    // ================= ATTEND =================
    private static void attendLesson() {

        String memberId = getStringInput("Enter Member ID (0 to cancel): ");
        if (memberId == null) return;

        Member member = system.findMemberById(memberId);
        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        List<Booking> activeBookings = member.getBookings().stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .toList();

        if (activeBookings.isEmpty()) {
            System.out.println("No active bookings for this member.");
            return;
        }

        Booking booking = selectBooking(member, activeBookings);
        if (booking == null) return;

        String review = getStringInput("Enter your review (0 to cancel): ");
        if (review == null) return;

        Integer rating = getIntInputAllowCancel("Enter rating (1-5, 0 to cancel): ");
        if (rating == null || rating < 1 || rating > 5) {
            System.out.println("Invalid rating. Operation cancelled.");
            return;
        }

        boolean success = bookingService.attendLesson(booking, rating, review);
        System.out.println(success ? "Lesson attended successfully." : "Failed to attend lesson.");
    }

    // ================= UTILS =================
    private static ExerciseType selectExerciseType() {
        ExerciseType[] types = ExerciseType.values();
        System.out.println("\nSelect Exercise Type:");
        for (int i = 0; i < types.length; i++) System.out.println((i + 1) + ". " + types[i].getDisplayName());
        System.out.println("0. Cancel");

        while (true) {
            Integer choice = getIntInputAllowCancel("Select option: ");
            if (choice == null) return null;
            if (choice >= 1 && choice <= types.length) return types[choice - 1];
            System.out.println("Invalid option. Try again.");
        }
    }

    private static Day selectDay() {
        Day[] days = Day.values();
        System.out.println("\nSelect Day:");
        for (int i = 0; i < days.length; i++) System.out.println((i + 1) + ". " + days[i]);
        System.out.println("0. Cancel");

        while (true) {
            Integer choice = getIntInputAllowCancel();
            if (choice == null || choice == 0) return null;
            if (choice >= 1 && choice <= days.length) return days[choice - 1];
            System.out.println("Invalid option. Try again.");
        }
    }

    private static Booking selectBooking(Member member, List<Booking> bookings) {
        System.out.println("\n--- CURRENT BOOKINGS ---");
        printLine(68);
        System.out.printf("%-10s | %-15s | %-10s | %-10s | %-10s%n", "Booking ID", "Exercise", "Day", "Time", "Status");
        printLine(68);

        for (Booking b : bookings) {
            System.out.printf("%-10s | %-15s | %-10s | %-10s | %-10s%n",
                    b.getBookingId(), b.getLesson().getExerciseType(), b.getLesson().getDay(),
                    b.getLesson().getTimeSlot(), b.getStatus());
        }
        printLine(68);

        String bookingId = getStringInput("Enter Booking ID (0 to cancel): ");
        if (bookingId == null) return null;

        Booking booking = member.findBookingById(bookingId);
        if (booking == null) System.out.println("Booking not found.");
        return booking;
    }
}