package raisetech.student.management.repository;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;

/**
 * 受講生情報を扱うRepository（インターフェース） 受講生テーブルと受講生コース情報テーブルと紐づいています。 全件検索や単一条件での検索、コース情報の検索が行えるクラスです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 【受講生の一覧表示】
   *
   * @return 受講生一覧（全件）
   */
  List<Student> displayStudent();

  /**
   * 【コース情報の一覧表示】
   *
   * @return コース情報一覧（全件）
   */
  List<StudentsCourse> displayCourse();

  /**
   * 【受講ステータス情報の一覧表示】
   *
   * @return 受講ステータス情報（全件）
   */
  List<StudentsCoursesStatus> displayStatus();

  /**
   * 【受講生検索】 受講生IDから受講生の検索。
   *
   * @param studentId 受講生ID
   * @return 受講生情報
   */
  Student searchStudent(Integer studentId);

  /**
   * 【コース情報検索】 受講生IDに紐づくコース情報を検索。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づくコース情報
   */
  List<StudentsCourse> searchStudentsCourses(Integer studentId);

  /**
   * 【受講ステータス検索】　コース受講IDに紐づく受講ステータス情報を検索して降順にソート。
   *
   * @param attendingId コース受講ID
   * @return コース受講IDに紐づく受講ステータス情報
   */
  List<StudentsCoursesStatus> searchStatuses(Long attendingId);

  /**
   * 【受講生情報の登録】 入力情報を元に、新規受講生を登録。
   *
   * @param registerStudent 登録情報（受講生情報)
   */
  int registerStudent(Student registerStudent);

  /**
   * 【コース情報の登録】 入力情報を元に、新規コース情報を登録。 受講ID（attendingID）は自動生成される。
   *
   * @param studentsCourse 登録内容（コース情報）
   */
  int registerStudentsCourses(StudentsCourse studentsCourse);

  /**
   * 【受講ステータス情報の登録】　入力情報を元に、受講ステータスを登録。ステータスID（statusId）は自動生成。
   *
   * @param studentsCoursesStatus 登録内容（ステータス情報）
   */
  int registerStudentsCoursesStatus(StudentsCoursesStatus studentsCoursesStatus);

  /**
   * 【受講生情報の更新】 特定の受講生IDの受講生情報を、入力情報を元に更新。
   *
   * @param updateStudent 更新内容（受講生情報）
   */
  int updateStudent(Student updateStudent);

  /**
   * 【コース情報の更新】 特定の受講IDのコース情報を、入力情報を元に更新。 実際に変更可能なのはコース名のみ（その他は自動設定のため）
   *
   * @param updateCourse 更新内容（コース情報）
   */
  int updateStudentsCourses(StudentsCourse updateCourse);

  /**
   * 【受講ステータス情報の更新】
   *
   * @param status ステータス
   */
  int updateStudentsCoursesStatus(StudentsCoursesStatus status);

  /**
   * 【既存の未終了ステータスの更新】
   *
   * @param attendingId     コース受講ID
   * @param statusStartDate 次ステータス開始日
   */
  int updateStatusEndDateForMysql(Long attendingId, LocalDate statusStartDate);

  /**
   * 【既存の未終了ステータスの更新】
   *
   * @param attendingId     コース受講ID
   * @param statusStartDate 次ステータス開始日
   */
  int updateStatusEndDateForH2(Long attendingId, LocalDate statusStartDate);
}
