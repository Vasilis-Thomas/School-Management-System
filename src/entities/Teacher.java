package entities;

public class Teacher {
    private int teacher_id;
    private String teacher_full_name, teacher_first_name, teacher_last_name, gender, specialty, email, phone, address;

    public String getTeacher_first_name() {
        return teacher_first_name;
    }

    public void setTeacher_first_name(String teacher_first_name) {
        this.teacher_first_name = teacher_first_name;
    }

    public String getTeacher_last_name() {
        return teacher_last_name;
    }

    public void setTeacher_last_name(String teacher_last_name) {
        this.teacher_last_name = teacher_last_name;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeacher_full_name() {
        return teacher_full_name;
    }

    public void setTeacher_full_name(String teacher_full_name) {
        this.teacher_full_name = teacher_full_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
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

    public Teacher(int teacher_id, String teacher_full_name, String gender, String specialty, String email, String phone, String address) {
        this.teacher_id = teacher_id;
        this.teacher_full_name = teacher_full_name;
        this.gender = gender;
        this.specialty = specialty;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
