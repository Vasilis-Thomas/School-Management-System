package entities;

import java.sql.Date;
import java.sql.Timestamp;

public class Student {

    private Integer studentID;
    private String first_name;
    private String last_name;
    private String student_name;
    private String classroom;
    private Date date_of_birth;
    private String gender;
    private Integer classroom_id;
    private String email;
    private String phone;
    private String address;
    private Date registration_date;
    private Double average_grade;
    private Timestamp modification_date;

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public Date getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Date registration_date) {
        this.registration_date = registration_date;
    }

    public Timestamp getModification_date() {
        return modification_date;
    }

    public void setModification_date(Timestamp modification_date) {
        this.modification_date = modification_date;
    }

    public Integer getStudent_id() {
        return studentID;
    }

    public void setStudent_id(Integer studentID) {
        this.studentID = studentID;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public String getNameConcat(){
//        String name = new StringBuilder().append(first_name).append(last_name).toString();
        return first_name + " " + last_name;
    }

//    public void setNameConcat(String name){
////        this.name = first_name + " " + last_name;
//        this.name = name;
//    }

//    public void setNameConcat(String first_name, String last_name){
//        this.
//    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }




//    private final StringProperty genderProperty = new SimpleStringProperty();
//    public final StringProperty getGenderProperty(){
//        return genderProperty;
//    }
    public String getGender() {
        return  gender;
//        return genderProperty.get();
    }

    public void setGender(String gender) {
        this.gender = gender;
//        genderProperty.set(gender);
    }

    public Integer getClassroom_id() {
        return classroom_id;
    }

    public void setClassroom_id(Integer classroom_id) {
        this.classroom_id = classroom_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAverage_grade() {
        return average_grade;
    }

    public void setAverage_grade(Double average_grade) {
        this.average_grade = average_grade;
    }

    public Student(){}

//    public String toSqlString() {
//        return String.format("(%d, '%s', '%s', '%s', '%s', %d, '%s', '%s', '%s')",
//                studentID, first_name, last_name, birth, gender,
//                classroom_id, email, phone, address);
//    }

    public Student(Integer studentID, String first_name, String last_name, Date birth, String gender) {
        this.studentID = studentID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = birth;
        this.gender = gender;
    }


    public Student(Integer studentID, String full_name, Date birth, String gender, String classroom_name, String email, String phone, String address, Double average_grade) {
        this.studentID = studentID;
        this.student_name = full_name;
        this.date_of_birth = birth;
        this.gender = gender;
        this.classroom = classroom_name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.average_grade= average_grade;
    }

    public Student(Integer studentID, String first_name, String last_name, Date birth, String gender, Integer classroom_id, String email, String phone, String address) {
        this.studentID = studentID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = birth;
        this.gender = gender;
        this.classroom_id = classroom_id;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Student(String first_name, String last_name, Date birth, String gender, Integer classroom_id, String email, String phone, String address) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = birth;
        this.gender = gender;
        this.classroom_id = classroom_id;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Student(Integer studentID, String first_name, String last_name, Date birth, String gender, Integer classroom_id, String email, String phone, String address, Date registration_date, Timestamp modification_date) {
        this.studentID = studentID;
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_of_birth = birth;
        this.gender = gender;
        this.classroom_id = classroom_id;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registration_date = registration_date;
        this.modification_date = modification_date;
    }
}
