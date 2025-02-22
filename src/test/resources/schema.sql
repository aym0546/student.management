-- 受講生情報
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(50) NOT NULL,
    name_pronunciation VARCHAR(50) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    birth_date DATE NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL DEFAULT 'Other',
    remark VARCHAR(200) NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- コースマスタ
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(50) NOT NULL,
    category VARCHAR(20) NOT NULL,
    duration INT NOT NULL DEFAULT 6,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    CHECK (category IN('制作系コース', '開発系コース'))
);

-- 受講生の受講コース情報
CREATE TABLE students_courses (
    attending_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_courses FOREIGN KEY (course_id)
        REFERENCES courses(course_id) ON DELETE CASCADE
);

-- ステータスマスタ
CREATE TABLE course_statuses (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 受講ステータス履歴情報
CREATE TABLE students_courses_status (
    status_history_id INT AUTO_INCREMENT PRIMARY KEY,
    attending_id INT NOT NULL,
    status_id INT NOT NULL,
    status_start_date DATE NOT NULL,
    status_end_date DATE NULL,
    change_reason VARCHAR(200) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_courses FOREIGN KEY (attending_id)
        REFERENCES students_courses(attending_id) ON DELETE CASCADE,
    CONSTRAINT fk_course_statuses FOREIGN KEY (status_id)
        REFERENCES course_statuses(status_id) ON DELETE CASCADE,
    CONSTRAINT unique_attending_status UNIQUE (attending_id, status_id, status_start_date)
    -- 受講ID/ステータスID/ステータス開始日の重複は不可
);