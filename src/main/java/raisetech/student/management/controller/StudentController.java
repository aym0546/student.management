package raisetech.student.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.util.List;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RestController
@Tag(name = "受講生管理", description = "受講生の登録・検索・更新を行うAPI")
public class StudentController {

  private final StudentService service;

  /**
   * コンストラクタ
   *
   * @param service 受講生サービス
   */
  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 【詳細情報検索】 リクエストデータに基づいて検索を行う。
   *
   * @param searchForm リクエスト情報
   * @return 該当する受講生詳細情報のリスト
   */
  @Operation(summary = "詳細検索", description = "クエリパラメータから取得した条件で検索を行います。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "404", description = "該当する受講生が見つかりません"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @GetMapping("/students")  // GET /students?__=__
  public ResponseEntity<List<StudentDetail>> searchStudents(StudentSearchForm searchForm) {

    List<StudentDetail> studentDetails = service.getStudentList(searchForm.toDTO());

    if (studentDetails.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(studentDetails);
  }

  /**
   * 【受講生登録】 POSTで受け取った情報を元に新規受講生登録を行う。
   *
   * @param studentDetail 入力情報（受講生情報）
   * @return 登録された受講生情報（自動生成ID情報を含む）
   */
  @Operation(summary = "受講生登録", description = "新規受講生の詳細情報を登録します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "成功"),
      @ApiResponse(responseCode = "400", description = "更新処理に失敗しました"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @PostMapping("/students")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {

    StudentDetail createdStudent = service.registerStudent(studentDetail);
    URI location = URI.create("/students/" + createdStudent.getStudent().getStudentId());

    return ResponseEntity.created(location).body(createdStudent);
  }

  /**
   * 【受講生検索】 IDに紐づく任意の受講生の情報を取得。
   *
   * @param studentId 受講生ID
   * @return 受講生情報
   */
  @Operation(summary = "受講生検索", description = "パスパラメータで指定された受講生IDで検索を行います。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "404", description = "該当する受講生が見つかりません"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @GetMapping("/students/{studentId}")
  public StudentDetail getStudent(
      @Parameter(description = "検索する受講生のID", example = "12")
      @PathVariable @Valid Integer studentId) {
    return service.searchStudent(studentId);
  }

  /**
   * 【受講生更新】 指定されたIDの受講生詳細情報を更新する。 キャンセルフラグの更新も行う（論理削除）
   *
   * @param studentDetail 更新される入力情報（受講生詳細情報）
   * @return 実行結果
   */
  @Operation(summary = "受講生更新", description = "指定IDの受講生詳細情報を更新します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "400", description = "更新処理に失敗しました"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @PutMapping("/students")
  public ResponseEntity<String> updateStudent(
      @RequestBody @Validated({Default.class, Update.class}) StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok(
        studentDetail.getStudent().getFullName() + "さんの更新処理が成功しました。");
    // 更新が完了したらレスポンスとしてOK(200)とメッセージを返す。
  }

  /**
   * 【リダイレクト】 旧URIが指定された場合に、正しいURI"/students"にリダイレクトする。
   *
   * @return /students リダイレクト
   */
  @Operation(summary = "/students リダイレクト", description = "旧URIのハンドリングメソッドです。")
  @GetMapping("/studentList")
  public ResponseEntity<Void> redirectStudents() {
    return ResponseEntity.status(HttpStatus.SEE_OTHER)
        .location(URI.create("/students")).build();
  }

}
