/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.flc.furzefieldleisurecentre;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Mavi
 */
public class FLCSystem {

    private final List<Member> members;
    private final Timetable timetable;

    public FLCSystem() {
        members = new ArrayList<>();
        timetable = new Timetable();
        generateSampleMembers();
    }

    public List<Member> getMembers() {
        return members;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    // ✅ Create some test members
    private void generateSampleMembers() {
        members.add(new Member("M001", "Alice"));
        members.add(new Member("M002", "Bob"));
        members.add(new Member("M003", "Charlie"));
        members.add(new Member("M004", "Steve"));
        members.add(new Member("M005", "Tony"));
    }

    // ✅ Find member by ID
    public Member findMemberById(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }
        return null;
    }

    // ✅ Book lesson
    public Booking bookLesson(String memberId, String lessonId) {

      Member member = findMemberById(memberId);
      if (member == null) return null;

      Lesson lesson = findLessonById(lessonId);
      if (lesson == null) return null;

      String bookingId = "B" + UUID.randomUUID().toString().substring(0, 6);

      boolean success = member.bookLesson(bookingId, lesson);

      return success ? member.findBookingById(bookingId) : null;
  }

    // ✅ Find lesson by ID
    public Lesson findLessonById(String lessonId) {

        for (Lesson lesson : timetable.getLessons()) {
            if (lesson.getLessonId().equals(lessonId)) {
                return lesson;
            }
        }

        return null;
    }
}
