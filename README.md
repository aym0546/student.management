# アプリの概要

スクールの運営支援を行うアプリです。

受講生のプロフィール、受講生が所属しているコースとその受講期間、申し込み状況などを管理できます。

# ER図

```mermaid
---
title: "StudentManagement"
---

erDiagram
    students ||--|{ students_courses: "受講生は複数のコースを受講できる"
    students_courses ||--|| course_status: "コース情報に対応する受講・申込状況は1:1"
    courses ||--o{ students_courses: "受講情報がもつコースIDは1つ"
    statuses ||--o{ course_status: "受講・申込状況がもつステータスIDは1つ"

    students {
        int student_id PK "受講生ID"
        varchar(50) full_name "氏名"
        varchar(50) name_pronunciation "ふりがな"
        varchar(20) nickname "ニックネーム"
        varchar(100) email "メールアドレス"
        varchar(100) area "地域"
        date birth_date "誕生日"
        enum gender "[Male, Female, Other]"
        varchar(200) remark "備考"
        boolean is_deleted "論理削除"
    }

    students_courses {
        int attending_id PK "受講ID"
        int student_id FK "受講生ID"
        int course_id FK "コースID"
        date start_date "受講開始日"
        date end_date "受講終了(予定)日"

    }

    courses {
        int course_id PK "コースID"
        varchar(50) course_name "コース名"
        varchar(20) category "コースカテゴリ"
        int duration "受講期間"
        boolean is_closed "開講状況"
        timestamp created_at
        timestamp updated_at
    }

    course_status {
        int id PK "受講・申込状況ID"
        int attending_id FK "受講ID"
        int status_id FK "ステータスID"
    }

    statuses {
        int status_id PK "ステータスID"
        varchar(20) status_name "ステータス名"
        int display_order "表示順制御"
        boolean is_active "進行中"
        boolean is_final "最終状態"
        timestamp created_at
        timestamp updated_at
    }
```

# データベース

