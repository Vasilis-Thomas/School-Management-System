CREATE SCHEMA IF NOT EXISTS schoolManagement;
SET search_path TO schoolManagement;

------------------------------------Dashboard-------------------------------------

DROP FUNCTION IF EXISTS getOverallStats();

CREATE OR REPLACE FUNCTION getOverallStats()
RETURNS TABLE(
	students_overall BIGINT,
	teachers_overall BIGINT,
	courses_overall BIGINT,
	classrooms_overall BIGINT	
) AS 
$$
DECLARE
	student_c BIGINT;
	teacher_c BIGINT;
	course_c BIGINT;
	classroom_c BIGINT;
BEGIN
	select count(*) INTO student_c from students;
	select count(*) INTO teacher_c from teachers;
	select count(*) INTO classroom_c from classrooms;
	select count(*) INTO course_c from courses;

	RETURN QUERY
	SELECT student_c, teacher_c, course_c, classroom_c;
END;
$$ LANGUAGE plpgsql;

------------------------------------Teachers--------------------------------------

DROP FUNCTION IF EXISTS getTeachers(varchar,varchar,varchar);

-- Returns teachers in tableview according to filters
CREATE OR REPLACE function getTeachers(param1 varchar, param2 varchar, param3 varchar)
RETURNS TABLE (
	teacher_id INT,
	teacher_full_name TEXT,
	gender VARCHAR,
	specialty VARCHAR,
	email VARCHAR,
	phone VARCHAR,
	address VARCHAR) AS
$$
DECLARE 
	query_text VARCHAR;
BEGIN
	query_text := 'SELECT T.teacher_id, CONCAT(T.last_name, '' '', T.first_name) AS Full_Name, 
					T.teacher_gender, T.specialty, T.email, T.phone, T.address
					FROM Teachers T
					WHERE 1 = 1';
	
	IF param1 <> 'All Genders' THEN
		query_text := query_text || ' AND T.teacher_gender = ' || quote_literal(param1);
	END IF;
	
	IF param2 <> 'All Specialties' THEN
		query_text := query_text || ' AND T.specialty = ' || quote_literal(param2);
	END IF;

	IF param3 <> '' THEN
		query_text := query_text || ' AND CONCAT(T.last_name, '' '', T.first_name) ILIKE ''%' || $3 || '%'' ';
	END IF;

	query_text := query_text || ' ORDER BY CONCAT(T.last_name, '' '', T.first_name)';

	RETURN QUERY EXECUTE query_text;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getTeacherGenders();

-- get filter1 value (gender)
CREATE OR REPLACE FUNCTION getTeacherGenders()
RETURNS TABLE (
	teacher_gender VARCHAR(50)
)
AS 
$$
BEGIN
    RETURN QUERY
	SELECT DISTINCT T.teacher_gender
	FROM Teachers T
	ORDER BY T.teacher_gender;
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS getTeacherSpecialties();

-- get filter2 value (specialty)
CREATE OR REPLACE FUNCTION getTeacherSpecialties()
RETURNS TABLE (
	specialty VARCHAR(50)) AS 
$$
BEGIN
    RETURN QUERY
	SELECT DISTINCT T.specialty
	FROM Teachers T
	ORDER BY T.specialty;
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS getTeacherDetails(int);

-- Prints Teacher details according to teacher selected 
CREATE OR REPLACE function getTeacherDetails(param1 int)
RETURNS TABLE (
	teacher_id INT,
	teacher_name TEXT,
	teacher_gender VARCHAR,
	specialty VARCHAR,
	teaches TEXT,
	email VARCHAR,
	phone VARCHAR,
	address VARCHAR,
	modification_date TIMESTAMP) AS
$$
BEGIN
	RETURN QUERY 
	SELECT T.teacher_id, CONCAT(T.last_name, ' ', T.first_name) AS Full_Name, T.teacher_gender,
			T.specialty, STRING_AGG(C.course_code, ', '), T.email, T.phone, T.address, T.modification_date
	FROM Teachers T LEFT OUTER JOIN TEaches Te ON (Te.teacher_id = T.teacher_id)
					LEFT OUTER JOIN Courses C ON(Te.course_id = C.course_id)
	WHERE T.teacher_id = $1
	GROUP BY T.teacher_id;
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS getTeacher(int);

-- Returns Teacher by id
CREATE OR REPLACE function getTeacher(param1 int)
RETURNS TABLE (
	teacher_id INT,
	teacher_first_name VARCHAR,
	teacher_last_name VARCHAR,
	gender VARCHAR,
	specialty VARCHAR,
	email VARCHAR,
	phone VARCHAR,
	address VARCHAR) AS
