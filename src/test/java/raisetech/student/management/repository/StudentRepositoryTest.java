package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;

@MybatisTest
class StudentRepositoryTest {

  private final List<Student> studentList = List.of(
      new Student(1, "山田 太郎", "やまだ たろう", "たろちゃん",
          "taro.yamada@example.com", "東京", LocalDate.of(1990, 5, 12), "Male", "スポーツが好き"),
      new Student(2, "鈴木 花子", "すずき はなこ", "はなちゃん",
          "hanako.suzuki@example.com", "大阪", LocalDate.of(1995, 10, 25), "Female", "読書が趣味"),
      new Student(3, "佐藤 健", "さとう けん", "けんくん",
          "ken.sato@example.com", "福岡", LocalDate.of(2005, 9, 12), "Male", ""),
      new Student(4, "高橋 美咲", "たかはし みさき", "みさみさ",
          "misaki.takahashi@example.com", "北海道", LocalDate.of(2000, 6, 30), "Female",
          "旅行が好き"),
      new Student(5, "中村 俊介", "なかむら しゅんすけ", "しゅん",
          "shun.nakamura@example.com", "愛知", LocalDate.of(2010, 3, 7), "Male", "料理が得意")
  );

  private final List<StudentsCourse> studentsCourseList = List.of(
      new StudentsCourse(1L, 1, 1),
      new StudentsCourse(2L, 2, 2),
      new StudentsCourse(3L, 3, 3),
      new StudentsCourse(4L, 4, 4),
      new StudentsCourse(5L, 5, 5),
      new StudentsCourse(6L, 3, 6),
      new StudentsCourse(7L, 1, 7),
      new StudentsCourse(8L, 4, 5)
  );

  private final List<StudentsCoursesStatus> studentsCoursesStatusList = List.of(
      // 山田 太郎 (Javaコース)
      new StudentsCoursesStatus(1, 1L, 1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10),
          "仮申し込み完了"),
      new StudentsCoursesStatus(2, 1L, 2, LocalDate.of(2024, 1, 11), LocalDate.of(2024, 1, 15),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(3, 1L, 3, LocalDate.of(2024, 2, 1), null, "受講開始"),

      // 山田 太郎 (フロントエンドコース)
      new StudentsCoursesStatus(4, 7L, 1, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 5),
          "仮申し込み完了"),
      new StudentsCoursesStatus(5, 7L, 2, LocalDate.of(2024, 3, 6), LocalDate.of(2024, 3, 10),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(6, 7L, 3, LocalDate.of(2024, 3, 15), null, "受講開始"),

      // 鈴木 花子 (AWSコース)
      new StudentsCoursesStatus(7, 2L, 1, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10),
          "仮申し込み完了"),
      new StudentsCoursesStatus(8, 2L, 2, LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 20),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(9, 2L, 3, LocalDate.of(2024, 3, 1), null, "受講開始"),

