-- students テーブルにデータを挿入
INSERT INTO students (full_name, name_pronunciation, nickname, email, area, age, gender, remark, is_deleted) VALUES
('山田 太郎', 'やまだ たろう', 'たろちゃん', 'taro.yamada@example.com', '東京', 20, 'Male', 'スポーツが好き', 0),
('鈴木 花子', 'すずき はなこ', 'はなちゃん', 'hanako.suzuki@example.com', '大阪', 22, 'Female', '読書が趣味', 0),
('佐藤 健', 'さとう けん', 'けんくん', 'ken.sato@example.com', '福岡', 19, 'Male', '', 0),
('高橋 美咲', 'たかはし みさき', 'みさみさ', 'misaki.takahashi@example.com', '北海道', 25, 'Female', '旅行が好き', 0),
('中村 俊介', 'なかむら しゅんすけ', 'しゅん', 'shun.nakamura@example.com', '愛知', 21, 'Male', '料理が得意', 0);

-- students_courses テーブルにデータを挿入
INSERT INTO students_courses (student_id, course, start_date, deadline) VALUES
(1, 'Javaコース', '2025-01-10', '2025-06-30'),
(2, 'デザインコース', '2025-02-15', '2025-08-15'),
(3, 'WordPressコース', '2025-03-01', '2025-09-01'),
(4, 'webマーケティングコース', '2025-04-20', '2025-10-20'),
(5, 'フロントエンドコース', '2025-05-05', '2025-11-05'),
(1, 'AWSコース', '2025-06-01', '2025-12-01'), -- 山田 太郎が複数のコースを受講
(2, '映像制作コース', '2025-07-10', '2026-01-10') -- 鈴木 花子が複数のコースを受講;