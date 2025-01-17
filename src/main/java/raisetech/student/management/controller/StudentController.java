package raisetech.student.management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.service.StudentService;

@RestController

public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  //	生徒情報の一覧表示
  @GetMapping("/students")
  public List<Student> getStudentList() {
    // リクエストの加工処理、入力チェック
    return service.getStudentList();
  }

  //	コース情報の一覧表示
  @GetMapping("/students_courses")
  public List<StudentsCourse> getStudentCouseList() {
    return service.getStudentCourseList();
  }

  //　20代受講生の抽出
  @GetMapping("/20s")
  public  List<Student> get20s() {
    return service.get20s();
  }

  // Javaコースの抽出
  @GetMapping("/Java")
  public  List<StudentsCourse> getJava() {
    return service.getJavaCourseList();
  }
}
