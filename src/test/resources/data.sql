-- students テーブルにデータを挿入
INSERT INTO students (full_name, name_pronunciation, nickname, email, area, birth_date, gender, remark, is_deleted) VALUES
('山田 太郎', 'やまだ たろう', 'たろちゃん', 'taro.yamada@example.com', '東京', '2000-01-01', 'Male', 'スポーツが好き', 0),
('鈴木 花子', 'すずき はなこ', 'はなちゃん', 'hanako.suzuki@example.com', '大阪', '2000-02-02', 'Female', '読書が趣味', 0),
('佐藤 健', 'さとう けん', 'けんくん', 'ken.sato@example.com', '福岡', '2000-03-03', 'Male', '', 0),
('高橋 美咲', 'たかはし みさき', 'みさみさ', 'misaki.takahashi@example.com', '北海道', '2000-04-04', 'Female', '旅行が好き', 0),
('中村 俊介', 'なかむら しゅんすけ', 'しゅん', 'shun.nakamura@example.com', '愛知', '2000-05-05', 'Male', '料理が得意', 0);

-- students_courses テーブルにデータを挿入
INSERT INTO students_courses (student_id, course_id, start_date, end_date) VALUES
(1, 1, '2025-01-10', '2025-06-30'),
(2, 2, '2025-02-15', '2025-08-15'),
(3, 3, '2025-03-01', '2025-09-01'),
(4, 4, '2025-04-20', '2025-10-20'),
(5, 5, '2025-05-05', '2025-11-05'),
(1, 6, '2025-06-01', '2025-12-01'), -- 山田 太郎が複数のコースを受講
(2, 7, '2025-07-10', '2026-01-10'); -- 鈴木 花子が複数のコースを受講;

-- statuses テーブルにデータを挿入
INSERT INTO statuses (status_name, display_order, is_active, is_final) VALUES
('仮申し込み', 1, 0, 0),
('入金待ち', 2, 0, 0),
('本申し込み', 3, 0, 0),
('受講中', 4, 1, 0),
('受講終了', 5, 0, 1),
('受講中断', 6, 0, 0),
('キャンセル', 99, 0, 1);

-- course_status テーブルにデータを挿入
INSERT INTO course_status (attending_id, status_id) VALUES
(1, 5),
(2, 5),
(3, 4),
(4, 4),
(5, 3),
(6, 3),
(7, 1);

-- courses テーブルにデータを挿入　
INSERT INTO courses (course_name, category, duration, is_closed) VALUES
('Javaコース', '開発系コース', 6, 0),
('AWSコース', '開発系コース', 6, 0),
('WordPressコース', '制作系コース', 6, 0),
('デザインコース', '制作系コース', 6, 0),
('webマーケティングコース', '制作系コース', 6, 0),
('映像制作コース', '制作系コース', 6, 0),
('フロントエンドコース', '開発系コース', 6, 0);
