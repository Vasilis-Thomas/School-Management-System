package entities;

import java.sql.Timestamp;

public class Grade {
    private int student_id, semester;
    private double grade;
    private String student_name, classroom_name, course_title, course_code;
    private Timestamp registration_date;

    public Grade(int student_id, String student_name, String classroom_name, String course_title, String course_code, int semester, double grade, Timestamp registration_date) {
        this.student_id = student_id;
        this.student_name = student_name;
        this.classroom_name = classroom_name;
        this.course_title = course_title;
        this.course_code = course_code;
        this.semester = semester;
        this.grade = grade;
        this.registration_date = registration_date;
    }


    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getClassroom_name() {
        return classroom_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public Timestamp getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Timestamp registration_date) {
        this.registration_date = registration_date;
    }
}
