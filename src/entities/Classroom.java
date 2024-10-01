package entities;

public class Classroom {
    private int classroom_id;
    private String classroom_name;
    private String class_grade;
    private String class_orientation;

    public int getClassroom_id() {
        return classroom_id;
    }

    public void setClassroom_id(int crid) {
        this.classroom_id = crid;
    }

    public String getClassroom_name() {
        return classroom_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String getClass_grade() {
        return class_grade;
    }

    public void setClass_grade(String class_grade) {
        this.class_grade = class_grade;
    }

    public String getClass_orientation() {
        return class_orientation;
    }

    public void setClass_orientation(String class_orientation) {
        this.class_orientation = class_orientation;
    }

    public Classroom() {
    }

    public Classroom(int crid, String classroom_name, String class_grade, String class_orientation) {
        this.classroom_id = crid;
        this.classroom_name = classroom_name;
        this.class_grade = class_grade;
        this.class_orientation = class_orientation;
    }
}
