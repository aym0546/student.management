package raisetech.student.management.service.student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.converter.student.StudentConverter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.dto.StudentSearchDTO;
import raisetech.student.management.exception.NoDataException;
import raisetech.student.management.exception.ProcessFailedException;
import raisetech.student.management.repository.course.CourseRepository;
import raisetech.student.management.repository.student.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private StudentConverter converter;

  @Mock
  private CourseRepository courseRepository;

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
  private CourseDetail courseDetail1;
  private CourseDetail courseDetail2;
  private List<CourseDetail> courseDetailList;

  private StudentDetail studentDetail;

  @BeforeEach
  void before() {
    sut = new StudentService(studentRepository, converter, courseRepository);

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

    status1 = new CourseStatus(1, attendingId1, 5);
    status2 = new CourseStatus(1, attendingId2, 4);

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
    when(studentRepository.findStudent(any())).thenReturn(studentList);
    when(studentRepository.findCourse(any())).thenReturn(studentCourseList);
    when(studentRepository.findStatus(any())).thenReturn(courseStatusList);
    when(converter.convertCourseDetails(studentCourseList, courseStatusList)).thenReturn(
        courseDetailList);
    when(converter.convertStudentDetails(studentList, courseDetailList)).thenReturn(expected);

    // 実行
    var actual = sut.getStudentList(
        new StudentSearchDTO(null, LocalDate.of(1900, 1, 1), LocalDate.of(2500, 1, 1), null,
            null, null, null, null, null, null, null, null));

    // 検証
    verify(studentRepository, times(1)).findStudent(any());
    verify(studentRepository, times(1)).findCourse(any());
    verify(converter, times(1)).convertCourseDetails(studentCourseList, courseStatusList);
    verify(converter, times(1)).convertStudentDetails(studentList, courseDetailList);

    // アサーション
    assertNotNull(actual);  // nullでないことを確認
    assertEquals(expected, actual);  // 期待値との一致を確認
  }

  @Test
  void 受講生詳細情報の検索機能_リポジトリを適切に呼び出し結果が返ること() {
    // Mock設定
    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(courseList);
    when(studentRepository.searchCourseStatus(attendingId1)).thenReturn(status1);
    when(studentRepository.searchCourseStatus(attendingId2)).thenReturn(status2);

    // 実行
    var actual = sut.searchStudent(studentId);

    // 検証
    verify(studentRepository, times(1)).searchStudent(studentId);
    verify(studentRepository, times(1)).searchStudentsCourses(studentId);
    verify(studentRepository, times(1)).searchCourseStatus(attendingId1);
    verify(studentRepository, times(1)).searchCourseStatus(attendingId2);

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertThat(actual.getCourseDetailList())
        .usingRecursiveComparison()
        .ignoringFields("studentsCourses.startDate", "studentsCourses.endDate")
        .isEqualTo(courseDetailList);
  }

  @Test
  void 受講生詳細情報の検索機能_存在しない受講生を検索すると例外をスローすること() {
    // searchStudentメソッドにnullを返す
    when(studentRepository.searchStudent(studentId)).thenReturn(null);

    // 実行時に例外をthrowすることを検証
    assertThrows(NoDataException.class, () -> sut.searchStudent(studentId));
  }

  @Test
  void 受講生詳細情報の検索機能_受講生が存在するがコース情報がない場合_空リストを返すこと() {
    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(null);

    var actual = sut.searchStudent(studentId);

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertTrue(actual.getCourseDetailList().isEmpty());
  }

  @Test
  void 受講生詳細情報の検索機能_受講コースが複数の場合_正しくリストされること() {
    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(courseList);
    when(studentRepository.searchCourseStatus(attendingId1)).thenReturn(status1);
    when(studentRepository.searchCourseStatus(attendingId2)).thenReturn(status2);

    var actual = sut.searchStudent(studentId);

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(2, actual.getCourseDetailList().size());
    assertEquals(status1, actual.getCourseDetailList().get(0).getStatus());
    assertEquals(status2, actual.getCourseDetailList().get(1).getStatus());
  }

  @Test
  void 受講生情報の登録機能が動作し_受講生情報_コース情報_ステータス情報すべての登録に成功すること() {
    // Mock設定
    when(studentRepository.registerStudent(student)).thenReturn(1);
    when(studentRepository.registerStudentsCourses(any(StudentsCourse.class))).thenReturn(1);
    when(studentRepository.registerCourseStatus(any(CourseStatus.class))).thenReturn(1);
    // Mockito.any()：どんなStudentsCourseが引数でもthenReturn(1)が適用される

    // 実行
    StudentDetail actual = sut.registerStudent(studentDetail);

    // 検証
    verify(studentRepository, times(1)).registerStudent(student);
    verify(studentRepository, times(courseList.size())).registerStudentsCourses(
        any(StudentsCourse.class));
    verify(studentRepository, times(courseList.size())).registerCourseStatus(
        any(CourseStatus.class));

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(courseDetailList, actual.getCourseDetailList());
  }

  @Test
  void 受講生情報の登録機能_受講生情報の登録に失敗するとProcessFailedExceptionをスローすること() {
    // registerStudentが失敗（0を返す）
    when(studentRepository.registerStudent(student)).thenReturn(0);

    // 例外がthrowされることを確認
    assertThrows(ProcessFailedException.class, () -> sut.registerStudent(studentDetail));

    // 以降の処理に進まないことを確認
    verify(studentRepository, never()).registerStudentsCourses(any());
    verify(studentRepository, never()).registerCourseStatus(any());
  }

  @Test
  void 受講生情報の登録機能_コース情報の登録に失敗するとProcessFailedExceptionをスローすること() {
    // registerStudentは成功（1を返す）し、registerStudentsCourseが失敗（0を返す）
    when(studentRepository.registerStudent(student)).thenReturn(1);
    when(studentRepository.registerStudentsCourses(course1)).thenReturn(0);

    // 例外がthrowされることを確認
    assertThrows(ProcessFailedException.class, () -> sut.registerStudent(studentDetail));

    // 以降の処理に進まないことを確認
    verify(studentRepository, never()).registerCourseStatus(any());
  }

  @Test
  void 受講生情報の登録機能_ステータス情報の登録に失敗するとProcessFailedExceptionをスローすること() {
    // registerStudent・registerStudentCourseは成功、registerCourseStatusが失敗
    when(studentRepository.registerStudent(student)).thenReturn(1);
    when(studentRepository.registerStudentsCourses(course1)).thenReturn(1);
    when(studentRepository.registerCourseStatus(status1)).thenReturn(0);

    // 例外がthrowされることを確認
    assertThrows(ProcessFailedException.class, () -> sut.registerStudent(studentDetail));
  }

  @Test
  void 受講生情報の更新機能_受講生情報_コース情報_ステータス情報すべての更新に成功すること() {
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

    var updateStatus1 = new CourseStatus(1, attendingId1, 5);
    var updateStatus2 = new CourseStatus(2, attendingId2, 5);
    var updateStatuses = List.of(updateStatus1, updateStatus2);

    var updateCourseDetails = List.of(
        new CourseDetail(updateCourse1, updateStatus1),
        new CourseDetail(updateCourse2, updateStatus2)
    );

    var updateStudentDetail = new StudentDetail(updateStudent, updateCourseDetails);

    // Mock設定
    when(studentRepository.searchStudent(studentId)).thenReturn(
        updateStudent);  // studentId(1) -> updateStudent
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(
        updateCourses);  // studentId(1) -> updateCourses
    when(studentRepository.searchCourseStatus(attendingId1)).thenReturn(
        updateStatus1);  // attendingId(1L) -> updateStatus1
    when(studentRepository.searchCourseStatus(attendingId2)).thenReturn(
        updateStatus2);  // attendingId(2L) -> updateStatus2
    when(studentRepository.updateStudent(updateStudent)).thenReturn(1);  // updateStudent -> 更新が成功する
    when(studentRepository.updateStudentsCourses(any(StudentsCourse.class))).thenReturn(
        1);  // any() -> 更新が成功する
    when(studentRepository.updateCourseStatus(any(CourseStatus.class))).thenReturn(
        1);  // any() -> 更新が成功する

    // 実行
    sut.updateStudent(studentId, updateStudentDetail);
    StudentDetail actual = sut.searchStudent(studentId);  // 実測値

    // 検証
    // メソッド呼び出し確認
    verify(studentRepository, times(1)).updateStudent(updateStudent);
    verify(studentRepository, times(updateCourses.size()))
        .updateStudentsCourses(any(StudentsCourse.class));
    verify(studentRepository, times(updateStatuses.size()))
        .updateCourseStatus(any(CourseStatus.class));
    // 更新前後の比較（結果が行進情報と一致/結果が更新対象と不一致）
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("studentsCourses.startDate", "studentsCourses.endDate")
        .isEqualTo(updateStudentDetail)  // 期待値との比較
        .isNotEqualTo(studentDetail);
  }

  @Test
  void 受講生情報の更新機能_該当する受講生がいない場合にNoDataExceptionをスローすること() {
    when(studentRepository.searchStudent(studentId)).thenReturn(null);

    assertThrows(NoDataException.class, () -> sut.updateStudent(studentId, studentDetail));
  }

  @Test
  void 受講生情報の更新機能_該当するコースがない場合にNoDataExceptionをスローすること() {
    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(List.of());  // 空リストが返ってくる

    assertThrows(NoDataException.class, () -> sut.updateStudent(studentId, studentDetail));
  }

  @Test
  void 受講生情報の更新機能_該当するステータスがない場合にNoDataExceptionをスローすること() {
    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(courseList);
    when(studentRepository.searchCourseStatus(attendingId1)).thenReturn(null);
    when(studentRepository.searchCourseStatus(attendingId2)).thenReturn(null);

    assertThrows(NoDataException.class, () -> sut.updateStudent(studentId, studentDetail));
  }

  @Test
  void 受講生情報の更新機能_すべての更新結果が0件の場合にProcessFailedExceptionをスローすること() {
    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.searchStudentsCourses(studentId)).thenReturn(courseList);
    when(studentRepository.searchCourseStatus(attendingId1)).thenReturn(status1);
    when(studentRepository.searchCourseStatus(attendingId2)).thenReturn(status2);

    when(studentRepository.updateStudent(any(Student.class))).thenReturn(0);
    when(studentRepository.updateStudentsCourses(any(StudentsCourse.class))).thenReturn(0);
    when(studentRepository.updateCourseStatus(any(CourseStatus.class))).thenReturn(0);

    assertThrows(ProcessFailedException.class, () -> sut.updateStudent(studentId, studentDetail));
  }

  @Test
  void 受講生情報の論理削除_部分更新処理が成功すること() {
    student = new Student();
    student.setStudentId(1);
    var isDeleted = true;

    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.updateStudent(any(Student.class))).thenReturn(1);

    assertDoesNotThrow(() -> sut.updateStudentIsDeleted(studentId, isDeleted));
    verify(studentRepository).updateStudent(any(Student.class));
  }

  @Test
  void 受講生情報の論理削除_該当IDが存在しない時_NoDataExceptionをスローすること() {
    studentId = 999;
    when(studentRepository.searchStudent(studentId)).thenReturn(null);

    var ex = assertThrows(NoDataException.class,
        () -> sut.updateStudentIsDeleted(studentId, true));

    assertTrue(ex.getMessage().contains("更新対象の受講生情報が見つかりません。"));
  }

  @Test
  void 受講生情報の論理削除_更新件数0の時_ProcessFailedExceptionをスローすること() {
    studentId = 1;
    Boolean isDeleted = true;
    student = new Student();
    student.setStudentId(studentId);

    when(studentRepository.searchStudent(studentId)).thenReturn(student);
    when(studentRepository.updateStudent(any(Student.class))).thenReturn(0);

    var ex = assertThrows(ProcessFailedException.class,
        () -> sut.updateStudentIsDeleted(studentId, isDeleted));
    assertEquals("更新が反映されませんでした", ex.getMessage());
  }
}
