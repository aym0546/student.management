package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;

@MybatisTest
class StudentRepositoryTest {

  private final List<Student> studentList = List.of(
      new Student(1, "山田 太郎", "やまだ たろう", "たろちゃん",
          "taro.yamada@example.com", "東京", (short) 20, "Male", "スポーツが好き"),
      new Student(2, "鈴木 花子", "すずき はなこ", "はなちゃん",
          "hanako.suzuki@example.com", "大阪", (short) 22, "Female", "読書が趣味"),
      new Student(3, "佐藤 健", "さとう けん", "けんくん",
          "ken.sato@example.com", "福岡", (short) 19, "Male", ""),
      new Student(4, "高橋 美咲", "たかはし みさき", "みさみさ",
          "misaki.takahashi@example.com", "北海道", (short) 25, "Female", "旅行が好き"),
      new Student(5, "中村 俊介", "なかむら しゅんすけ", "しゅん",
          "shun.nakamura@example.com", "愛知", (short) 21, "Male", "料理が得意")
  );

  private final List<StudentsCourse> studentsCourseList = List.of(
      new StudentsCourse(1L, 1, "Javaコース",
          LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 6, 30, 0, 0)),
      new StudentsCourse(2L, 2, "デザインコース",
          LocalDateTime.of(2025, 2, 15, 0, 0), LocalDateTime.of(2025, 8, 15, 0, 0)),
      new StudentsCourse(3L, 3, "WordPressコース",
          LocalDateTime.of(2025, 3, 1, 0, 0), LocalDateTime.of(2025, 9, 1, 0, 0)),
      new StudentsCourse(4L, 4, "webマーケティングコース",
          LocalDateTime.of(2025, 4, 20, 0, 0), LocalDateTime.of(2025, 10, 20, 0, 0)),
      new StudentsCourse(5L, 5, "フロントエンドコース",
          LocalDateTime.of(2025, 5, 5, 0, 0), LocalDateTime.of(2025, 11, 5, 0, 0)),
      new StudentsCourse(6L, 1, "AWSコース",
          LocalDateTime.of(2025, 6, 1, 0, 0), LocalDateTime.of(2025, 12, 1, 0, 0)),
      new StudentsCourse(7L, 2, "映像制作コース",
          LocalDateTime.of(2025, 7, 10, 0, 0), LocalDateTime.of(2026, 1, 10, 0, 0))
  );

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の一覧表示が行えること() {
    List<Student> actual = sut.displayStudent();
    assertEquals(actual, studentList);
  }

  @Test
  void コース情報の一覧表示が行えること() {
    List<StudentsCourse> actual = sut.displayCourse();
    assertEquals(actual, studentsCourseList);
  }

  @Test
  void 受講生IDから受講生検索が行えること() {
    int studentId = 1;
    Student actual = sut.searchStudent(studentId);
    Student expected = new Student(studentId, "山田 太郎", "やまだ たろう", "たろちゃん",
        "taro.yamada@example.com", "東京", (short) 20, "Male", "スポーツが好き");
    assertEquals(actual, expected);
  }

  @Test
  void 受講生IDからコース情報検索が行えること() {
    int studentId = 1;
    List<StudentsCourse> actual = sut.searchStudentsCourses(studentId);
    List<StudentsCourse> expected = List.of(
        new StudentsCourse(1L, studentId, "Javaコース", LocalDateTime.of(2025, 1, 10, 0, 0),
            LocalDateTime.of(2025, 6, 30, 0, 0)),
        new StudentsCourse(6L, studentId, "AWSコース", LocalDateTime.of(2025, 6, 1, 0, 0),
            LocalDateTime.of(2025, 12, 1, 0, 0)));
    assertEquals(actual, expected);
  }

  @Test
  void 受講生の登録が行えること() {
    var student = new Student(null, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", (short) 25, "Female", "");
    int actual = sut.registerStudent(student);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 受講生情報の更新が行えること() {
    int studentId = 2;
    var student = new Student(studentId, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", (short) 25, "Female", "");
    int updated = sut.updateStudent(student);

    Student actual = sut.searchStudent(studentId);
    assertEquals(1, updated);  // (1) -> 更新成功
    assertEquals(actual, student);  // 前後の比較
  }

  @Test
  void コース情報の更新が行えること() {
    Long attendingId = 2L;
    int studentId = 2;
    StudentsCourse baseCourse = new StudentsCourse(attendingId, studentId, "Javaコース",
        LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 6, 30, 0, 0));

    // 実行
    int updated = sut.updateStudentsCourses(baseCourse);

    // 実行後のStudentsCourseを全件取得
    List<StudentsCourse> courseList = sut.displayCourse();

    // 該当するattendingIdのStudentsCourseをactualに設定
    Optional<StudentsCourse> actual = courseList.stream()
        .filter(studentsCourse -> studentsCourse.getAttendingId().equals(attendingId))
        .findFirst();

    assertEquals(1, updated);  // 戻り値が(1) -> 成功
    assertEquals(baseCourse.getCourse(),
        actual.map(StudentsCourse::getCourse).orElseThrow());  // コース名が更新されていることを確認
  }

}