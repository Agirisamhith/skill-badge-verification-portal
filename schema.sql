CREATE DATABASE IF NOT EXISTS skill_badge_portal;
USE skill_badge_portal;

CREATE TABLE IF NOT EXISTS Students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Modules (
    module_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    unit VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Badges (
    badge_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    module_id INT NOT NULL,
    verification_code VARCHAR(255) UNIQUE NOT NULL,
    score INT DEFAULT 0,
    tier VARCHAR(20) DEFAULT 'Bronze',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (module_id) REFERENCES Modules(module_id) ON DELETE CASCADE
);

-- Insert some seed data for demonstration
INSERT IGNORE INTO Students (name, email) VALUES 
('Alice Johnson', 'alice@example.com'),
('Bob Smith', 'bob@example.com');

INSERT IGNORE INTO Modules (title, unit) VALUES 
('Servlet Fundamentals', 'Backend Web'),
('JDBC Basics', 'Database Connectivity'),
('CGI Internals', 'Web Architecture');