$$
BEGIN
	RETURN QUERY
	SELECT T.teacher_id, T.first_name, T.last_name, T.teacher_gender,
			T.specialty, T.email, T.phone, T.address
	FROM Teachers T
	WHERE T.teacher_id = param1;
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS insertTeacher(varchar,varchar,varchar,varchar,varchar,varchar,varchar);

-- Inserts new record in table teachers function
CREATE OR REPLACE FUNCTION insertTeacher(first_name VARCHAR, last_name VARCHAR,
					    teacher_gender VARCHAR, specialty VARCHAR, email VARCHAR, 
					    phone VARCHAR, address VARCHAR)
RETURNS void AS
$$
BEGIN
   INSERT INTO Teachers (first_name, last_name, teacher_gender,  specialty, email, phone, address, modification_date)
	VALUES ($1,$2,$3,$4,$5,$6,$7, CURRENT_TIMESTAMP);
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS updateTeacher(varchar, varchar, varchar, varchar, varchar, varchar, varchar, integer);

-- Updates record in table teachers function
CREATE OR REPLACE FUNCTION updateTeacher(varchar, varchar, varchar, varchar, varchar, varchar, varchar, integer)
RETURNS void AS
$$
UPDATE teachers
SET first_name = $1,
	last_name = $2,
	teacher_gender = $3,
	specialty = $4,
	email = $5,
	phone = $6,
	address = $7
WHERE teacher_id = $8;
$$ LANGUAGE SQL;


DROP FUNCTION IF EXISTS deleteTeacher (int);

-- Deletes record of table teachers function
CREATE OR REPLACE FUNCTION deleteTeacher(param1 INT)
RETURNS void AS
$$
BEGIN
	DELETE FROM teachers WHERE teacher_id = $1 ;
END;
$$ LANGUAGE 'plpgsql';


------------------------------------Students--------------------------------------

DROP VIEW IF EXISTS averageGrade;

--creates a table with the average grade of the students based on the current class they are
CREATE OR REPLACE VIEW averageGrade AS
SELECT St.student_id, CONCAT(St.first_name, ' ', St.last_name) AS Full_Name, AVG(G.grade) AS Average_Grade
FROM students St LEFT OUTER JOIN grades G ON (St.student_id = G.student_id)
				LEFT OUTER JOIN courses Co ON (G.course_id = Co.course_id)
WHERE Co.class_id = (
			SELECT C.class_id
			FROM students S LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
							LEFT OUTER JOIN classes Cl ON (C.class_id = Cl.class_id)
			WHERE S.student_id = St.student_id )
GROUP BY St.student_id
ORDER BY St.student_id;


DROP FUNCTION IF EXISTS getStudents(varchar,varchar,varchar,varchar);

CREATE OR REPLACE FUNCTION getStudents(
	param1 VARCHAR,
	param2 VARCHAR,
	grade_range VARCHAR,
	search_name VARCHAR
) RETURNS TABLE (
    student_id INT,
    student_name TEXT,
    date_of_birth DATE,
    gender VARCHAR,
    classroom VARCHAR,
    email VARCHAR,
    phone VARCHAR,
    address VARCHAR,
    average_grade DOUBLE PRECISION
) AS $$
DECLARE
    query_text VARCHAR;
    min_grade DOUBLE PRECISION;
    max_grade DOUBLE PRECISION;
BEGIN

	IF grade_range <> 'Average Grade' THEN
		-- Assuming your grade_range parameter is in the format "min-max"   
		-- Extract minimum and maximum values from the range
		min_grade := CAST(SUBSTRING(grade_range FROM 1 FOR POSITION('-' IN grade_range) - 1) AS DOUBLE PRECISION);
		max_grade := CAST(SUBSTRING(grade_range FROM POSITION('-' IN grade_range) + 1) AS DOUBLE PRECISION);

		-- Display a message with the extracted values
		RAISE NOTICE 'Filtering students with grades between % and %', min_grade, max_grade;
   	END IF;


    query_text := 'SELECT S.student_id, CONCAT(S.last_name, '' '', S.first_name) AS Full_Name, S.date_of_birth, S.student_gender,
                          C.classroom_name, S.email, S.phone, S.address, averageGrade.average_grade
                   FROM students S
                        LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
                        LEFT OUTER JOIN averageGrade ON (averageGrade.student_id = S.student_id)
                   WHERE 1 = 1';

    IF param1 <> 'All Genders' THEN
        query_text := query_text || ' AND S.student_gender = ' || quote_literal(param1);
    END IF;

    IF param2 <> 'All Classrooms' THEN
        query_text := query_text || ' AND C.classroom_name = ' || quote_literal(param2);
    END IF;

    IF grade_range <> 'Average Grade' THEN
        query_text := query_text || ' AND averageGrade.average_grade > ' || min_grade || ' AND averageGrade.average_grade < ' || max_grade;
    END IF;
	
	IF search_name <> ' ' THEN
		query_text := query_text || ' AND CONCAT(S.LAST_NAME , '' '', S.first_name) ILIKE ''%' ||  search_name || '%'' ';
	END IF;

    query_text := query_text || ' ORDER BY CONCAT(S.last_name, '' '', S.first_name)';

    -- Execute the dynamic query
    RETURN QUERY EXECUTE query_text;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getClassroomNames();

