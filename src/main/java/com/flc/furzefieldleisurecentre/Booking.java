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

    // SETTERS for BookingService
    public void setLesson(Lesson lesson) { this.lesson = lesson; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public void setRating(int rating) { this.rating = rating; }
    public void setReview(String review) { this.review = review; }
}