package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生一覧表示機能_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 事前準備
    // Mockのconverterの引数を用意
    List<Student> studentList = new ArrayList<>();
    List<StudentsCourse> studentCourseList = new ArrayList<>();
    // expectedを用意
    List<StudentDetail> expected = new ArrayList<>();
    // whenのメソッドを呼び出した場合に、thenReturnを返すようMockを設定
    Mockito.when(repository.displayStudent()).thenReturn(studentList);
    Mockito.when(repository.displayCourse()).thenReturn(studentCourseList);
    Mockito.when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(expected);

    // 実行
    List<StudentDetail> actual = sut.getStudentList();

    // 検証
    Mockito.verify(repository, times(1)).displayStudent();
    Mockito.verify(repository, times(1)).displayCourse();
    Mockito.verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);

    // アサーション
    assertNotNull(actual);  // nullでないことを確認
    assertEquals(expected, actual);  // 期待値との一致を確認
  }

  @Test
  void 受講生詳細情報の検索機能() {
    // 事前準備
    Integer studentId = 1;  // 適当なstudentIdを設定
    Student student = new Student();
    student.setStudentId(studentId);  // studentにstudentIdを設定
    List<StudentsCourse> studentCourseList = new ArrayList<>();

    // Mock設定
    Mockito.when(repository.searchStudent(studentId)).thenReturn(student);
    Mockito.when(repository.searchStudentsCourses(student.getStudentId())).thenReturn(studentCourseList);

    // 実行
    StudentDetail actual = sut.searchStudent(studentId);

    // 検証
    Mockito.verify(repository, times(1)).searchStudent(studentId);
    Mockito.verify(repository, times(1)).searchStudentsCourses(student.getStudentId());

    // 結果のアサーション
    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(studentCourseList, actual.getStudentsCourses());
  }

  @Test
  void 受講生情報の登録機能が動作していること() {
    // 事前準備
    StudentDetail studentDetail = new StudentDetail();
    Student student = new Student();
    student.setStudentId(1);

    List<StudentsCourse> studentCourseList = new ArrayList<>();
    StudentsCourse courseA = new StudentsCourse();
    StudentsCourse courseB = new StudentsCourse();
    studentCourseList.add(courseA);
    studentCourseList.add(courseB);

    studentDetail.setStudent(student);
    studentDetail.setStudentsCourses(studentCourseList);

    // Mock設定
    Mockito.when(repository.registerStudent(student)).thenReturn(1);
    Mockito.when(repository.registerStudentsCourses(Mockito.any(StudentsCourse.class))).thenReturn(1);
      // Mockito.any()：どんなStudentsCourseが引数でもthenReturn(1)が適用される

    // 実行
    StudentDetail actual = sut.registerStudent(studentDetail);

    // 検証
    Mockito.verify(repository, times(1)).registerStudent(studentDetail.getStudent());
    Mockito.verify(repository, times(studentCourseList.size())).registerStudentsCourses(Mockito.any(StudentsCourse.class));

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(studentCourseList.size(), actual.getStudentsCourses().size());
    // コース情報を確認
    for (StudentsCourse actualCourse : actual.getStudentsCourses()) {
      // studentId
      assertEquals(student.getStudentId(), actualCourse.getStudentId());
      // startDate, deadline
      assertNotNull(actualCourse.getStartDate());
      assertNotNull(actualCourse.getDeadline());
    }
  }

  @Test
  void 受講生情報の更新機能が動作していること() {
    // 事前準備
    // 更新情報 updateStudentDetail
    Integer studentId = 1;
    StudentDetail updateStudentDetail = new StudentDetail();
    Student updateStudent = new Student();
    updateStudent.setStudentId(studentId);
    updateStudentDetail.setStudent(updateStudent);
    List<StudentsCourse> updateStudentsCourses = new ArrayList<>();
    StudentsCourse courseA = new StudentsCourse();
    courseA.setStudentId(studentId);
    StudentsCourse courseB = new StudentsCourse();
    courseB.setStudentId(studentId);
    updateStudentsCourses.add(courseA);
    updateStudentsCourses.add(courseB);
    updateStudentDetail.setStudentsCourses(updateStudentsCourses);

    // 更新対象 existedStudentDetail
    StudentDetail existedStudentDetail = new StudentDetail();
    Student existedStudent = new Student();
    existedStudent.setStudentId(studentId);
    List<StudentsCourse> existedStudentsCourses = new ArrayList<>();
    StudentsCourse existedStudentsCourseA = new StudentsCourse();
    existedStudentsCourseA.setAttendingId(1L);
    StudentsCourse existedStudentsCourseB = new StudentsCourse();
    existedStudentsCourseB.setAttendingId(2L);
    existedStudentsCourses.add(existedStudentsCourseA);
    existedStudentsCourses.add(existedStudentsCourseB);
    existedStudentDetail.setStudent(existedStudent);
    existedStudentDetail.setStudentsCourses(existedStudentsCourses);

    // Mock設定
    Mockito.when(repository.searchStudent(studentId)).thenReturn(existedStudent);  // studentId -> existedStudent
    Mockito.when(repository.searchStudentsCourses(studentId)).thenReturn(existedStudentsCourses);  // studentId -> existedStudentsCourses
    Mockito.when(repository.updateStudent(updateStudent)).thenReturn(1);  // updateStudent -> 1
    Mockito.when(repository.updateStudentsCourses(Mockito.any(StudentsCourse.class))).thenReturn(1);  // updateStudentsCourses(any) -> 1

    // 実行
    sut.updateStudent(updateStudentDetail);

    // 検証
    Mockito.verify(repository, times(1)).updateStudent(updateStudent);
    Mockito.verify(repository, times(updateStudentsCourses.size()))
        .updateStudentsCourses(Mockito.any(StudentsCourse.class));

  }
}