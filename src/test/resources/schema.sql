CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(50) NOT NULL,
    name_pronunciation VARCHAR(50) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    birth_date DATE NULL,
    gender VARCHAR(10) NOT NULL DEFAULT 'Other',
    remark VARCHAR(200) NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    CHECK (gender IN ('Male', 'Female', 'Other'))
);

CREATE TABLE students_courses (
    attending_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    CONSTRAINT fk_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE course_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attending_id INT NOT NULL,
    status ENUM('仮申し込み', '本申し込み', '受講中', '受講終了') NOT NULL,
    CONSTRAINT fk_course_status_attending FOREIGN KEY (attending_id)
        REFERENCES students_courses(attending_id) ON DELETE CASCADE
);

CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(50) NOT NULL,
    category VARCHAR(20) NOT NULL,
    duration INT NOT NULL DEFAULT 6,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
