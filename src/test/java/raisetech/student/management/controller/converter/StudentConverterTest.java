package raisetech.student.management.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;

class StudentConverterTest {

  StudentConverter sut = new StudentConverter();

  // 日時固定
  LocalDateTime fixedDateTime = LocalDateTime.of(2021, 5, 7, 16, 0, 0);

  @Test
  void 受講生リストと受講コース情報のリストから受講生詳細リストへの変換が正しく行われること() {

    // 事前準備
    int studentId1 = 999;
    int studentId2 = 888;
    var student1 = new Student(studentId1, "テスト１子", "てすといちこ", "いちこ", "1@test.email", "テスト区", (short)25, "Female", "");
    var student2 = new Student(studentId2, "テスト２斗", "てすとにと", "にと", "2@test.email", "テスト区", (short)22, "Male", "");
    List<Student> studentList = List.of(student1, student2);
    var course1 = new StudentsCourse(111L, studentId1, "Javaコース", fixedDateTime, fixedDateTime.plusYears(1));
    var course2 = new StudentsCourse(222L, studentId2, "AWSコース", fixedDateTime, fixedDateTime.plusYears(1));
    var course3 = new StudentsCourse(333L, studentId1, "フロントエンドコース", fixedDateTime, fixedDateTime.plusYears(1));
    List<StudentsCourse> studentsCourseList = List.of(course1, course2, course3);

    // expectedを用意
    var studentDetail1 = new StudentDetail(student1, List.of(course1, course3));
    var studentDetail2 = new StudentDetail(student2, List.of(course2));
    List<StudentDetail> expected = List.of(studentDetail1, studentDetail2);

    // 実行
    List<StudentDetail> actual = sut.convertStudentDetails(studentList, studentsCourseList);

    // 検証
    assertThat(actual)
        .usingRecursiveComparison()  // 入れ子の要素も評価
        .isEqualTo(expected);

  }

}