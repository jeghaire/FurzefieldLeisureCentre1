package com.flc.furzefieldleisurecentre;

import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private final FLCSystem system;

    public ReportService(FLCSystem system) {
        this.system = system;
    }

    private void printLine(int length) {
        System.out.println("-".repeat(length));
    }

    // ================= MONTHLY LESSON REPORT =================
    public void generateMonthlyLessonReport() {

        System.out.println("\n--- Monthly Lesson Report ---");

        printLine(80);
        System.out.printf("%-10s | %-12s | %-10s | %-10s | %-8s | %-10s%n",
                "Lesson ID", "Exercise", "Day", "Time", "Attended", "Avg Rating");
        printLine(80);

        for (Lesson lesson : system.getTimetable().getLessons()) {

            long attendedCount = lesson.getBookings().stream()
                    .filter(b -> b.getStatus() == BookingStatus.ATTENDED)
                    .count();

            System.out.printf("%-10s | %-12s | %-10s | %-10s | %-8d | %-10.1f%n",
                    lesson.getLessonId(),
                    lesson.getExerciseType().getDisplayName(),
                    lesson.getDay(),
                    lesson.getTimeSlot(),
                    attendedCount,
                    lesson.calculateAverageRating());
        }

        printLine(80);
    }

    // ================= MONTHLY CHAMPION REPORT =================
    public void generateMonthlyChampionReport() {

        System.out.println("\n--- Monthly Champion Exercise Report ---");

        var lessons = system.getTimetable().getLessons();

        record ExerciseSummary(ExerciseType type, double income, long attended) {}

        List<ExerciseSummary> summaries = new ArrayList<>();

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

        summaries.sort((a, b) -> Double.compare(b.income(), a.income()));

        printLine(60);
        System.out.printf("%-5s | %-15s | %-12s | %-12s%n",
                "Rank", "Exercise", "Income", "Total Attended");
        printLine(60);

        int rank = 1;

        for (ExerciseSummary summary : summaries) {
            System.out.printf("%-5d | %-15s | £%-11.2f | %-12d%n",
                    rank++,
                    summary.type().getDisplayName(),
                    summary.income(),
                    summary.attended());
        }

        printLine(60);

        if (!summaries.isEmpty()) {
            ExerciseSummary champion = summaries.get(0);
            System.out.println("\n🏆 Champion Exercise: "
                    + champion.type().getDisplayName()
                    + " (£" + String.format("%.2f", champion.income()) + ")");
        }
    }
}
