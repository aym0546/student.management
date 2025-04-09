package raisetech.student.management.controller.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 旧URIリダイレクト専用のControllerです。
 */
@RestController
@Tag(name = "旧URIリダイレクト", description = "旧URIリダイレクト用API")
public class RedirectController {

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
