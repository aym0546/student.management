package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;

/**
 * 受講生情報を扱うリポジトリ（インターフェース）
 *
 * 全件検索や単一条件での検索、コース情報の検索が行えるクラスです。
 */

@Mapper
public interface StudentRepository {
  // データベースを操作するためのインターフェース

//  検索メソッド
  @Select("SELECT * FROM students WHERE name = #{name}")
  List<Student> searchByName(String name); // この引数のnameを↑の#{name}として受け取ってくれる

  /**
   * 受講生の一覧表示
   * @return 受講生情報の一覧
   */
  @Select("SELECT * FROM students")
  List<Student> displayStudent();  // Student型のリスト形式で出力

  /**
   * コース受講情報の一覧表示
   * @return 受講情報の一覧
   */
  @Select("SELECT * FROM students_courses")
  List<StudentsCourse> displayCourse();

//  受講生情報の検索
  @Select("SELECT * FROM students WHERE student_id = #{studentId}")
  Student searchStudent(String studentId);

//  コース情報の検索
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentsCourse> searchStudentsCourses(String studentId);

  /**
   * 受講生情報の登録
   * @param registerStudent 登録された受講生情報
   */
  @Insert("INSERT INTO students(student_id, full_name, name_pronunciation, nickname, email, area, age, gender, remark, is_deleted) VALUES (#{studentId}, #{fullName}, #{namePronunciation}, #{nickname}, #{email}, #{area}, #{age}, #{gender}, #{remark}, false)")
  void registerStudent(Student registerStudent);

  /**
   * コース情報の登録
   * @param studentsCourse 登録された受講コース情報
   */
  @Insert("INSERT INTO students_courses(student_id, course, start_date, deadline) VALUES (#{studentId}, #{course}, #{startDate}, #{deadline})")
  @Options(useGeneratedKeys = true, keyProperty = "attendingId")
  void registerStudentsCourses(StudentsCourse studentsCourse);

//  受講生情報の更新
  @Update("UPDATE students SET full_name = #{fullName}, name_pronunciation = #{namePronunciation}, nickname = #{nickname}, email = #{email}, area = #{area}, age = #{age}, gender = #{gender}, remark = #{remark}, is_deleted = #{isDeleted} WHERE student_id = #{studentId}")
  void updateStudent(Student updateStudent);

//  コース情報の更新
  @Update("UPDATE students_courses SET course = #{course} WHERE attending_id = #{attendingId}")
  void updateStudentsCourses(StudentsCourse updateCourse);

}
