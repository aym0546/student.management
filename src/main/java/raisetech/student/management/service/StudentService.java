package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

/**
 * 受講生情報を取り扱うServiceです。
 * 受講生の検索や登録、更新処理を行います。
 */
@Service
public class StudentService {

//	インターフェースStudentRepository、StudentConverterを呼び出し
  private final StudentRepository repository;
  private final StudentConverter converter;

  /**
   * コンストラクタ
   * @param repository 受講生を扱うリポジトリ
   * @param converter 受講生詳細と受講生情報・コース情報のコンバーター
   */
  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   *【受講生一覧表示】
   * 全件検索のため、条件指定は行わない。
   * @return 受講生詳細情報一覧（全件）
   */
  public List<StudentDetail> getStudentList() {
    List<Student> studentList = repository.displayStudent();
    List<StudentsCourse> studentsCourses = repository.displayCourse();
    return converter.convertStudentDetails(studentList, studentsCourses);
  }

  /**
   * 【受講生検索】
   * IDに紐づく受講生情報を取得し、次いでその受講生に紐づく受講生コース情報を取得して設定。
   * @param studentId 受講生ID
   * @return 受講生詳細情報
   */
  public StudentDetail searchStudent(Integer studentId) {
    // 受け取ったstudentIDを元に受講生情報を検索
    Student student = repository.searchStudent(studentId);
    // 受講生情報のstudentIDに基づいてコース情報を検索
    List<StudentsCourse> studentsCourses = repository.searchStudentsCourses(student.getStudentId());
    // ↑の受講生情報・コース情報を持つnew StudentDetailを生成してreturn
    return new StudentDetail(student, studentsCourses);
  }

  /**
   * 【受講生情報の登録】
   * 新規受講生の受講生情報と受講コース情報を登録。
   * StudentIDで2つの情報を紐付け。
   * attendingID,StartDate,DeadLineは自動設定されます。
   * @param studentDetail 入力情報（受講生詳細情報）
   * @return 登録された受講生詳細情報
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    // 受講生情報の登録
    int registeredStudent = repository.registerStudent(studentDetail.getStudent());
    if (registeredStudent == 0) {
      throw new RuntimeException("受講生の登録に失敗しました。");
    }
    // コース情報の登録（コースの数だけコース情報を取得する）
    int registeredCourse = 0;
    for(StudentsCourse studentsCourse : studentDetail.getStudentsCourses()) {
      // student_idは↑で登録したstudent_idを流用
      studentsCourse.setStudentId(studentDetail.getStudent().getStudentId());
      // start_dateとdeadlineは現在日時とその１年後を取得
      studentsCourse.setStartDate(LocalDateTime.now());
      studentsCourse.setDeadline(LocalDateTime.now().plusYears(1));
      registeredCourse += repository.registerStudentsCourses(studentsCourse);
    }
    if (registeredCourse == 0) {
      throw new RuntimeException("受講コース情報の登録に失敗しました。");
    }
    return studentDetail;
  }

  /**
   * 【受講生更新】
   * 指定されたIDの受講生情報を更新。
   * @param studentDetail 入力された更新情報（受講生詳細）
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    // ここに遷移した時点で既に特定のstudentIdのstudentDetailが呼び出されている
    // 受講生情報の更新
    int updatedStudentData = repository.updateStudent(studentDetail.getStudent());
    // コース情報の更新
    int updatedStudentsCourseData = 0;
    for(StudentsCourse studentsCourse : studentDetail.getStudentsCourses()) {
      // studentDetailに含まれるStudentCoursesを一つづつ取り出して処理
      // ↓ studentsCourseにはデータベースから取得した時点でattending_idが設定済みであるため、自動的にattending_idは@Updateに渡される
      updatedStudentsCourseData += repository.updateStudentsCourses(studentsCourse);
    }
    // 更新されたデータが0の場合に例外をスルー
    if (updatedStudentData == 0 && updatedStudentsCourseData == 0) {
      throw new NullPointerException();
    }

  }

}
