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

    public String getBookingId() { return bookingId; }
    public BookingStatus getStatus() { return status; }
    public Lesson getLesson() { return lesson; }
    public Member getMember() { return member; }
    public int getRating() { return rating; }
    public String getReview() { return review; }

    // =========================
    // DOMAIN BEHAVIOR METHODS
    // =========================

    public boolean changeLesson(Lesson newLesson) {
        if (status == BookingStatus.CANCELLED || status == BookingStatus.ATTENDED) {
            return false;
        }
        this.lesson = newLesson;
        return true;
    }

    public boolean cancel() {
        if (status == BookingStatus.CANCELLED || status == BookingStatus.ATTENDED) {
            return false;
        }
        status = BookingStatus.CANCELLED;
        return true;
    }

    public boolean attend(int rating, String review) {
        if (status != BookingStatus.BOOKED) {
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