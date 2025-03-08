package raisetech.student.management.controller.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;

class StudentConverterTest {

  StudentConverter sut = new StudentConverter();

  // 日時固定
  LocalDateTime fixedDateTime = LocalDateTime.of(2021, 5, 7, 16, 0, 0);

  @Test
  void 受講生リストと受講コース情報のリストから受講生詳細リストへの変換が正しく行われること() {

    // 事前準備　* テストデータ作成 *
    int studentId1 = 999;
    int studentId2 = 888;

    var student1 = new Student(studentId1, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", LocalDate.of(2000, 1, 11), "Female", "");
    var student2 = new Student(studentId2, "テスト２斗", "てすとにと", "にと", "2@test.email",
        "テスト区", LocalDate.of(2000, 2, 22), "Male", "");
    List<Student> studentList = List.of(student1, student2);

    var course1 = new StudentsCourse(111L, studentId1, 1, fixedDateTime,
        fixedDateTime.plusYears(1));
    var course2 = new StudentsCourse(222L, studentId2, 2, fixedDateTime,
        fixedDateTime.plusYears(1));
    var course3 = new StudentsCourse(333L, studentId1, 8, fixedDateTime,
        fixedDateTime.plusYears(1));
    List<StudentsCourse> studentsCourseList = List.of(course1, course2, course3);

    var status1 = new CourseStatus(1, 111L, Status.受講中);
    var status2 = new CourseStatus(2, 222L, Status.本申し込み);
    var status3 = new CourseStatus(3, 333L, Status.仮申し込み);
    List<CourseStatus> courseStatusList = List.of(status1, status2, status3);

    List<CourseDetail> courseDetailList = List.of(
        new CourseDetail(course1, status1),
        new CourseDetail(course2, status2),
        new CourseDetail(course3, status3)
    );

    // expectedを用意
    var courseDetail1 = new CourseDetail(course1, status1);
    var courseDetail2 = new CourseDetail(course2, status2);
    var courseDetail3 = new CourseDetail(course3, status3);
    var studentDetail1 = new StudentDetail(student1, List.of(courseDetail1, courseDetail3));
    var studentDetail2 = new StudentDetail(student2, List.of(courseDetail2));
    List<StudentDetail> expected = List.of(studentDetail1, studentDetail2);

    // 実行
    List<StudentDetail> actual = sut.convertStudentDetails(studentList, courseDetailList);

    // 検証
    assertThat(actual)
        .usingRecursiveComparison()  // 入れ子の要素も評価
        .isEqualTo(expected);

  }

  @Test
  void コース情報と受講ステータスのリストから受講生詳細リストへの変換が正しく行われること() {
    // 事前準備　* テストデータ作成 *
    int studentId1 = 999;
    int studentId2 = 888;

    Long attendingId1 = 111L;
    Long attendingId2 = 222L;
    Long attendingId3 = 333L;

    var course1 = new StudentsCourse(attendingId1, studentId1, 1, fixedDateTime,
        fixedDateTime.plusYears(1));
    var course2 = new StudentsCourse(attendingId2, studentId2, 2, fixedDateTime,
        fixedDateTime.plusYears(1));
    var course3 = new StudentsCourse(attendingId3, studentId1, 8, fixedDateTime,
        fixedDateTime.plusYears(1));
    List<StudentsCourse> studentsCourseList = List.of(course1, course2, course3);

    var status1 = new CourseStatus(1, attendingId1, Status.受講中);
    var status2 = new CourseStatus(2, attendingId2, Status.本申し込み);
    var status3 = new CourseStatus(3, attendingId3, Status.仮申し込み);
    List<CourseStatus> courseStatusList = List.of(status1, status2, status3);

    // expectedを用意
    var courseDetail1 = new CourseDetail(course1, status1);
    var courseDetail2 = new CourseDetail(course2, status2);
    var courseDetail3 = new CourseDetail(course3, status3);
    var expected = List.of(courseDetail1, courseDetail2, courseDetail3);

    // 実行
    List<CourseDetail> actual = sut.convertCourseDetails(studentsCourseList, courseStatusList);

    // 検証
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
    
  }

}
