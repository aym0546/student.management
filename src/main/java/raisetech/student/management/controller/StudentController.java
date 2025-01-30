package raisetech.student.management.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

@RestController

public class StudentController {

  private final StudentService service;
  private final StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

//  各生徒の受講情報の一覧表示
  @GetMapping("/studentList")
  public List<StudentDetail> getStudent() {
    List<Student> students = service.getStudentList();
    List<StudentsCourse> studentsCourses = service.getStudentCourseList();
    return converter.convertStudentDetails(students, studentsCourses);
  }

//  コース情報の一覧表示
  @GetMapping("/students_courses")
  public List<StudentsCourse> getCourse() {
    return service.getStudentCourseList();
  }

//  生徒情報の登録①
//  登録フォームregisterStudent.htmlを表示し、新規StudentDetailオブジェクトをフォームに渡す
  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudentsCourses(Arrays.asList(new StudentsCourse()));
    model.addAttribute("studentDetail", studentDetail);
    return "registerStudent";
  }

//  生徒情報の登録②
//  登録フォームの情報をPOSTで受け取り、生徒一覧画面に遷移
  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if(result.hasErrors()) {  // 入力チェックでエラーがあった場合の対応
      return "registerStudent";  // 登録フォームを再表示
    }
    service.registerStudent(studentDetail);
    System.out.println(studentDetail.getStudent().getFullName() + "さんが新規受講生として登録されました。");
    return "redirect:/studentList";  // 一覧画面/studentListに飛ぶ
  }

  //  IDからの検索機能 → 更新画面に飛ぶ
  @GetMapping("/student/{studentId}") // student_idを元に単一の受講生情報を表示
  public String getStudent(@PathVariable String studentId, Model model) {
    StudentDetail studentDetail = service.searchStudent(studentId);
    model.addAttribute("studentDetail", studentDetail);
    return "updateStudent"; // 更新画面に遷移
  }

  //  生徒情報の更新
  @PostMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok(studentDetail.getStudent().getFullName() + "さんの更新処理が成功しました。");
  }

}
