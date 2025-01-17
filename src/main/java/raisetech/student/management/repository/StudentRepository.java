package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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

}
