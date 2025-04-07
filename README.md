# アプリの概要

スクールの運営支援を行うアプリです。

受講生のプロフィール、受講生が所属しているコースとその受講期間、申し込み状況などを管理できます。

# ER図

```mermaid
---
title: "受講生管理システム"
---

erDiagram
    students ||--|{ students_courses: "受講生は複数のコースを受講できる"
    students_courses ||--|| course_status: "コース情報に対応する受講状況・申し込み状況は1:1"
    courses ||--o{ students_courses: "受講情報がもつコースIDは1つ"

    students {
        Integer student_id PK "受講生ID"
        varchar(50) full_name
        varchar(50) name_pronunciation
        varchar(20) nickname
        varchar(100) email
        varchar(100) area
        date birth_date
        enum gender "[Male, Female, Other]"
        varchar(200) remark "備考"
        boolean is_deleted "キャンセルフラグ"
    }

    students_courses {
        Long attending_id PK "受講ID"
        Integer student_id FK "受講生ID"
        int course_id FK "コースID"
        date start_date
        date end_date
    }

    course_status {
        int id PK "受講・申し込み状況ID"
        Long attending_id FK "受講ID"
        enum status "[仮申し込み, 本申し込み, 受講中, 受講終了]"
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
```

# 使用技術

## バックエンド

- Java
- SpringBoot

## インフラ・DB

- AWS
- MySQL

# 機能一覧

|  | 機能 |
| --- | --- |
| 1 | 受講生プロフィールの登録 |
| 2 | 受講生プロフィールの更新 |
| 3 | 受講生プロフィールの削除（論理削除） |
| 4 | 受講生プロフィールの検索 |
| 5 | 受講情報の登録 |
| 6 | 受講情報の更新 |
| 7 | 受講情報の削除 |
| 8 | 受講情報の検索 |
| 9 | 受講状況（ステータス）の登録（コース情報durationから算出） |
| 10 | 受講状況（ステータス）の更新 |
| 11 | 受講状況（ステータス）の削除 |
| 12 | 受講状況（ステータス）の検索 |
| 13 | コースマスタの登録 |
| 14 | コースマスタの更新 |
| 15 | コースマスタの削除 |

### データベース
![image](https://github.com/user-attachments/assets/cbf6fd0e-a676-4b0e-baf3-bba38a00f459)

# 何ができるのか

### 受講生の詳細検索
<img width="871" alt="image" src="https://github.com/user-attachments/assets/919ebbc3-e04d-461b-97a2-ffdc5f56a14b" />

### コース情報の詳細検索
<img width="871" alt="image" src="https://github.com/user-attachments/assets/44835c39-67d6-430b-995f-2a7b95e63e88" />

### ステータス情報の詳細検索
<img width="871" alt="image" src="https://github.com/user-attachments/assets/99e95350-ece8-4645-8ac1-da872c8917fd" />

### 受講生プロフィール・コース情報・ステータス複合検索
<img width="871" alt="image" src="https://github.com/user-attachments/assets/40b3d172-797e-4a14-b7e5-5894747c460d" />

### クエリパラメータ指定なしの詳細検索（全件検索）
<img width="871" alt="image" src="https://github.com/user-attachments/assets/d601c3e3-41b0-473f-8c87-ca2ea4b6114a" />

## 工夫したところ（設計面）

1. コースをID管理・マスタテーブルを作成し、今後のコース増減（講師違い・期間違いなど）に柔軟に対応できるよう設計
2. 受講ステータスの終了時期について、コース情報から算出

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
    
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/913f32b0-a04b-4c79-8811-e414ecd86039" />
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/800045fd-2ce9-4d92-9ed9-9abcb12d8dd4" />
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/e93ab087-9dbd-413d-bb5e-1db54828d8bf" />
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/ccf34e95-23bd-4d4e-aefb-dbad5ae4b059" />
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/ddaebbe3-5bc5-4376-bf26-f5171764edef" />
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/f90d2bdf-eac4-41e0-9967-e6da2e30af21" />
    <img width="1198" alt="image" src="https://github.com/user-attachments/assets/f2f6865c-4ff5-48ea-a2f3-4b016a1cd2a5" />






