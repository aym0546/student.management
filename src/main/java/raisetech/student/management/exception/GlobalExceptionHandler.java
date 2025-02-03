package raisetech.student.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TestException.class)
  public ResponseEntity<String> handleAllExceptions(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("エラーが発生しました。：" + ex.getMessage());
  }

}