![image](https://github.com/user-attachments/assets/cbf6fd0e-a676-4b0e-baf3-bba38a00f459)

# 使用技術

## バックエンド

- Java（21.0.5）
- SpringBoot（3.4.1）

## インフラ・DB

- AWS
- MySQL（3.9.4）
- JUnit（5.11.4）

## その他

- Postman
- Git / GitHub
- Docker

# 機能一覧

|    | 機能                        |
|----|---------------------------|
| 1  | 受講生プロフィールの登録              |
| 2  | 受講生プロフィールの更新              |
| 3  | 受講生プロフィールの削除（論理削除）        |
| 4  | 受講生プロフィールの検索（ID指定）        |
| 5  | 受講生プロフィールの詳細検索（条件指定）      |
| 6  | 受講情報の登録                   |
| 7  | 受講情報の更新                   |
| 8  | 受講情報の検索（ID指定）             |
| 9  | 受講情報の検索（条件指定）             |
| 10 | 受講状況（ステータス）の登録            |
| 11 | 受講状況（ステータス）の更新            |
| 12 | 受講状況（ステータス）のクローズ          |
| 13 | 受講状況（ステータス）の検索            |
| 14 | 受講生プロフィール・コース情報・ステータス複合検索 |
| 15 | コースマスタの登録                 |
| 16 | コースマスタの更新                 |
| 17 | コースマスタのクローズ               |
| 18 | コースマスタの全件検索               |

# 何ができるのか

## 受講生情報管理

### 受講生プロフィール・受講情報・受講ステータスの登録

<img width="793" alt="image" src="https://github.com/user-attachments/assets/63db5a25-b356-47e7-8df5-ec05f7a965a0" />
<img width="793" alt="image" src="https://github.com/user-attachments/assets/771ec2ba-5d1d-4cde-89e1-02f64ca6424c" />

### 受講生プロフィール・受講情報・受講ステータスの更新

<img width="793" alt="image" src="https://github.com/user-attachments/assets/16be7ad6-b18e-49ff-9630-42899fd7f57e" />

### 受講生プロフィールの削除（論理削除）

<img width="793" alt="image" src="https://github.com/user-attachments/assets/dd84bbb8-68d0-482a-b172-c2bd0aa84837" />

### 受講生プロフィールの検索（ID指定）

<img width="793" alt="image" src="https://github.com/user-attachments/assets/f573f6aa-ad71-4b5d-b4d0-2f1cd780b8ba" />

### 受講生プロフィール・受講情報・受講ステータス詳細検索（複合条件指定）

<img width="871" alt="image" src="https://github.com/user-attachments/assets/40b3d172-797e-4a14-b7e5-5894747c460d" />

### クエリパラメータ指定なしの詳細検索（全件検索）

<img width="871" alt="image" src="https://github.com/user-attachments/assets/d601c3e3-41b0-473f-8c87-ca2ea4b6114a" />

## コース情報管理

### コースマスタの全件取得

  <img width="869" alt="image" src="https://github.com/user-attachments/assets/5aaf4b1b-55b6-48ef-b73b-5d9d22c862b3" />

### コースマスタの登録

  <img width="869" alt="image" src="https://github.com/user-attachments/assets/65f5eda0-b884-49f3-a2ef-c59ec4aa4528" />

### コースマスタの更新

  <img width="791" alt="image" src="https://github.com/user-attachments/assets/eaaf2edc-df7a-48cd-98dc-2005308bdf23" />

### コースマスタのクローズ

  <img width="791" alt="image" src="https://github.com/user-attachments/assets/61a20dfa-5263-4698-a4fa-dcec508676bc" />

## 工夫したところ（設計面）

1. コースをID管理・マスタテーブルを作成し、今後のコース増減（講師違い・期間違いなど）に柔軟に対応できるよう設計
2. 受講状況（ステータス）の終了日について、コース情報から算出
3. 受講ステータスもID管理・マスタテーブルを作成し、ステータスごとの属性・分類ができるよう設計（未実装）


## 工夫したところ（実装面）

1. 検索において、年齢で検索条件指定を受け取り、内部で誕生日に変換して検索

    ```java
    public StudentSearchDTO toDTO() {
    
    	// 年齢指定をbirthDateに置き換え
    	var today = LocalDate.now();
    	var startBirthDate =
    		(maxAge() != null) ? today.minusYears(maxAge() + 1).plusDays(1)
    			: null;
    	var endBirthDate =
    		(minAge() != null) ? today.minusYears(minAge() + 1).minusDays(1)
    			: null;
    
    	// statusをList<String>からList<Status>に変換
    	var statusDTOList = Optional.ofNullable(status())
    				.map(statusList -> statusList.stream().map(Status::valueOf).toList())
    				.orElse(List.of());  // statusがnullの時は空リストList.of()を返す
    
    	// リクエストデータをStudentSearchFormからStudentSearchDTOに詰め替え
    	return new StudentSearchDTO(
    		name(), startBirthDate, endBirthDate, area(), email(), gender(), remark(), courseId(),
    		category(), startDate(), endDate(), statusDTOList);
    
    }
    ```

2. 受講情報検索はコースID指定だけでなく、カテゴリ指定でも行えるよう実装


    ```xml
    <select id="findCourse" resultType="raisetech.student.management.data.StudentsCourse">
    	SELECT DISTINCT sc.* FROM students_courses sc
    	JOIN courses c ON sc.course_id = c.course_id
    	<where>
    		<if test='courseId != null'>
    			AND sc.course_id = #{courseId}
    		</if>
    		<if test='category != null and !category.isBlank()'>
    			AND c.category = #{category}
    		</if>
    	</where>
    </select>
    ```

3. 単体テスト実装
   <img width="1203" alt="image" src="https://github.com/user-attachments/assets/41950dc1-cc2a-4e12-bba5-58ec4097dff5" />
   <img width="687" alt="image" src="https://github.com/user-attachments/assets/c9fb0195-3c40-42ad-abca-c9114201939c" />