--get filter value
CREATE OR REPLACE FUNCTION getClassroomNames()
RETURNS TABLE (
    classroom_name VARCHAR(50)
)
AS 
$$
BEGIN
    RETURN QUERY
    SELECT DISTINCT c.classroom_name
    FROM Classrooms c
    ORDER BY c.classroom_name;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getStudentGenders();

--get filter value
CREATE OR REPLACE FUNCTION getStudentGenders()
RETURNS TABLE (
    student_gender VARCHAR(50)
)
AS 
$$
BEGIN
    RETURN QUERY
	SELECT DISTINCT S.student_gender
	FROM Students S;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getStudent(int);

-- Returns Student by id
CREATE OR REPLACE function getStudent(param1 int)
RETURNS TABLE (
	student_id INT,
	student_first_name VARCHAR,
	student_last_name VARCHAR,
	date_of_birth DATE,
	gender VARCHAR,
	classroom VARCHAR,
	email VARCHAR,
	phone VARCHAR,
	address VARCHAR) AS
$$
BEGIN
	RETURN QUERY
	SELECT S.student_id, S.first_name, S.last_name, S.date_of_birth, S.student_gender,
			C.classroom_name, S.email, S.phone, S.address
	FROM students S LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
	WHERE S.student_id = param1;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getStudentDetails(int);

CREATE OR REPLACE function getStudentDetails(param1 int)
RETURNS TABLE (
	student_id INT,
	full_Name TEXT,
	date_of_birth DATE,
	gender VARCHAR,
	classroom VARCHAR,
	email VARCHAR,
	phone VARCHAR,
	address VARCHAR,
	average_grade DOUBLE PRECISION,
	registration_date DATE,
	modification_date TIMESTAMP
) AS $$
BEGIN
	RETURN QUERY
	SELECT S.student_id, CONCAT(S.last_name,' ', S.first_name) AS full_Name, S.date_of_birth, S.student_gender,
			C.classroom_name, S.email, S.phone, S.address, averageGrade.average_grade, 
			S.registration_date, S.modification_date
	FROM students S LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
					LEFT OUTER JOIN averageGrade ON (averageGrade.student_id = S.student_id)
	WHERE S.student_id = param1;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS insertStudent(VARCHAR, VARCHAR, DATE, VARCHAR, VARCHAR, VARCHAR, VARCHAR, VARCHAR);

--Inserts new record in table Students
CREATE OR REPLACE FUNCTION insertStudent(first_name VARCHAR, last_name VARCHAR, date_of_birth DATE,
                        student_gender VARCHAR, email VARCHAR, classroom_name VARCHAR,
                        phone VARCHAR, address VARCHAR)
RETURNS void AS
$$
DECLARE
	classroom_id_temp INT;
BEGIN

	SELECT cl.classroom_id INTO classroom_id_temp 
	FROM Classrooms cl
	WHERE cl.classroom_name = $6;

   INSERT INTO Students (first_name, last_name, date_of_birth, student_gender, email, classroom_id, phone, address, modification_date)
    VALUES ($1,$2,$3,$4,$5,classroom_id_temp,$7,$8, CURRENT_TIMESTAMP);
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS updateStudent(varchar, varchar, date, varchar, varchar, varchar, varchar, varchar, integer);

--Updates record of table Student
CREATE OR REPLACE FUNCTION updateStudent(varchar, varchar, date, varchar, varchar, varchar, varchar, varchar, integer)
RETURNS void AS $$
DECLARE
	classroom_id_temp INT;
BEGIN

	SELECT cl.classroom_id INTO classroom_id_temp 
	FROM Classrooms cl
	WHERE cl.classroom_name = $6;

	UPDATE students
	SET first_name = $1,
		last_name = $2,
		date_of_birth = $3,
		student_gender = $4,
		email = $5,
		classroom_id = classroom_id_temp,
		phone = $7,
		address = $8,
		modification_date = CURRENT_TIMESTAMP
	WHERE student_id = $9;
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS deleteStudent(int);

-- Deletes record of table students
CREATE OR REPLACE FUNCTION deleteStudent(param1 INT)
RETURNS void AS
$$
BEGIN
	DELETE FROM students WHERE student_id = $1 ;
END;
$$ LANGUAGE 'plpgsql';

------------------------------------Courses---------------------------------------

DROP FUNCTION IF EXISTS getCourses( VARCHAR, VARCHAR, VARCHAR,VARCHAR); 

