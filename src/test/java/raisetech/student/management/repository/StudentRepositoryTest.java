package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の一覧表示が行えること() {
    List<Student> actual = sut.displayStudent();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void コース情報の一覧表示が行えること() {
    List<StudentsCourse> actual = sut.displayCourse();
    assertThat(actual.size()).isEqualTo(7);
  }

  @Test
  void 受講生IDから受講生検索が行えること() {
    int studentId = 1;
    Student actual = sut.searchStudent(studentId);
    Student expected = new Student(studentId, "山田 太郎", "やまだ たろう", "たろちゃん", "taro.yamada@example.com", "東京", (short) 20, "Male", "スポーツが好き");
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void 受講生IDからコース情報検索が行えること() {
    int studentId = 1;
    List<StudentsCourse> actual = sut.searchStudentsCourses(studentId);
    List<StudentsCourse> expected = List.of(
        new StudentsCourse(1L, studentId, "Javaコース", LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 6, 30, 0, 0)),
        new StudentsCourse(6L, studentId, "AWSコース", LocalDateTime.of(2025, 6, 1, 0, 0), LocalDateTime.of(2025, 12, 1, 0, 0))
    );
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void 受講生の登録が行えること() {
    var student = new Student(null,"テスト１子", "てすといちこ", "いちこ", "1@test.email", "テスト区", (short)25, "Female", "");
    int actual = sut.registerStudent(student);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 受講生情報の更新が行えること() {
    int studentId = 2;
    var student = new Student(studentId,"テスト１子", "てすといちこ", "いちこ", "1@test.email", "テスト区", (short)25, "Female", "");
    int updated = sut.updateStudent(student);

    Student actual = sut.searchStudent(studentId);
    assertEquals(1, updated);
    assertThat(actual).usingRecursiveComparison().isEqualTo(student);
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

    StudentsCourse actual = null;
    // 該当するattendingIdのStudentsCourseをactualに設定
    for (StudentsCourse studentsCourse : courseList)
      if (studentsCourse.getAttendingId().equals(attendingId))
        actual = studentsCourse;

    assertEquals(1, updated);  // 戻り値が(1) -> 成功
    assertEquals(baseCourse.getCourse(), Objects.requireNonNull(actual).getCourse());  // コース名が更新されていることを確認
  }

}