CREATE SCHEMA IF NOT EXISTS schoolManagement;
SET search_path TO schoolManagement;

DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS teaches;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS classrooms;
DROP TABLE IF EXISTS classes;


CREATE TABLE Classes (
	class_id SERIAL NOT NULL UNIQUE,
	class_grade VARCHAR(10) CHECK (class_grade IN ('Α','Β', 'Γ')) NOT NULL,
	orientation VARCHAR(100),
	CONSTRAINT class_pk PRIMARY KEY(class_id),
CONSTRAINT Unique_classGrade_orientation UNIQUE (class_grade, orientation));


CREATE TABLE Classrooms (
    classroom_id SERIAL NOT NULL UNIQUE,
    classroom_name VARCHAR(50),
    class_id INT,    
    CONSTRAINT classroom_id_pk PRIMARY KEY(classroom_id),
    CONSTRAINT classroom_fk_class FOREIGN KEY (class_id) REFERENCES Classes(class_id) ON DELETE CASCADE);


CREATE TABLE Students (
    student_id SERIAL NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    student_gender VARCHAR(15) CHECK (student_gender IN ('Άνδρας','Γυναίκα')) NOT NULL,
    classroom_id INT,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(100),
    registration_date DATE,
    modification_date TIMESTAMP NOT NULL,
    CONSTRAINT student_id_pk PRIMARY KEY(student_id),
    CONSTRAINT classroom_id_fk_classroom FOREIGN KEY (classroom_id) REFERENCES Classrooms(classroom_id) ON DELETE SET NULL);


CREATE TABLE Teachers (
    teacher_id SERIAL NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    teacher_gender VARCHAR(15) CHECK (teacher_gender IN ('Άνδρας','Γυναίκα')) NOT NULL,
    specialty VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(100),
    modification_date TIMESTAMP NOT NULL,
    CONSTRAINT teacher_id_pk PRIMARY KEY(teacher_id));


CREATE TABLE Courses (
    course_id SERIAL NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    
    hours_per_week INT,
    
    class_id INT,    
    course_description TEXT,
    CONSTRAINT course_id_pk PRIMARY KEY(course_id),
    CONSTRAINT course_fk_class FOREIGN KEY (class_id) REFERENCES Classes(class_id) ON DELETE SET NULL);


CREATE TABLE Teaches(
	teacher_id INT,
	course_id INT,
	CONSTRAINT teacher_id_fk_teachers FOREIGN KEY (teacher_id) REFERENCES Teachers(teacher_id) ON DELETE CASCADE,
	CONSTRAINT course_id_fk_courses FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
	CONSTRAINT teaches_pk PRIMARY KEY(teacher_id, course_id));


CREATE TABLE Grades (
    student_id INT,
    course_id INT,
    semester INT CHECK (semester IN (1, 2)),
    grade FLOAT,
    registration_date TIMESTAMP,
    CONSTRAINT student_id_fk FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE,
    CONSTRAINT course_id_fk FOREIGN KEY (course_id) REFERENCES Courses(course_id) ON DELETE CASCADE,
    CONSTRAINT grade_pk PRIMARY KEY (student_id, course_id, semester));


