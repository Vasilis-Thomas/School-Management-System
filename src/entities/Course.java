package entities;

public class Course {
    private Integer course_id,  hours_per_week;
    private  String title, course_code, class_grade, orientation, course_description;

    public Course(Integer course_id, String title, String course_code, Integer hours_per_week, String class_grade, String orientation, String course_description) {
        this.course_id = course_id;
        this.title = title;
        this.course_code = course_code;
        this.hours_per_week = hours_per_week;
        this.class_grade = class_grade;
        this.orientation = orientation;
        this.course_description = course_description;
    }

    public Integer getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Integer course_id) {
        this.course_id = course_id;
    }

    public Integer getHours_per_week() {
        return hours_per_week;
    }

    public void setHours_per_week(Integer hours_per_week) {
        this.hours_per_week = hours_per_week;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getClass_grade() {
        return class_grade;
    }

    public void setClass_grade(String class_grade) {
        this.class_grade = class_grade;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getCourse_description() {
        return course_description;
    }

    public void setCourse_description(String course_description) {
        this.course_description = course_description;
    }
}
