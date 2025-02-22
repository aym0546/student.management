package raisetech.student.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.exception.NoDataException;
import raisetech.student.management.exception.ProcessFailedException;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  private int studentId;
  private Student student1;
  private StudentsCourse course1;
  private StudentsCourse course2;
  private StudentsCoursesStatus status1;
  private StudentsCoursesStatus status2;
  private StudentsCoursesStatus status3;
  private List<StudentsCourse> studentsCourses;
  private List<StudentsCoursesStatus> statuses;
  private List<CourseDetail> courseDetails;
  private StudentDetail studentDetail;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);

    studentId = 123;
    student1 = new Student(
        studentId, "ベース テスト", "べーす てすと",
        "ベース", "base@email.com", "",
        LocalDate.of(2004, 12, 1), "Other", "");

    course1 = new StudentsCourse(1L, studentId, 1);
    course2 = new StudentsCourse(2L, studentId, 2);

    status1 = new StudentsCoursesStatus(1, 1L, 1, LocalDate.of(2000, 1, 1), null, "新規申し込み");
    status2 = new StudentsCoursesStatus(2, 2L, 1, LocalDate.of(2000, 1, 1),
        LocalDate.of(2000, 1, 1), "新規申し込み");
    status3 = new StudentsCoursesStatus(3, 2L, 2, LocalDate.of(2000, 1, 2), null, "入金確認");

    studentsCourses = List.of(course1, course2);
    statuses = List.of(status1, status2, status3);
    courseDetails = List.of(
        new CourseDetail(course1, List.of(status1)),
        new CourseDetail(course2, List.of(status2, status3))
    );

    studentDetail = new StudentDetail(student1, courseDetails);
  }

  @Test
  void 受講生一覧表示機能_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 事前準備
    // Mockのconverterの引数を用意
    List<Student> studentList = new ArrayList<>();
    List<StudentsCourse> studentCourseList = new ArrayList<>();
    List<StudentsCoursesStatus> studentsCoursesStatusList = new ArrayList<>();
    List<CourseDetail> courseDetailList = new ArrayList<>();
    // expectedを用意
    List<StudentDetail> expected = new ArrayList<>();
    // whenのメソッドを呼び出した場合に、thenReturnを返すようMockを設定
    when(repository.displayStudent()).thenReturn(studentList);
    when(repository.displayCourse()).thenReturn(studentCourseList);
    when(repository.displayStatus()).thenReturn(studentsCoursesStatusList);
    when(converter.convertCourseDetails(studentCourseList, studentsCoursesStatusList))
        .thenReturn(courseDetailList);
    when(converter.convertStudentDetails(studentList, courseDetailList)).thenReturn(expected);

    // 実行
    List<StudentDetail> actual = sut.getStudentList();

    // 検証
    verify(repository, times(1)).displayStudent();
    verify(repository, times(1)).displayCourse();
    verify(repository, times(1)).displayStatus();
    verify(converter, times(1)).convertCourseDetails(studentCourseList, studentsCoursesStatusList);
    verify(converter, times(1)).convertStudentDetails(studentList, courseDetailList);

    // アサーション
    assertNotNull(actual);  // nullでないことを確認
    assertEquals(expected, actual);  // 期待値との一致を確認
  }

  @Test
  void 受講生詳細情報の検索機能_受講生が存在しコース情報とステータス情報が正しく紐づけられること() {
    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(student1);
    when(repository.searchStudentsCourses(studentId)).thenReturn(studentsCourses);
    when(repository.searchStatuses(course1.getAttendingId())).thenReturn(List.of(status1));
    when(repository.searchStatuses(course2.getAttendingId())).thenReturn(List.of(status2, status3));
    when(converter.convertCourseDetails(studentsCourses, statuses)).thenReturn(courseDetails);

    // 実行
    StudentDetail actual = sut.searchStudent(studentId);

    // モックの呼び出し確認
    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, times(1)).searchStudentsCourses(studentId);
    verify(repository, times(1)).searchStatuses(course1.getAttendingId());
    verify(repository, times(1)).searchStatuses(course2.getAttendingId());
    verify(converter, times(1)).convertCourseDetails(studentsCourses, statuses);

    // 戻り値となるstudent1とcourseDetailsについて検証
    assertNotNull(actual);
    assertEquals(student1, actual.getStudent());
    assertThat(actual.getCourseDetails())
        .usingRecursiveComparison().ignoringFields("createdAt", "updatedAt")
        .isEqualTo(courseDetails);
  }

  @Test
  void 受講生詳細情報の検索機能_受講生が見つからない場合にNoDataExceptionが発生すること() {
    // モック設定（該当する受講生が見つからない場合
    when(repository.searchStudent(999)).thenReturn(null);

    // 例外の発生を確認
    assertThatThrownBy(() -> sut.searchStudent(999))
        .isInstanceOf(NoDataException.class)
        .hasMessage("該当する受講生が見つかりません。ID：999");

    // 続くメソッドが呼ばれないことを確認
    verify(repository, times(0)).searchStudentsCourses(anyInt());
    verify(repository, times(0)).searchStatuses(anyLong());
    verify(converter, times(0)).convertCourseDetails(anyList(), anyList());
  }

  @Test
  void 受講生情報の登録機能_すべての情報が正常に登録されること() {
    // Mock設定
    when(repository.registerStudent(student1)).thenReturn(1);
    when(repository.registerStudentsCourses(any(StudentsCourse.class))).thenReturn(1);
    when(repository.registerStudentsCoursesStatus(any(StudentsCoursesStatus.class))).thenReturn(1);
    // Mockito.any()：どんなStudentsCourseが引数でもthenReturn(1)が適用される

    // 実行
    StudentDetail actual = sut.registerStudent(studentDetail);

    // 呼び出し確認
    verify(repository, times(1)).registerStudent(student1);
    verify(repository, times(studentsCourses.size())).registerStudentsCourses(
        any(StudentsCourse.class));
    verify(repository, times(statuses.size())).registerStudentsCoursesStatus(any(
        StudentsCoursesStatus.class));

    // 検証
    assertNotNull(actual);
    assertEquals(student1, actual.getStudent());
    assertEquals(courseDetails, actual.getCourseDetails());
  }

  @Test
  void 受講生情報の更新機能_すべての情報が正常に更新されること() {
    // 事前準備
    var updateCourseDetail = new CourseDetail(course1, List.of(status1));
    var updateStudentDetail = new StudentDetail(student1, List.of(updateCourseDetail));

    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(student1);  // studentId -> updateStudent
    when(repository.searchStudentsCourses(studentId)).thenReturn(
        List.of(course1));  // studentId -> List(course1)
    when(repository.searchStatuses(course1.getAttendingId())).thenReturn(List.of(status1));

    when(repository.updateStudent(student1)).thenReturn(1);  // student1 -> 更新が成功する
    when(repository.updateStudentsCourses(course1)).thenReturn(1);  // course1 -> 更新が成功する
    when(repository.registerStudentsCoursesStatus(status1)).thenReturn(
        1);  // status1 -> ステータス更新（新規ステータス登録）が成功する

    // 実行
    sut.updateStudent(updateStudentDetail);

    // 呼び出し確認
    verify(repository).updateStudent(student1);
    verify(repository).updateStudentsCourses(course1);
    verify(repository).registerStudentsCoursesStatus(status1);
  }

  @Test
  void 受講生情報の更新機能_ステータス情報のユニーク制約違反時には更新処理が実行されること() {
    // 事前準備
    var updateCourseDetail = new CourseDetail(course1, List.of(status1));
    var updateStudentDetail = new StudentDetail(student1, List.of(updateCourseDetail));

    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(student1);  // studentId -> updateStudent
    when(repository.searchStudentsCourses(studentId)).thenReturn(
        List.of(course1));  // studentId -> List(course1)
    when(repository.searchStatuses(course1.getAttendingId())).thenReturn(List.of(status1));

    when(repository.updateStudent(student1)).thenReturn(1);  // student1 -> 更新が成功する
    when(repository.updateStudentsCourses(course1)).thenReturn(1);  // course1 -> 更新が成功する
    when(repository.registerStudentsCoursesStatus(status1)).thenThrow(
        new DataIntegrityViolationException(
            "ステータス情報重複エラー発生　新規ステータス情報登録から更新に切り替え"));
    when(repository.updateStudentsCoursesStatus(status1)).thenReturn(1);  // status1 -> 更新が成功する

    // 実行
    sut.updateStudent(updateStudentDetail);

    // 呼び出し確認
    verify(repository).updateStudent(student1);
    verify(repository).updateStudentsCourses(course1);
    verify(repository).updateStudentsCoursesStatus(status1);
  }

  @Test
  void 受講生情報の更新機能_受講生が見つからない場合にNoDataExceptionが発生すること() {
    when(repository.searchStudent(studentId)).thenReturn(null);  // nullを返す

    assertThatThrownBy(() -> sut.updateStudent(studentDetail))
        .isInstanceOf(NoDataException.class)
        .hasMessageContaining("更新対象の受講生情報が見つかりません。");

    verify(repository, times(0)).searchStudentsCourses(anyInt());
  }

  @Test
  void 受講生情報の更新機能_受講生に紐づくコース情報が見つからない場合にNoDataExceptionが発生すること() {
    when(repository.searchStudent(studentId)).thenReturn(student1);
    when(repository.searchStudentsCourses(studentId)).thenReturn(List.of());  // 空リストを返す

    assertThatThrownBy(() -> sut.updateStudent(studentDetail))
        .isInstanceOf(NoDataException.class)
        .hasMessageContaining("更新対象のコース受講情報が見つかりません。");

    verify(repository, times(0)).searchStatuses(anyLong());
  }

  @Test
  void 受講生情報の更新機能_受講コース情報に紐づくステータス情報が見つからない場合にNoDataExceptionが発生すること() {
    when(repository.searchStudent(studentId)).thenReturn(student1);
    when(repository.searchStudentsCourses(studentId)).thenReturn(List.of(course1));
    when(repository.searchStatuses(course1.getAttendingId())).thenReturn(List.of());  // 空リストを返す

    assertThatThrownBy(() -> sut.updateStudent(studentDetail))
        .isInstanceOf(NoDataException.class)
        .hasMessageContaining("更新対象の受講ステータス情報が見つかりません。");
  }

  @Test
  void 受講生情報の更新機能_更新が行われなかった場合にProcessFailedExceptionが発生すること() {
    when(repository.searchStudent(studentId)).thenReturn(student1);
    when(repository.searchStudentsCourses(studentId)).thenReturn(List.of(course1));
    when(repository.searchStatuses(course1.getAttendingId())).thenReturn(List.of(status1));

    when(repository.updateStudent(student1)).thenReturn(0);
    when(repository.updateStudentsCourses(course1)).thenReturn(0);
    when(repository.registerStudentsCoursesStatus(status1)).thenThrow(
        new DataIntegrityViolationException(""));
    when(repository.updateStudentsCoursesStatus(status1)).thenReturn(0);

    assertThatThrownBy(() -> sut.updateStudent(studentDetail))
        .isInstanceOf(ProcessFailedException.class)
        .hasMessageContaining(
            "受講生情報・コース受講情報・受講ステータス いずれも更新されませんでした。");
  }
  
}