package com.flc.furzefieldleisurecentre;

import java.util.UUID;

public class BookingService {

    private final FLCSystem system;

    public BookingService(FLCSystem system) {
        this.system = system;
    }

    public Booking bookLesson(String memberId, String lessonId) {

        Member member = system.findMemberById(memberId);
        if (member == null) return null;

        Lesson lesson = system.findLessonById(lessonId);
        if (lesson == null) return null;

        // Prevent duplicate booking
        boolean alreadyBooked = member.getBookings().stream()
                .anyMatch(b -> b.getLesson().equals(lesson) && b.getStatus() != BookingStatus.CANCELLED);
        if (alreadyBooked) {
            System.out.println("Member already has a booking for this lesson.");
            return null;
        }

        if (!lesson.hasSpace()) {
            System.out.println("Lesson is full.");
            return null;
        }

        String bookingId = "B" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);

        Booking booking = new Booking(bookingId, member, lesson);
        lesson.addBooking(booking);
        member.getBookings().add(booking);

        return booking;
    }

    public boolean changeBooking(Booking booking, String newLessonId) {

        Lesson newLesson = system.findLessonById(newLessonId);
        if (newLesson == null) return false;

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return false;
        }

        if (booking.getLesson().equals(newLesson)) {
            System.out.println("Booking is already for this lesson.");
            return false;
        }

        if (!newLesson.hasSpace()) {
            System.out.println("New lesson is full.");
            return false;
        }

        booking.getLesson().removeBooking(booking);
        newLesson.addBooking(booking);

        // Update booking reference via reflection
        try {
            java.lang.reflect.Field lessonField = Booking.class.getDeclaredField("lesson");
            lessonField.setAccessible(true);
            lessonField.set(booking, newLesson);
        } catch (Exception e) {
            System.out.println("⚠ Error updating booking lesson: " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean cancelBooking(Booking booking) {

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return false;
        }

        booking.getLesson().removeBooking(booking);

        try {
            java.lang.reflect.Field statusField = Booking.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(booking, BookingStatus.CANCELLED);
        } catch (Exception e) {
            System.out.println("⚠ Error updating booking status: " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean attendLesson(Booking booking, int rating, String review) {

        if (booking.getStatus() != BookingStatus.BOOKED) {
            return false;
        }

        if (rating < 1 || rating > 5) return false;

        try {
            java.lang.reflect.Field ratingField = Booking.class.getDeclaredField("rating");
            ratingField.setAccessible(true);
            ratingField.set(booking, rating);

            java.lang.reflect.Field reviewField = Booking.class.getDeclaredField("review");
            reviewField.setAccessible(true);
            reviewField.set(booking, review);

            java.lang.reflect.Field statusField = Booking.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(booking, BookingStatus.ATTENDED);

        } catch (Exception e) {
            System.out.println("⚠ Error attending lesson: " + e.getMessage());
            return false;
        }

        return true;
    }
}