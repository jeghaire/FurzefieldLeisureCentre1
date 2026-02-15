/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flc.furzefieldleisurecentre;

/**
 *
 * @author Mavi
 */
public class Booking {
    private final String bookingId;
    private BookingStatus status;
    private Lesson lesson;
    private final Member member;

    private int rating;        
    private String review;     

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
        this.status = BookingStatus.BOOKED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public Member getMember() {
        return member;
    }

    public int getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    // ✅ Change lesson
    public boolean changeLesson(Lesson newLesson) {

        if (status == BookingStatus.CANCELLED || status == BookingStatus.ATTENDED) {
            return false; // Cannot change cancelled or attended bookings
        }

        if (!newLesson.hasSpace()) {
            return false; // No capacity
        }

        // Remove from old lesson
        lesson.removeBooking(this);

        // Add to new lesson
        newLesson.addBooking(this);

        // Update lesson reference
        this.lesson = newLesson;

        this.status = BookingStatus.CHANGED;

        return true;
    }

    // ✅ Cancel booking
    public boolean cancelBooking() {

        if (status == BookingStatus.CANCELLED || status == BookingStatus.ATTENDED) {
            return false;
        }

        lesson.removeBooking(this);
        status = BookingStatus.CANCELLED;

        return true;
    }

    // ✅ Attend lesson
    public boolean attendLesson(int rating, String review) {

        if (status == BookingStatus.CANCELLED) {
            return false;
        }

        if (rating < 1 || rating > 5) {
            return false;
        }

        this.rating = rating;
        this.review = review;
        this.status = BookingStatus.ATTENDED;

        return true;
    }
}