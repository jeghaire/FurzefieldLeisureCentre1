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
public class Member {

    private final String memberId;
    private final String name;
    private final List<Booking> bookings;

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        this.bookings = new ArrayList<>();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    // ✅ Find booking by ID
    public Booking findBookingById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }
}