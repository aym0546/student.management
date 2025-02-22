package raisetech.student.management.controller.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;

class StudentConverterTest {

  StudentConverter sut = new StudentConverter();

  // 日時固定
  LocalDateTime fixedDateTime = LocalDateTime.of(2021, 5, 7, 16, 0, 0);

  @BeforeEach
  void before() {
  }

  @Test
  void 受講生リストと受講コース情報のリストから受講生詳細リストへの変換が正しく行われること() {
    // 事前準備
    // 受講生データ
    int studentId1 = 999;
    int studentId2 = 888;
    var student1 = new Student(studentId1, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区",
        LocalDate.of(2000, 1, 20), "Female", "");
    var student2 = new Student(studentId2, "テスト２斗", "てすとにと", "にと", "2@test.email",
        "テスト区", LocalDate.of(1989, 3, 31), "Male", "");
    List<Student> studentList = List.of(student1, student2);
    // 受講コースデータ
    var course1 = new StudentsCourse(111L, studentId1, 1);
    var course2 = new StudentsCourse(222L, studentId2, 2);
    var course3 = new StudentsCourse(333L, studentId1, 3);
    List<StudentsCourse> courseList = List.of(course1, course2, course3);
    // 受講ステータスデータ
    var status1_1 = new StudentsCoursesStatus(1, 111L, 1, LocalDate.of(2025, 1, 1), null,
        "新規申し込み");
    var status2_1 = new StudentsCoursesStatus(2, 222L, 1, LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 1, 31), "新規申し込み");
    var status2_2 = new StudentsCoursesStatus(3, 222L, 2, LocalDate.of(2025, 2, 1), null,
        "面談済み");
    var status3_1 = new StudentsCoursesStatus(4, 333L, 1, LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 1, 31), "新規申し込み");
    var status3_2 = new StudentsCoursesStatus(5, 333L, 2, LocalDate.of(2025, 2, 1),
        LocalDate.of(2025, 2, 28), "面談済み");
    var status3_3 = new StudentsCoursesStatus(6, 333L, 3, LocalDate.of(2025, 3, 1), null,
        "受講開始日決定");
    List<StudentsCoursesStatus> statuseList = List.of(status1_1, status2_1, status2_2, status3_1,
        status3_2, status3_3);

    // expectedを用意
    var courseDetail1 = new CourseDetail(course1, List.of(status1_1));
    var courseDetail2 = new CourseDetail(course2, List.of(status2_1, status2_2));
    var courseDetail3 = new CourseDetail(course3, List.of(status3_1, status3_2, status3_3));
    var studentDetail1 = new StudentDetail(student1, List.of(courseDetail1, courseDetail3));
    var studentDetail2 = new StudentDetail(student2, List.of(courseDetail2));
    List<StudentDetail> expected = List.of(studentDetail1, studentDetail2);

    // 実行
    List<CourseDetail> courseDetailList = sut.convertCourseDetails(courseList, statuseList);
    List<StudentDetail> actual = sut.convertStudentDetails(studentList, courseDetailList);

    // 検証
    assertThat(actual)
        .usingRecursiveComparison()  // 入れ子の要素も評価
        .isEqualTo(expected);

  }

}