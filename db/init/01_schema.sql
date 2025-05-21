-- テーブル定義
CREATE TABLE IF NOT EXISTS statuses (
    status_id INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE,
    display_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS students (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(20) NOT NULL,
    duration INT NOT NULL DEFAULT 6,
    is_closed TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS students_courses (
    attending_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    CONSTRAINT fk_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS course_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attending_id INT NOT NULL,
    status_id INT NOT NULL,
    CONSTRAINT fk_course_status_attending FOREIGN KEY (attending_id)
        REFERENCES students_courses(attending_id) ON DELETE CASCADE,
    CONSTRAINT fk_course_status_status FOREIGN KEY (status_id)
        REFERENCES statuses(status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
