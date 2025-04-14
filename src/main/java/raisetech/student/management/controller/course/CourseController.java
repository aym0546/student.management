package raisetech.student.management.controller.course;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.data.Course;
import raisetech.student.management.service.course.CourseService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして受け付けるControllerです。
 */
@Validated
@RestController
@RequestMapping("/courses")
@Tag(name = "コースマスタ管理", description = "コースマスタの登録・検索・更新を行うAPI")
public class CourseController {

  private final CourseService service;

  /**
   * コンストラクタ
   *
   * @param service 受講生サービス
   */
  @Autowired
  public CourseController(CourseService service) {
    this.service = service;
  }

  /**
   * 【コースマスタの全件取得】開講状況にかかわらず全て表示
   *
   * @return コースマスタリスト（全件）
   */
  @Operation(summary = "コースマスタ取得", description = "コースマスタを全件取得します")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "404", description = "コースマスタが見つかりません"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @GetMapping
  public ResponseEntity<List<Course>> getCourses() {
    var courses = service.getCourseList();
    if (courses.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(courses);
  }

  /**
   * 【コースマスタの登録】POSTで受け取った情報を元に新規マスタ登録を行う。
   *
   * @param course 入力情報（コースマスタ情報）
   * @return 登録されたコースマスタ情報（自動生成されたIDを含む）
   */
  @Operation(summary = "コースマスタ登録", description = "新規コースマスタを登録します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "成功"),
      @ApiResponse(responseCode = "400", description = "登録処理に失敗しました"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @PostMapping
  public ResponseEntity<Course> registerCourseMaster(@RequestBody @Valid Course course) {

    service.registerCourseMaster(course);
    // 登録されたIDから、登録されたマスタ情報を呼び出し
    var createdCourse = service.getCourseMaster(course.getCourseId());
    URI location = URI.create("/courses/" + createdCourse.getCourseId());

    return ResponseEntity.created(location).body(createdCourse);
  }

  /**
   * 【コースマスタの更新】受け取った情報をもとに既存のコースマスタを更新する。
   *
   * @param course 入力情報（コースマスタ情報）
   */
  @Operation(summary = "コースマスタ更新", description = "既存のコースマスタを更新します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "成功"),
      @ApiResponse(responseCode = "400", description = "更新処理に失敗しました"),
      @ApiResponse(responseCode = "404", description = "更新対象コースマスタが見つかりません"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー"),
  })
  @PutMapping("/{courseId}")
  public ResponseEntity<String> updateCourseMaster(
      @PathVariable Integer courseId,
      @RequestBody @Validated({Default.class, Update.class}) Course course) {
    service.updateCourseMaster(courseId, course);
    return ResponseEntity.ok(
        "コース名：【 " + course.getCourseName() + " 】の更新処理が成功しました。");
  }

  /**
   * 【コースマスタの削除】
   *
   * @param courseId 削除対象のコースマスタID
   */
  @Operation(summary = "コースマスタ削除", description = "既存のコースマスタを削除します。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "コースマスタ削除に成功しました"),
      @ApiResponse(responseCode = "400", description = "削除に失敗しました"),
      @ApiResponse(responseCode = "404", description = "削除対象コースマスタが見つかりません"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー"),
  })
  @DeleteMapping("/{courseId}")
  public ResponseEntity<String> deleteCourseMaster(@PathVariable @Valid Integer courseId) {
    service.deleteCourseMaster(courseId);
    return ResponseEntity.noContent().build();
  }

}
