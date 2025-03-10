package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.service.StudentSearchEntity;

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
   * 【受講ステータスの一覧表示】
   *
   * @return 受講ステータス一覧（全件）
   */
  List<CourseStatus> displayStatus();

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
   * 【受講情報検索】　受講IDに紐づく受講ステータスを検索
   *
   * @param attendingId 受講ID
   * @return 受講IDに紐づく受講ステータス
   */
  CourseStatus searchCourseStatus(Long attendingId);

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
   * 【受講ステータスの登録】　入力情報を元に、受講ステータスを登録。 ステータスIDは自動生成される。
   *
   * @param courseStatus 登録内容（受講ステータス情報）
   */
  int registerCourseStatus(CourseStatus courseStatus);

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
   * 【受講ステータスの更新】 特定のステータスIDの受講状況を、入力情報を元に更新。 実際に変更可能なのはステータスのみ（その他は自動設定のため）
   *
   * @param courseStatus 更新内容（受講ステータス）
   */
  int updateCourseStatus(CourseStatus courseStatus);

  /**
   * 【受講生情報の詳細検索】
   *
   * @param condition リクエスト（検索条件）
   * @return 該当する受講生リスト
   */
  List<Student> findStudent(StudentSearchEntity condition);

  /**
   * 【受講コース情報の詳細検索】
   *
   * @param condition リクエスト（検索条件）
   * @return 該当するコース情報リスト
   */
  List<StudentsCourse> findCourse(StudentSearchEntity condition);

  /**
   * 【ステータス情報の詳細検索】
   *
   * @param condition リクエスト（検索条件）
   * @return 該当するステータス情報リスト
   */
  List<CourseStatus> findStatus(StudentSearchEntity condition);
}
