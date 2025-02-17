CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(50) NOT NULL,
    name_pronunciation VARCHAR(50) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    age TINYINT NOT NULL,
    gender VARCHAR(10) NOT NULL DEFAULT 'Other',
    remark VARCHAR(200) NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    CHECK (gender IN ('Male', 'Female', 'Other'))
);

CREATE TABLE students_courses (
    attending_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    deadline DATE NOT NULL,
    CONSTRAINT fk_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE
);