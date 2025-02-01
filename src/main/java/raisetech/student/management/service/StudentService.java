package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

//	インターフェースStudentRepositoryを呼び出し
  private final StudentRepository repository;

//  コンストラクタに対しDI  ※ StudentServiceクラスにStudentRepositoryインターフェースを注入
  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

//  生徒情報表示のメソッド
  public List<Student> getStudentList() {
    return repository.displayStudent();
  }

//  生徒情報の検索
  public StudentDetail searchStudent(String studentId) {
    StudentDetail studentDetail = new StudentDetail();
    // HTML画面から入ってきたstudentIDを元に受講生情報を検索
    Student student = repository.searchStudent(studentId);
    // 受講生情報のstudentIDに基づくコース情報を検索
    List<StudentsCourse> studentsCourses = repository.searchStudentsCourses(student.getStudentId());
    // 受講生情報・コース情報をstudentDetailに設定
    studentDetail.setStudent(student);
    studentDetail.setStudentsCourses(studentsCourses);
    return studentDetail;
  }

//  受講生情報とコース情報の登録メソッド
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    // 受講生情報の登録
    repository.registerStudent(studentDetail.getStudent());
    // コース情報の登録（コースの数だけコース情報を取得する）
    for(StudentsCourse studentsCourse : studentDetail.getStudentsCourses()) {
      // student_idは↑で登録したstudent_idを流用
      studentsCourse.setStudentId(studentDetail.getStudent().getStudentId());
      // start_dateとdeadlineは現在日時とその１年後を取得
      studentsCourse.setStartDate(LocalDateTime.now());
      studentsCourse.setDeadline(LocalDateTime.now().plusYears(1));
      repository.registerStudentsCourses(studentsCourse);
    }
    return studentDetail;
  }

//  受講生情報とコース情報の更新メソッド
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    // ここに遷移した時点で既に特定のstudentIdのstudentDetailが呼び出されている
    // 受講生情報の更新
    repository.updateStudent(studentDetail.getStudent());
    // コース情報の更新
    for(StudentsCourse studentsCourse : studentDetail.getStudentsCourses()) {
      // studentDetailに含まれるStudentCoursesを一つづつ取り出して処理
      // ↓ studentsCourseにはデータベースから取得した時点でattending_idが設定済みであるため、自動的にattending_idは@Updateに渡される
      repository.updateStudentsCourses(studentsCourse);
    }
  }


//  受講情報表示のメソッド
  public List<StudentsCourse> getStudentCourseList() {
    return repository.displayCourse();
  }

}
