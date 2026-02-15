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
public class Timetable {
    
    private final List<Lesson> lessons;

    public Timetable() {
        lessons = new ArrayList<>();
        generateLessons();
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    // ✅ Generate 8 weekends (48 lessons)
    private void generateLessons() {

        ExerciseType[] exerciseTypes = ExerciseType.values();

        for (int weekend = 1; weekend <= 8; weekend++) {

            for (Day day : Day.values()) {

                for (TimeSlot timeSlot : TimeSlot.values()) {

                    ExerciseType exercise = exerciseTypes[(weekend + day.ordinal() + timeSlot.ordinal()) % exerciseTypes.length];

                    String lessonId = "L" + UUID.randomUUID().toString().substring(0, 6);

                    Lesson lesson = new Lesson(lessonId, exercise, day, timeSlot, exercise.getPrice(), 4);

                    lessons.add(lesson);
                }
            }
        }
    }

    // ✅ View by Day
    public List<Lesson> getLessonsByDay(Day day) {

        List<Lesson> result = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.getDay() == day) {
                result.add(lesson);
            }
        }

        return result;
    }

    // ✅ View by Exercise
    public List<Lesson> getLessonsByExercise(ExerciseType exerciseType) {

        List<Lesson> result = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.getExerciseType() == exerciseType) {
                result.add(lesson);
            }
        }

        return result;
    }
}
