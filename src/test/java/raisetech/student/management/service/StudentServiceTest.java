package raisetech.student.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

  private int studentId;
  private Student student;
  private List<StudentsCourse> baseCourses;
  private StudentDetail baseStudentDetail;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);

    studentId = 1;
    student = new Student(
        studentId, "ベース テスト", "べーす てすと",
        "ベース", "base@email.com", "",
        (short) 20, "Other", "");
    baseCourses = List.of(
        new StudentsCourse(1L, studentId, "Javaコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1)),
        new StudentsCourse(2L, studentId, "AWSコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1))
    );
    baseStudentDetail = new StudentDetail(student, baseCourses);
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
    when(repository.displayStudent()).thenReturn(studentList);
    when(repository.displayCourse()).thenReturn(studentCourseList);
    when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(expected);

    // 実行
    List<StudentDetail> actual = sut.getStudentList();

    // 検証
    verify(repository, times(1)).displayStudent();
    verify(repository, times(1)).displayCourse();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);

    // アサーション
    assertNotNull(actual);  // nullでないことを確認
    assertEquals(expected, actual);  // 期待値との一致を確認
  }

  @Test
  void 受講生詳細情報の検索機能が動作していること() {
    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(student);
    when(repository.searchStudentsCourses(studentId)).thenReturn(baseCourses);

    // 実行
    StudentDetail actual = sut.searchStudent(studentId);

    // 検証
    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, times(1)).searchStudentsCourses(studentId);

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(baseCourses, actual.getStudentsCourses());
  }

  @Test
  void 受講生情報の登録機能が動作していること() {
    // Mock設定
    when(repository.registerStudent(student)).thenReturn(1);
    when(repository.registerStudentsCourses(any(StudentsCourse.class))).thenReturn(1);
      // Mockito.any()：どんなStudentsCourseが引数でもthenReturn(1)が適用される

    // 実行
    StudentDetail actual = sut.registerStudent(baseStudentDetail);

    // 検証
    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(baseCourses.size())).registerStudentsCourses(any(StudentsCourse.class));

    assertNotNull(actual);
    assertEquals(student, actual.getStudent());
    assertEquals(baseCourses, actual.getStudentsCourses());
  }

  @Test
  void 受講生情報の更新機能が動作していること() {
    // 事前準備
    // 更新情報 updateStudentDetail
    var updateStudent = new Student(
        studentId, "更新 テスト", "こうしん てすと",
        "てっくん", "update@email.com", "更新後",
        (short) 20, "Other", "");
    var updateCourses = List.of(
        new StudentsCourse(1L, studentId, "Javaコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1)),
        new StudentsCourse(2L, studentId, "AWSコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1))
    );
    var updateStudentDetail = new StudentDetail(updateStudent, updateCourses);

    // 更新対象 existedStudentDetail
    var existedStudent = new Student(
        studentId, "オリジナル テスト", "おりじなる てすと",
        "てっくん", "existed@email.com", "更新前",
        (short) 20, "Other", "");
    var existedStudentsCourses = List.of(
        new StudentsCourse(1L, studentId, "Javaコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1)),
        new StudentsCourse(2L, studentId, "AWSコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1))
    );
    var existedStudentDetail = new StudentDetail(existedStudent, existedStudentsCourses);

    // Mock設定
    when(repository.searchStudent(studentId)).thenReturn(updateStudent);  // studentId(1) -> updateStudent
    when(repository.searchStudentsCourses(studentId)).thenReturn(updateCourses);  // studentId(1) -> updateCourses
    when(repository.updateStudent(updateStudent)).thenReturn(1);  // updateStudent -> 更新が成功する
    when(repository.updateStudentsCourses(any(StudentsCourse.class))).thenReturn(1);  // any() -> 更新が成功する

    // 実行
    sut.updateStudent(updateStudentDetail);
    StudentDetail actual = sut.searchStudent(studentId);  // 実測値

    // 検証
    // メソッド呼び出し確認
    verify(repository, times(1)).updateStudent(updateStudent);
    verify(repository, times(updateCourses.size()))
        .updateStudentsCourses(any(StudentsCourse.class));
    // 更新前後の比較（結果が行進情報と一致/結果が更新対象と不一致）
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("studentsCourses.startDate", "studentsCourses.deadLine")
        .isEqualTo(updateStudentDetail)  // 期待値との比較
        .isNotEqualTo(existedStudentDetail);
  }
}