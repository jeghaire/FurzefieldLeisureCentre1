/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flc.furzefieldleisurecentre;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mavi
 */
public class Lesson {
    
    private final String lessonId;
    private final ExerciseType exerciseType;
    private final Day day;
    private final TimeSlot timeSlot;
    private final double price;
    private final int capacity;

    private final List<Booking> bookings;

    public Lesson(String lessonId, ExerciseType exerciseType, Day day, TimeSlot timeSlot, double price, int capacity) {
        this.lessonId = lessonId;
        this.exerciseType = exerciseType;
        this.day = day;
        this.timeSlot = timeSlot;
        this.price = price;
        this.capacity = capacity;
        this.bookings = new ArrayList<>();
    }

    public String getLessonId() {
        return lessonId;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public Day getDay() {
        return day;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public double getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
    
     // ✅ Check if lesson has space
    public boolean hasSpace() {
        return bookings.size() < capacity;
    }

    // ✅ Add booking if space available
    public boolean addBooking(Booking booking) {
        if (hasSpace()) {
            bookings.add(booking);
            return true;
        }
        return false;
    }

    // ✅ Remove booking (used when cancelling or changing)
    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    // ✅ Calculate average rating (only attended bookings)
    public double calculateAverageRating() {
        int totalRating = 0;
        int count = 0;

        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.ATTENDED) {
                totalRating += booking.getRating();
                count++;
            }
        }

        if (count == 0) return 0.0;

        return (double) totalRating / count;
    }

    // ✅ Calculate income (only attended bookings)
    public double calculateIncome() {
        int attendedCount = 0;

        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.ATTENDED) {
                attendedCount++;
            }
        }

        return attendedCount * price;
    }
}
