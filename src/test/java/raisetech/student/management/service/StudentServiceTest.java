package raisetech.student.management.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  private int studentId;
  private Student student;
  private Long attendingId1;
  private Long attendingId2;
  private StudentsCourse course1;
  private StudentsCourse course2;
  private List<StudentsCourse> courseList;
  private CourseStatus status1;
  private CourseStatus status2;
  private List<CourseStatus> statusList;
  private CourseDetail courseDetail1;
  private CourseDetail courseDetail2;
  private List<CourseDetail> courseDetailList;

  private StudentDetail studentDetail;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);

    studentId = 1;
    student = new Student(
        studentId, "ベース テスト", "べーす てすと",
        "ベース", "base@email.com", "",
        LocalDate.of(1999, 12, 31), "Other", "");

    attendingId1 = 1L;
    attendingId2 = 2L;
    course1 = new StudentsCourse(attendingId1, studentId, 1, LocalDateTime.now(),
        LocalDateTime.now().plusYears(1));
    course2 = new StudentsCourse(attendingId2, studentId, 2, LocalDateTime.now(),
        LocalDateTime.now().plusYears(1));
    courseList = List.of(course1, course2);

    status1 = new CourseStatus(1, attendingId1, Status.受講終了);
    status2 = new CourseStatus(1, attendingId2, Status.受講中);
    statusList = List.of(status1, status2);

    courseDetail1 = new CourseDetail(course1, status1);
    courseDetail2 = new CourseDetail(course2, status2);
    courseDetailList = List.of(courseDetail1, courseDetail2);

    studentDetail = new StudentDetail(student, courseDetailList);
  }

  @Test
  void 受講生一覧表示機能_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 事前準備
    // Mockのconverterの引数を用意
    List<Student> studentList = new ArrayList<>();
    List<StudentsCourse> studentCourseList = new ArrayList<>();
    List<CourseStatus> courseStatusList = new ArrayList<>();
    List<CourseDetail> courseDetailList = List.of(new CourseDetail());

    // expectedを用意
    List<StudentDetail> expected = new ArrayList<>();
    // whenのメソッドを呼び出した場合に、thenReturnを返すようMockを設定
    when(repository.findStudent(any())).thenReturn(studentList);
    when(repository.findCourse(any())).thenReturn(studentCourseList);
    when(repository.findStatus(any())).thenReturn(courseStatusList);
    when(converter.convertCourseDetails(studentCourseList, courseStatusList)).thenReturn(
        courseDetailList);
    when(converter.convertStudentDetails(studentList, courseDetailList)).thenReturn(expected);

    // 実行
    List<StudentDetail> actual = sut.getStudentList(
        new StudentSearchEntity(null, LocalDate.of(1900, 1, 1), LocalDate.of(2500, 1, 1), null,
            null, null, null, null, null, null, null, null));

    // 検証
    verify(repository, times(1)).findStudent(any());
    verify(repository, times(1)).findCourse(any());
    verify(converter, times(1)).convertCourseDetails(studentCourseList, courseStatusList);
    verify(converter, times(1)).convertStudentDetails(studentList, courseDetailList);

    // アサーション
    assertNotNull(actual);  // nullでないことを確認
    assertEquals(expected, actual);  // 期待値との一致を確認
  }

  @Test
  void 受講生詳細情報の検索機能が動作していること() {
    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(student);
    when(repository.searchStudentsCourses(studentId)).thenReturn(courseList);
    when(repository.searchCourseStatus(attendingId1)).thenReturn(status1);
    when(repository.searchCourseStatus(attendingId2)).thenReturn(status2);

    // 実行
    StudentDetail actual = sut.searchStudent(studentId);

    // 検証
    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, times(1)).searchStudentsCourses(studentId);
    verify(repository, times(1)).searchCourseStatus(attendingId1);
    verify(repository, times(1)).searchCourseStatus(attendingId2);

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertThat(actual.getCourseDetailList())
        .usingRecursiveComparison()
        .ignoringFields("studentsCourses.startDate", "studentsCourses.endDate")
        .isEqualTo(courseDetailList);
  }

  @Test
  void 受講生情報の登録機能が動作していること() {
    // Mock設定
    when(repository.registerStudent(student)).thenReturn(1);
    when(repository.registerStudentsCourses(any(StudentsCourse.class))).thenReturn(1);
    when(repository.registerCourseStatus(any(CourseStatus.class))).thenReturn(1);
    // Mockito.any()：どんなStudentsCourseが引数でもthenReturn(1)が適用される

    // 実行
    StudentDetail actual = sut.registerStudent(studentDetail);

    // 検証
    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(courseList.size())).registerStudentsCourses(any(StudentsCourse.class));
    verify(repository, times(courseList.size())).registerCourseStatus(any(CourseStatus.class));

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(courseDetailList, actual.getCourseDetailList());
  }

  @Test
  void 受講生情報の更新機能が動作していること() {
    // 事前準備
    // 更新情報 updateStudentDetail
    var updateStudent = new Student(
        studentId, "更新 テスト", "こうしん てすと",
        "てっくん", "update@email.com", "更新後",
        LocalDate.of(2000, 1, 1), "Other", "");

    var updateCourse1 = new StudentsCourse(attendingId1, studentId, 1, LocalDateTime.now(),
        LocalDateTime.now().plusYears(1));
    var updateCourse2 = new StudentsCourse(attendingId2, studentId, 2, LocalDateTime.now(),
        LocalDateTime.now().plusYears(1));
    var updateCourses = List.of(updateCourse1, updateCourse2);

    var updateStatus1 = new CourseStatus(1, attendingId1, Status.受講終了);
    var updateStatus2 = new CourseStatus(2, attendingId2, Status.受講終了);
    var updateStatuses = List.of(updateStatus1, updateStatus2);

    var updateCourseDetails = List.of(
        new CourseDetail(updateCourse1, updateStatus1),
        new CourseDetail(updateCourse2, updateStatus2)
    );

    var updateStudentDetail = new StudentDetail(updateStudent, updateCourseDetails);

    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(
        updateStudent);  // studentId(1) -> updateStudent
    when(repository.searchStudentsCourses(studentId)).thenReturn(
        updateCourses);  // studentId(1) -> updateCourses
    when(repository.searchCourseStatus(attendingId1)).thenReturn(
        updateStatus1);  // attendingId(1L) -> updateStatus1
    when(repository.searchCourseStatus(attendingId2)).thenReturn(
        updateStatus2);  // attendingId(2L) -> updateStatus2
    when(repository.updateStudent(updateStudent)).thenReturn(1);  // updateStudent -> 更新が成功する
    when(repository.updateStudentsCourses(any(StudentsCourse.class))).thenReturn(
        1);  // any() -> 更新が成功する
    when(repository.updateCourseStatus(any(CourseStatus.class))).thenReturn(1);  // any() -> 更新が成功する

    // 実行
    sut.updateStudent(updateStudentDetail);
    StudentDetail actual = sut.searchStudent(studentId);  // 実測値

    // 検証
    // メソッド呼び出し確認
    verify(repository, times(1)).updateStudent(updateStudent);
    verify(repository, times(updateCourses.size()))
        .updateStudentsCourses(any(StudentsCourse.class));
    verify(repository, times(updateStatuses.size()))
        .updateCourseStatus(any(CourseStatus.class));
    // 更新前後の比較（結果が行進情報と一致/結果が更新対象と不一致）
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("studentsCourses.startDate", "studentsCourses.endDate")
        .isEqualTo(updateStudentDetail)  // 期待値との比較
        .isNotEqualTo(studentDetail);
  }
}
