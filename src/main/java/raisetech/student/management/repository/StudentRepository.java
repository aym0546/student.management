package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;

/**
 * 受講生情報を扱うRepository（インターフェース）
 * 受講生テーブルと受講生コース情報テーブルと紐づいています。
 * 全件検索や単一条件での検索、コース情報の検索が行えるクラスです。
 */

@Mapper
public interface StudentRepository {
  // データベースを操作するためのインターフェース

  /**
   * 【受講生の一覧表示】
   * @return 受講生一覧（全件）
   */
//  @Select("SELECT * FROM students")
  List<Student> displayStudent();  // Student型のリスト形式で出力

  /**
   * 【コース情報の一覧表示】
   * @return コース情報一覧（全件）
   */
//  @Select("SELECT * FROM students_courses")
  List<StudentsCourse> displayCourse();

  /**
   * 【受講生検索】
   * 受講生IDから受講生の検索。
   * @param studentId 受講生ID
   * @return 受講生情報
   */
//  @Select("SELECT * FROM students WHERE student_id = #{studentId}")
  Student searchStudent(String studentId);

  /**
   * 【コース情報検索】
   * 受講生IDに紐づくコース情報を検索。
   * @param studentId 受講生ID
   * @return 受講生IDに紐づくコース情報
   */
//  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentsCourse> searchStudentsCourses(String studentId);

  /**
   * 【受講生情報の登録】
   * 入力情報を元に、新規受講生を登録。
   * @param registerStudent 登録情報（受講生情報)
   */
//  @Insert("INSERT INTO students(student_id, full_name, name_pronunciation, nickname, email, area, age, gender, remark, is_deleted) VALUES (#{studentId}, #{fullName}, #{namePronunciation}, #{nickname}, #{email}, #{area}, #{age}, #{gender}, #{remark}, false)")
  void registerStudent(Student registerStudent);

  /**
   * 【コース情報の登録】
   * 入力情報を元に、新規コース情報を登録。
   * 受講ID（attendingID）は自動生成される。
   * @param studentsCourse 登録内容（コース情報）
   */
//  @Insert("INSERT INTO students_courses(student_id, course, start_date, deadline) VALUES (#{studentId}, #{course}, #{startDate}, #{deadline})")
//  @Options(useGeneratedKeys = true, keyProperty = "attendingId")
  void registerStudentsCourses(StudentsCourse studentsCourse);

  /**
   * 【受講生情報の更新】
   * 特定の受講生IDの受講生情報を、入力情報を元に更新。
   * @param updateStudent 更新内容（受講生情報）
   */
//  @Update("UPDATE students SET full_name = #{fullName}, name_pronunciation = #{namePronunciation}, nickname = #{nickname}, email = #{email}, area = #{area}, age = #{age}, gender = #{gender}, remark = #{remark}, is_deleted = #{isDeleted} WHERE student_id = #{studentId}")
  void updateStudent(Student updateStudent);

  /**
   * 【コース情報の更新】
   * 特定の受講IDのコース情報を、入力情報を元に更新。
   * 実際に変更可能なのはコース名のみ（その他は自動設定のため）
   * @param updateCourse 更新内容（コース情報）
   */
//  @Update("UPDATE students_courses SET course = #{course} WHERE attending_id = #{attendingId}")
  void updateStudentsCourses(StudentsCourse updateCourse);

}