      // 佐藤 健 (WordPressコース)
      new StudentsCoursesStatus(10, 3L, 1, LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5),
          "仮申し込み完了"),
      new StudentsCoursesStatus(11, 3L, 2, LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 10),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(12, 3L, 3, LocalDate.of(2024, 4, 15), LocalDate.of(2024, 7, 15),
          "受講開始"),
      new StudentsCoursesStatus(13, 3L, 4, LocalDate.of(2024, 7, 16), null, "受講修了"),

      // 佐藤 健 (映像制作コース)
      new StudentsCoursesStatus(14, 6L, 1, LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 5),
          "仮申し込み完了"),
      new StudentsCoursesStatus(15, 6L, 2, LocalDate.of(2024, 8, 6), LocalDate.of(2024, 8, 10),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(16, 6L, 3, LocalDate.of(2024, 8, 15), null, "受講開始"),

      // 高橋 美咲 (デザインコース)
      new StudentsCoursesStatus(17, 4L, 1, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 10),
          "仮申し込み完了"),
      new StudentsCoursesStatus(18, 4L, 2, LocalDate.of(2024, 5, 11), LocalDate.of(2024, 5, 20),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(19, 4L, 3, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 9, 1),
          "受講開始"),
      new StudentsCoursesStatus(20, 4L, 5, LocalDate.of(2024, 9, 2), null, "一時中断"),

      // 高橋 美咲 (webマーケティングコース)
      new StudentsCoursesStatus(21, 8L, 1, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 10),
          "仮申し込み完了"),
      new StudentsCoursesStatus(22, 8L, 2, LocalDate.of(2024, 6, 11), LocalDate.of(2024, 6, 20),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(23, 8L, 3, LocalDate.of(2024, 7, 1), null, "受講開始"),

      // 中村 俊介 (映像制作コース)
      new StudentsCoursesStatus(24, 5L, 1, LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 10),
          "仮申し込み完了"),
      new StudentsCoursesStatus(25, 5L, 2, LocalDate.of(2024, 7, 11), LocalDate.of(2024, 7, 20),
          "本申し込みへ移行"),
      new StudentsCoursesStatus(26, 5L, 3, LocalDate.of(2024, 8, 1), null, "受講開始"),
      new StudentsCoursesStatus(27, 5L, 6, LocalDate.of(2025, 2, 1), null, "コース延長")
  );

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の一覧表示が行えること() {
    List<Student> actual = sut.displayStudent();
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(studentList);
  }

  @Test
  void コース情報の一覧表示が行えること() {
    List<StudentsCourse> actual = sut.displayCourse();
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(studentsCourseList);
  }

  @Test
  void 受講ステータス情報の一覧表示が行えること() {
    List<StudentsCoursesStatus> actual = sut.displayStatus();
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(studentsCoursesStatusList);
  }

  @Test
  void 受講生IDから受講生検索が行えること() {
    int studentId = 1;
    Student actual = sut.searchStudent(studentId);
    Student expected = new Student(studentId, "山田 太郎", "やまだ たろう", "たろちゃん",
        "taro.yamada@example.com", "東京", LocalDate.of(1990, 5, 12), "Male", "スポーツが好き");
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected);
  }

  @Test
  void 受講生IDからコース情報検索が行えること() {
    int studentId = 1;
    List<StudentsCourse> actual = sut.searchStudentsCourses(studentId);
    List<StudentsCourse> expected = List.of(
        new StudentsCourse(1L, studentId, 1),
        new StudentsCourse(7L, studentId, 7));
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected);
  }

  @Test
  void コース受講IDから受講ステータス検索が行えること() {
    Long attendingId = 1L;
    List<StudentsCoursesStatus> actual = sut.searchStatuses(attendingId);
    List<StudentsCoursesStatus> expected = List.of(
        new StudentsCoursesStatus(1, 1L, 1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10),
            "仮申し込み完了"),
        new StudentsCoursesStatus(2, 1L, 2, LocalDate.of(2024, 1, 11), LocalDate.of(2024, 1, 15),
            "本申し込みへ移行"),
        new StudentsCoursesStatus(3, 1L, 3, LocalDate.of(2024, 2, 1), null, "受講開始")
    );
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected);
  }

  @Test
  void 受講生の登録が行えること() {
    var student = new Student(null, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", LocalDate.of(2000, 1, 1), "Female", "");
    int actual = sut.registerStudent(student);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void コース情報の登録が行えること() {
    // 登録済みのstudentIdに対してcourseIdを登録する attendingIdは自動採番
    StudentsCourse studentsCourse = new StudentsCourse(null, 1, 2);
    int actual = sut.registerStudentsCourses(studentsCourse);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 受講ステータス情報の登録が行えること() {
    // 登録済みのattendingIdに対してstatusを登録する statusHistoryIdは自動採番
    StudentsCoursesStatus studentsCoursesStatus = new StudentsCoursesStatus(null, 1L, 1,
        LocalDate.of(2000, 1, 1), null, "新規申し込み");
    int actual = sut.registerStudentsCoursesStatus(studentsCoursesStatus);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 受講生情報の更新が行えること() {
    int studentId = 2;
    var student = new Student(studentId, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", LocalDate.of(2000, 12, 31), "Female", "");
    int updated = sut.updateStudent(student);

    Student actual = sut.searchStudent(studentId);
    assertEquals(1, updated);  // (1) -> 更新成功
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(student);  // 前後の比較
  }

  @Test
  void コース情報の更新が行えること() {
    Long attendingId = 2L;
    int studentId = 2;
    StudentsCourse baseCourse = new StudentsCourse(attendingId, studentId, 1);

    // 実行
    int updated = sut.updateStudentsCourses(baseCourse);

    // 実行後のStudentsCourseを全件取得
    List<StudentsCourse> courseList = sut.displayCourse();

    // 該当するattendingIdのStudentsCourseをactualに設定
    Optional<StudentsCourse> actual = courseList.stream()
        .filter(studentsCourse -> studentsCourse.getAttendingId().equals(attendingId))
        .findFirst();

    assertEquals(1, updated);  // 戻り値が(1) -> 成功
    assertThat(baseCourse.getCourseId())
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(
            actual.map(StudentsCourse::getCourseId).orElseThrow());  // コースIDが更新されていることを確認
  }

  @Test
  void 受講ステータス情報の更新が行えること() {
    int statusHistoryId = 3;
    Long attendingId = 1L;
    var coursesStatus = new StudentsCoursesStatus(statusHistoryId, attendingId, 3,
        LocalDate.of(2024, 2, 22), null, "開始日変更");

    int updated = sut.updateStudentsCoursesStatus(coursesStatus);

    var statuses = sut.searchStatuses(attendingId);
    Optional<StudentsCoursesStatus> actual = statuses.stream()
        .filter(status -> status.getStatusHistoryId().equals(statusHistoryId))
        .findFirst();

    assertEquals(1, updated);
    assertThat(coursesStatus)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(actual.orElseThrow());
  }

  @Test
  void 既存の未終了ステータスの更新が行えること() {
    Long attendingId = 1L;
    LocalDate statusStartDate = LocalDate.of(2024, 5, 1);

    int updateEndDate = sut.updateStatusEndDateForH2(attendingId, statusStartDate);

    var actual = sut.searchStatuses(attendingId);

    var expected = List.of(
        new StudentsCoursesStatus(1, 1L, 1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 10),
            "仮申し込み完了"),
        new StudentsCoursesStatus(2, 1L, 2, LocalDate.of(2024, 1, 11), LocalDate.of(2024, 1, 15),
            "本申し込みへ移行"),
        new StudentsCoursesStatus(3, 1L, 3, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 4, 30),
            "受講開始")
    );

    assertEquals(1, updateEndDate);
    assertThat(expected)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(actual);

  }

}