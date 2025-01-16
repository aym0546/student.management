package raisetech.student.management;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StudentRepository {
  // データベースを操作するためのインターフェース

//  検索メソッド
  @Select("SELECT * FROM students WHERE name = #{name}")
  List<Student> searchByName(String name); // この引数のnameを↑の#{name}として受け取ってくれる

//  一覧表示メソッド
  @Select("SELECT * FROM students")
  List<Student> displayStudent();  // Student型のリスト形式で出力

//  コース一覧表示
  @Select("SELECT * FROM students_courses")
  List<StudentsCourse> displayCourse();
}
