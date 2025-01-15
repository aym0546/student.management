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
  @Select("SELECT * FROM student WHERE name = #{name}")
  Student searchByName(String name); // この引数のnameを↑の#{name}として受け取ってくれる

//  登録メソッド
  @Insert("INSERT student values(#{name}, #{age})")
  void registerStudent(String name, int age);

//  登録情報の変更メソッド
  @Update("UPDATE student SET age = #{age} WHERE name = #{name}")
  void updateStudent(String name, int age);

//  登録情報の削除メソッド
  @Delete("DELETE FROM student WHERE name = #{name}")
  void deleteStudent(String name);

//  一覧表示メソッド
  @Select("SELECT * FROM student")
  List<Student> display();  // Student型のリスト形式で出力
}
