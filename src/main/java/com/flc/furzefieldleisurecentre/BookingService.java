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

        boolean alreadyBooked = member.getBookings().stream()
                .anyMatch(b -> b.getLesson().equals(lesson)
                        && b.getStatus() != BookingStatus.CANCELLED);

        if (alreadyBooked) {
            System.out.println("Member already has a booking for this lesson.");
            return null;
        }

        if (!lesson.hasSpace()) {
            System.out.println("Lesson is full.");
            return null;
        }

        String bookingId = "B" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6);

        Booking booking = new Booking(bookingId, member, lesson);

        lesson.addBooking(booking);
        member.getBookings().add(booking);

        return booking;
    }

    public boolean changeBooking(Booking booking, String newLessonId) {

        Lesson newLesson = system.findLessonById(newLessonId);
        if (newLesson == null) return false;

        if (!newLesson.hasSpace()) {
            System.out.println("New lesson is full.");
            return false;
        }

        if (booking.getLesson().equals(newLesson)) {
            System.out.println("Booking already for this lesson.");
            return false;
        }

        // Remove from old lesson
        booking.getLesson().removeBooking(booking);

        // Add to new lesson
        newLesson.addBooking(booking);

        return booking.changeLesson(newLesson);
    }

    public boolean cancelBooking(Booking booking) {

        if (!booking.cancel()) {
            return false;
        }

        booking.getLesson().removeBooking(booking);
        return true;
    }

    public boolean attendLesson(Booking booking, int rating, String review) {
        return booking.attend(rating, review);
    }
}