CREATE OR REPLACE FUNCTION getCourses(
    param1 VARCHAR,
    param2 VARCHAR,
	param3 VARCHAR,
	search_title VARCHAR 
) RETURNS TABLE (
    course_id INT,
    title VARCHAR,
    course_code VARCHAR,
    hours_per_week INT,
    class_grade VARCHAR,
	orientation VARCHAR,
    course_description TEXT
) AS $$
DECLARE
    query_text VARCHAR;	
    last_name VARCHAR;
    first_name VARCHAR;	
BEGIN
    query_text := 'SELECT DISTINCT c.course_id, c.title, c.course_code, c.hours_per_week, cl.class_grade, cl.orientation, c.course_description
                   FROM Courses c
                        LEFT OUTER JOIN Classes cl ON (c.class_id = cl.class_id)
                        LEFT OUTER JOIN Teaches ts ON (c.course_id = ts.course_id)
						LEFT OUTER JOIN Teachers t ON (ts.teacher_id = t.teacher_id)
                   WHERE 1 = 1';

    IF param1 <> 'All Classes' THEN
        query_text := query_text || ' AND cl.class_grade = ' || quote_literal(param1);
    END IF;

    IF param2 <> 'All Orientations' THEN
        query_text := query_text || ' AND cl.orientation = ' || quote_literal(param2);
    END IF;

    IF param3 <> 'All Teachers' THEN
		-- Extract first and last name from the full name
		last_name := CAST(SUBSTRING(param3 FROM 1 FOR POSITION(' ' IN param3) - 1) AS VARCHAR);
		first_name := CAST(SUBSTRING(param3 FROM POSITION(' ' IN param3) + 1) AS VARCHAR);

		-- Display a message with the extracted values
		RAISE NOTICE 'Last Name: % First Name: %', last_name, first_name;
	
        query_text := query_text || ' AND t.last_name = ''' || last_name || ''' AND t.first_name = ''' || first_name || '''';
    END IF;
	
	IF search_title <> ' ' THEN
		query_text := query_text || ' AND CONCAT(c.title) ILIKE ''%' ||  search_title || '%'' ';
	END IF;

    query_text := query_text || ' ORDER BY c.course_id';

    -- Execute the dynamic query
    RETURN QUERY EXECUTE query_text;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getClasses();

--get filter value
CREATE OR REPLACE FUNCTION getClasses()
RETURNS TABLE(
	class_grade VARCHAR
) AS
$$
	SELECT DISTINCT(C.class_grade)
	FROM Classes C
	ORDER BY C.class_grade;
$$ LANGUAGE SQL;


DROP FUNCTION IF EXISTS getOrientations();

--get filter value
CREATE OR REPLACE FUNCTION getOrientations()
RETURNS TABLE(
	orientation VARCHAR
) AS
$$
	SELECT DISTINCT(C.orientation)
	FROM Classes C
	ORDER BY C.orientation;
$$ LANGUAGE SQL;


DROP FUNCTION IF EXISTS getTeacherNames();

--get filter value
CREATE OR REPLACE FUNCTION getTeacherNames()
RETURNS TABLE (
    teacherNames TEXT
)
AS 
$$
BEGIN
    RETURN QUERY
    SELECT DISTINCT CONCAT(t.last_name, ' ', t.first_name) AS Full_Name
    FROM Teachers t
    ORDER BY Full_Name;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getCourseDetails(int);

CREATE OR REPLACE FUNCTION getCourseDetails(
	param int
) RETURNS TABLE (
	course_id INT,
	course_title VARCHAR,
	course_code VARCHAR,
	hours_per_week INT,
	class_grade VARCHAR,
	orientaton VARCHAR,
	course_description TEXT) AS $$
BEGIN
	RETURN QUERY
	SELECT c.course_id, c.title, c.course_code, c.hours_per_week, cl.class_grade, cl.orientation, c.course_description  
	FROM Courses c LEFT OUTER JOIN Classes cl ON (c.class_id = cl.class_id)
	WHERE c.course_id = param; 
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS insertCourse(VARCHAR, VARCHAR, INT, VARCHAR, VARCHAR, TEXT);

-- Inserts new record in table Courses
CREATE OR REPLACE FUNCTION insertCourse(title VARCHAR, course_code VARCHAR, hours_per_week INT,
					    class_grade VARCHAR, orietation VARCHAR, course_description TEXT)
RETURNS void AS
$$
DECLARE
	class_id_temp INT;
BEGIN

	SELECT cl.class_id INTO class_id_temp
	FROM Classes cl
	WHERE cl.orientation = $5 AND cl.class_grade = $4;


   INSERT INTO Courses (title, course_code, hours_per_week, class_id, course_description)
	VALUES ($1,$2,$3,class_id_temp,$6);
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS updateCourse(INT, VARCHAR, VARCHAR, INT, VARCHAR, VARCHAR, TEXT);

-- Updates record of table Courses
CREATE OR REPLACE FUNCTION updateCourse(INT, VARCHAR, VARCHAR, INT, VARCHAR, VARCHAR, TEXT)
RETURNS void AS $$
DECLARE
	class_id_temp INT;
BEGIN

	SELECT cl.class_id INTO class_id_temp
	FROM Classes cl
	WHERE cl.orientation = $6 AND cl.class_grade = $5;

	UPDATE Courses
	SET title = $2,
		course_code = $3,
		hours_per_week = $4,
		class_id = class_id_temp,
		course_description = $7	
	WHERE course_id = $1;
	
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS deleteCourse(INT);

-- Deletes record of table courses function
CREATE OR REPLACE FUNCTION deleteCourse(param1 INT)
RETURNS void AS
$$
BEGIN
	DELETE FROM Courses WHERE course_id = $1 ;
END;
$$ LANGUAGE 'plpgsql';


------------------------------------Grades----------------------------------------

DROP FUNCTION IF EXISTS getGrades(VARCHAR, VARCHAR, INT, VARCHAR);

CREATE OR REPLACE FUNCTION getGrades(
	param1 VARCHAR,
	param2 VARCHAR,
	param3 INT,
	search_name VARCHAR
) RETURNS TABLE (
    student_id INT,
    student_name TEXT,
    classroom_name VARCHAR,
    course_title VARCHAR,
    course_code VARCHAR,
    semester INT,
    grade DOUBLE PRECISION,
    registration_date TIMESTAMP
) AS $$
DECLARE
    query_text VARCHAR;	
	
BEGIN
    query_text := 'SELECT st.student_id, CONCAT(st.first_name, '' '', st.last_name) AS Full_Name, get_classroom_name(st.classroom_id) AS Classroom_name, co.title, co.course_code,g.semester, g.grade, g.registration_date    
					FROM Grades g JOIN Students st ON (g.student_id = st.student_id)
								JOIN Courses co ON (g.course_id = co.course_id)
					WHERE Co.class_id = (
								SELECT C.class_id
								FROM students S LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
												LEFT OUTER JOIN classes Cl ON (C.class_id = Cl.class_id)
								WHERE S.student_id = St.student_id)';

    IF param1 <> 'All Classrooms' THEN
        query_text := query_text || ' AND get_classroom_name(st.classroom_id) = ' || quote_literal(param1);
    END IF;

    IF param2 <> 'All Courses' THEN
        query_text := query_text || ' AND co.course_code = ' || quote_literal(param2);
    END IF;

    IF param3 IN (1, 2) THEN
		query_text := query_text || ' AND g.semester = ' || quote_literal(param3);
    END IF;

    IF search_name <> ' ' THEN
		query_text := query_text || ' AND CONCAT(st.first_name, '' '', st.last_name) ILIKE ''%' ||  search_name || '%'' ';
    END IF;

    query_text := query_text || ' ORDER BY st.student_id, co.course_code, co.title';

    -- Execute the dynamic query
    RETURN QUERY EXECUTE query_text;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getCourseCodes();

--get filter value
CREATE OR REPLACE FUNCTION getCourseCodes()
RETURNS TABLE (
    course_code VARCHAR(50)
)
AS 
$$
BEGIN
    RETURN QUERY
    SELECT DISTINCT c.course_code
    FROM Courses c;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getSemesters(); 

--get filter value
CREATE OR REPLACE FUNCTION getSemesters()
RETURNS TABLE (
    semester INT
)
AS 
$$
BEGIN
    RETURN QUERY
    SELECT DISTINCT g.semester
    FROM Grades g;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS get_classroom_name(INT);

-- Returns the classrooms names form the ids
CREATE OR REPLACE FUNCTION get_classroom_name(param INT)
RETURNS VARCHAR(50) AS $$
DECLARE
    classroom_name_var VARCHAR(50);
BEGIN
    -- Retrieve the classroom_name based on the provided classroom_id
    SELECT c.classroom_name INTO classroom_name_var
    FROM Classrooms c
    WHERE c.classroom_id = param;

    -- Return the result
    RETURN classroom_name_var;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getGradeDetails(INT, VARCHAR, INT);

CREATE OR REPLACE FUNCTION getGradeDetails(param1 INT, param2 VARCHAR, param3 INT)
RETURNS TABLE (
    student_id INT,
    student_name TEXT,
    classroom_name VARCHAR,
    course_title VARCHAR,
    course_code VARCHAR,
	semester INT,
    grade DOUBLE PRECISION,
	registration_date TIMESTAMP)
AS $$
DECLARE
    cid INT;
BEGIN
	
	SELECT DISTINCT g.course_id INTO cid
	FROM Grades g JOIN Courses c ON (g.course_id = c.course_id)
	WHERE c.course_code = $2;

	RETURN QUERY
	SELECT st.student_id, CONCAT(st.first_name, ' ', st.last_name) AS Full_Name, get_classroom_name(st.classroom_id) AS Classroom_name, co.title, co.course_code,g.semester, g.grade, g.registration_date    
	FROM Grades g JOIN Students st ON (g.student_id = st.student_id)
				JOIN Courses co ON (g.course_id = co.course_id)
	WHERE Co.class_id = (
				SELECT C.class_id
				FROM students S LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
								LEFT OUTER JOIN classes Cl ON (C.class_id = Cl.class_id)
				WHERE S.student_id = St.student_id)
			AND g.student_id = $1 AND g.course_id = cid AND g.semester = $3; 
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getGrade(INT, VARCHAR, INT);

--Returns Grade by Student, Course and Semester
CREATE OR REPLACE FUNCTION getGrade(param1 INT, param2 VARCHAR, param3 INT)
RETURNS TABLE (
	student_id INT,
	course_code VARCHAR,
	semester INT,
	grade DOUBLE PRECISION)
AS $$
DECLARE
    cid INT;
BEGIN
	
	SELECT DISTINCT g.course_id INTO cid
	FROM Grades g JOIN Courses c ON (g.course_id = c.course_id)
	WHERE c.course_code = $2;

	RETURN QUERY
	SELECT st.student_id, co.course_code,g.semester, g.grade  
	FROM Grades g JOIN Students st ON (g.student_id = st.student_id)
				JOIN Courses co ON (g.course_id = co.course_id)
	WHERE Co.class_id = (
				SELECT C.class_id
				FROM students S LEFT OUTER JOIN classrooms C ON (C.classroom_id = S.classroom_id)
								LEFT OUTER JOIN classes Cl ON (C.class_id = Cl.class_id)
				WHERE S.student_id = St.student_id)
			AND g.student_id = $1 AND g.course_id = cid AND g.semester = $3; 
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS insertGrade(INT, VARCHAR, INT, DOUBLE PRECISION);

-- Inserts new record in table Grades
CREATE OR REPLACE FUNCTION insertGrade(sid INT, ccd VARCHAR, sem INT, grd DOUBLE PRECISION)
RETURNS void AS
$$
DECLARE
	cid INT;
BEGIN

	SELECT DISTINCT g.course_id INTO cid
	FROM Grades g JOIN Courses c ON (g.course_id = c.course_id)
	WHERE c.course_code = $2;

   INSERT INTO Grades(student_id, course_id, semester, grade, registration_date)
	VALUES ($1,cid,$3,$4,CURRENT_TIMESTAMP);
END;
$$ LANGUAGE 'plpgsql';


DROP FUNCTION IF EXISTS updateGrade(INT, VARCHAR, INT, DOUBLE PRECISION);

-- Updates record of table Grades
CREATE OR REPLACE FUNCTION updateGrade(INT, VARCHAR, INT, DOUBLE PRECISION)
RETURNS void AS $$
DECLARE
	cid INT;
BEGIN
	
	SELECT DISTINCT g.course_id INTO cid
	FROM Grades g JOIN Courses c ON (g.course_id = c.course_id)
	WHERE c.course_code = $2;

	UPDATE Grades
	SET student_id = $1,
		course_id = cid,
		semester = $3,
		grade = $4,
		registration_date = CURRENT_TIMESTAMP	
	WHERE student_id = $1 AND course_id = cid AND semester = $3;
	RAISE NOTICE 'STUDENT_ID: % COURSE_ID: % SEMESTER: %', $1,cid,$3;
	
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS deleteGrade(INT, VARCHAR, INT);

-- Deletes record of table grades
CREATE OR REPLACE FUNCTION deleteGrade(param1 INT, param2 VARCHAR, param3 INT)
RETURNS void AS
$$
DECLARE
    cid INT;
BEGIN

	SELECT DISTINCT g.course_id INTO cid
	FROM Grades g JOIN Courses c ON (g.course_id = c.course_id)
	WHERE c.course_code = $2;

	DELETE FROM Grades WHERE student_id = $1 AND course_id = cid AND semester = $3 ;
END;
$$ LANGUAGE 'plpgsql';


------------------------------------Classrooms------------------------------------

--//-TODO-//-- CHECK IF THE FILTER FUNCTIONS ARE UNIQUE


DROP FUNCTION IF EXISTS getClassrooms(varchar, varchar);  

-- Print Classroom tableview according to filters
CREATE OR REPLACE function getClassrooms(param1 varchar, param2 varchar)
RETURNS TABLE (
	classroom_id INT,
	classroom_name VARCHAR,
	class_grade VARCHAR,
	class_orientation VARCHAR
	) AS
$$
BEGIN
IF param1='All Classes' AND param2='All Orientations' THEN
	RETURN QUERY 
		SELECT CR.classroom_id, CR.classroom_name, C.class_grade, C.orientation
		FROM Classrooms CR INNER JOIN Classes C ON (CR.class_id = C.class_id) 
		ORDER BY C.class_grade;
ELSIF param2='All Orientations' THEN
	RETURN QUERY 
		SELECT CR.classroom_id, CR.classroom_name, C.class_grade, C.orientation
		FROM Classrooms CR INNER JOIN Classes C ON (CR.class_id = C.class_id) 
		WHERE C.class_grade = $1
		ORDER BY C.class_grade;
	 RAISE NOTICE 'Parameter 1: %', param1;
ELSIF param1='All Classes' THEN
	RETURN QUERY 
		SELECT CR.classroom_id, CR.classroom_name, C.class_grade, C.orientation
		FROM Classrooms CR INNER JOIN Classes C ON (CR.class_id = C.class_id) 
		WHERE C.orientation = $2
		ORDER BY C.class_grade;
	RAISE NOTICE 'Parameter 2: %', param2;
ELSE
	RETURN QUERY 
		SELECT CR.classroom_id, CR.classroom_name, C.class_grade, C.orientation
		FROM Classrooms CR INNER JOIN Classes C ON (CR.class_id = C.class_id) 
		WHERE C.class_grade = $1 AND C.orientation = $2
		ORDER BY C.class_grade;
	 RAISE NOTICE 'Parameter 1: %', param1;
	  RAISE NOTICE 'Parameter 2: %', param2;
END IF;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getClassesNames();

-- get filter value
CREATE OR REPLACE FUNCTION getClassesNames()
RETURNS TABLE(
	class_grade VARCHAR
) AS
$$
	SELECT DISTINCT(C.class_grade)
	FROM Classes C
	ORDER BY C.class_grade;
$$ LANGUAGE SQL;


DROP FUNCTION IF EXISTS getClassesOrientations();

-- get filter value
CREATE OR REPLACE FUNCTION getClassesOrientations()
RETURNS TABLE(
	orientation VARCHAR
) AS
$$
	SELECT DISTINCT(C.orientation)
	FROM Classes C
	ORDER BY C.orientation;
$$ LANGUAGE SQL;


DROP FUNCTION IF EXISTS getClassroomDetails(int);

CREATE OR REPLACE FUNCTION getClassroomDetails(param1 int)
RETURNS TABLE(
	classroom_id INT,
	classroom_name VARCHAR,
	classroom_class VARCHAR,
	orientation VARCHAR,
	classroom_size BIGINT
) AS $$
BEGIN
	RETURN QUERY
	SELECT CR.classroom_id, CR.classroom_name, C.class_grade , C.orientation, count(S.student_id) AS classroom_size
	FROM classrooms CR LEFT OUTER JOIN students S ON (CR.classroom_id = S.classroom_id)
					   LEFT OUTER JOIN Classes C ON (CR.class_id = C.class_id)
	WHERE CR.classroom_id = $1			  
	GROUP BY CR.classroom_id, C.class_grade, C.orientation
	ORDER BY CR.classroom_id;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS getClassroom(int); 

-- get classroom field values by id
CREATE OR REPLACE function getClassroom(param1 int)
RETURNS TABLE (
	classroom_id INT,
	classroom_name VARCHAR,
	classroom_class VARCHAR,
	orientation VARCHAR) AS
$$
BEGIN
	RETURN QUERY
	SELECT CR.classroom_id, CR.classroom_name, C.class_grade, C.orientation
	FROM classrooms CR LEFT OUTER JOIN Classes C ON (CR.class_id = C.class_id)
	WHERE CR.classroom_id = $1
	ORDER BY C.class_grade;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS insertClassroom(VARCHAR, VARCHAR, VARCHAR);

-- Inserts new record in table classrooms
CREATE OR REPLACE FUNCTION insertClassroom(classroom_name VARCHAR, class_grade VARCHAR, orientation VARCHAR)
RETURNS void AS
$$
DECLARE
	class_id_temp INT;
BEGIN
	SELECT class_id INTO class_id_temp
	FROM Classes C 
	WHERE C.class_grade = $2 AND C.orientation = $3;

   INSERT INTO Classrooms (classroom_name, class_id)
	VALUES ($1,class_id_temp);
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS updateClassroom(varchar, varchar, varchar, int);

-- Updates record in table classrooms
CREATE OR REPLACE FUNCTION updateClassroom(varchar, varchar, varchar, int)
RETURNS void AS
$$
DECLARE
	class_id_temp INT;
BEGIN
	SELECT class_id INTO class_id_temp
	FROM Classes C 
	WHERE C.class_grade = $2 AND C.orientation = $3;
	
	UPDATE Classrooms
	SET classroom_name = $1,
		class_id = class_id_temp
WHERE classroom_id = $4;
END
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS deleteClassroom(INT);

-- Deletes record of table classroom
CREATE OR REPLACE FUNCTION deleteClassroom(param1 INT)
RETURNS void AS
$$
	DELETE FROM classrooms WHERE classroom_id = $1 ;
$$ LANGUAGE SQL;


------------------------------------History---------------------------------------

DROP TABLE IF EXISTS log;

CREATE TABLE Log (
    log_id SERIAL NOT NULL UNIQUE PRIMARY KEY,
	action_type VARCHAR(20), -- INSERT, UPDATE, DELETE
    table_name VARCHAR(50),
    record_id TEXT,
    user_id TEXT, -- If your system has user accounts
	action_description TEXT,
	action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


DROP TRIGGER IF EXISTS students_log_trigger ON Students;
DROP TRIGGER IF EXISTS teachers_log_trigger ON Teachers;
DROP TRIGGER IF EXISTS classrooms_trigger ON Classrooms;
DROP TRIGGER IF EXISTS classes_trigger ON Classes;
DROP TRIGGER IF EXISTS courses_trigger ON Courses;
DROP TRIGGER IF EXISTS grades_trigger ON Grades;
DROP TRIGGER IF EXISTS teaches_trigger ON Teaches;

DROP FUNCTION IF EXISTS log_trigger();

-- log_trigger v2
CREATE OR REPLACE FUNCTION log_trigger()
RETURNS TRIGGER AS $$
DECLARE
    pk_column_name TEXT[];
    record_id TEXT :='';
	temp_value TEXT;
BEGIN
    -- Get the primary key column name
    SELECT array_agg(column_name::text) INTO pk_column_name
    FROM information_schema.key_column_usage
    WHERE table_name = TG_TABLE_NAME
        AND constraint_name = (
            SELECT constraint_name
            FROM information_schema.table_constraints
            WHERE table_name = TG_TABLE_NAME
                AND constraint_type = 'PRIMARY KEY'
        );

    -- Process the primary key values
    FOR i IN 1..array_length(pk_column_name, 1) LOOP
        -- Get the values dynamically
        IF TG_OP = 'INSERT' THEN
            EXECUTE 'SELECT $1.' || pk_column_name[i] || '::TEXT'
            USING NEW
            INTO temp_value;
        ELSIF TG_OP = 'UPDATE' THEN
            EXECUTE 'SELECT $1.' || pk_column_name[i] || '::TEXT'
            USING NEW
            INTO temp_value;
        ELSIF TG_OP = 'DELETE' THEN
            EXECUTE 'SELECT $1.' || pk_column_name[i] || '::TEXT'
            USING OLD
            INTO temp_value;
        END IF;

        -- Concatenate the values of all columns in the composite primary key
        record_id := record_id || ' Primary Key Column Name: ' || pk_column_name[i] || ' Value: ' || temp_value; 
    END LOOP;

    -- Insert into the log table
    INSERT INTO Log (action_type, table_name, record_id, user_id, action_description)
    VALUES (TG_OP, TG_TABLE_NAME, record_id, current_user, TG_OP || ' info: ' || 
			CASE 
				WHEN TG_OP = 'INSERT' THEN
					'New values: ' || NEW.*
				WHEN TG_OP = 'UPDATE' THEN
					'Old values: ' || OLD.* || ', New values: ' || NEW.*
				WHEN TG_OP = 'DELETE' THEN
					'Old values: ' || OLD.*																		
			END
    );

    RETURN NULL; -- For AFTER triggers, we don't need to return anything.
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER students_log_trigger
AFTER INSERT OR UPDATE OR DELETE ON Students
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();

CREATE TRIGGER teachers_log_trigger
AFTER INSERT OR UPDATE OR DELETE ON Teachers
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();

CREATE TRIGGER classrooms_trigger
AFTER INSERT OR UPDATE OR DELETE ON Classrooms
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();

CREATE TRIGGER classes_trigger
AFTER INSERT OR UPDATE OR DELETE ON Classes
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();

CREATE TRIGGER courses_trigger
AFTER INSERT OR UPDATE OR DELETE ON Courses
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();

CREATE TRIGGER teaches_trigger
AFTER INSERT OR UPDATE OR DELETE ON Teaches
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();

CREATE TRIGGER grades_trigger
AFTER INSERT OR UPDATE OR DELETE ON Grades
FOR EACH ROW
EXECUTE PROCEDURE log_trigger();


DROP FUNCTION IF EXISTS getLog();

CREATE OR REPLACE FUNCTION getLog()
RETURNS SETOF log AS 
$$
BEGIN
    RETURN QUERY
    SELECT *
    FROM log;
END;
$$ LANGUAGE plpgsql;
