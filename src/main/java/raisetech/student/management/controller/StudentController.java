package raisetech.student.management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして受け付けるControllerです。
 */
@RestController

public class StudentController {

  private final StudentService service;

  /**
   * コンストラクタ
   * @param service 受講生サービス
   */
  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 【受講生一覧表示】
   * 全件検索のため、条件指定は行わない。
   * @return 受講生一覧（全件）
   */
  @GetMapping("/studentList")
  public List<StudentDetail> getStudent() {
    return service.getStudentList();
  }

  /**
   * 【受講生登録】
   * POSTで受け取った情報を元に新規受講生登録を行う。
   * @param studentDetail 入力情報（受講生情報）
   * @return 登録された受講生情報（自動生成ID情報を含む）
   */
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody StudentDetail studentDetail) {
    return ResponseEntity.ok(service.registerStudent(studentDetail));
    // 成功時、登録情報をそのまま返す
  }

  /**
   * 【受講生検索】
   * IDに紐づく任意の受講生の情報を取得。
   * @param studentId 受講生ID
   * @return 受講生情報
   */
  @GetMapping("/student/{studentId}") // student_idを元に単一の受講生情報を表示
  public StudentDetail getStudent(@PathVariable String studentId) {
    return service.searchStudent(studentId);
  }

  /**
   * 【受講生更新】
   * 指定されたIDの受講生詳細情報を更新する。
   * キャンセルフラグの更新も行う（論理削除）
   * @param studentDetail 更新される入力情報（受講生詳細情報）
   * @return 実行結果
   */
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok(studentDetail.getStudent().getFullName() + "さんの更新処理が成功しました。");
    // 更新が完了したらレスポンスとしてOK(200)とメッセージを返す。
  }

}
