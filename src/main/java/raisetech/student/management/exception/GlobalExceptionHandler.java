package raisetech.student.management.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // エラーハンドリングテスト用
  @ExceptionHandler(TestException.class)
  public ResponseEntity<String> handleAllExceptions(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("エラーが発生しました。：" + ex.getMessage());
  }

  // バリデーションエラー
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    // エラー元のフィールドkeyとエラーメッセージvalueをMapに格納
    for (ObjectError error : ex.getBindingResult().getAllErrors()) {
      String fieldName = ((FieldError)error).getField(); // errorをフィールド固有のエラーFieldErrorにキャストして、エラーが発生したフィールドを取得
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // 制約違反エラー
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolationsExceptions(ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
      errors.put(constraintViolation.getPropertyPath().toString(),  // エラー元のフィールドpropertyを取得
          constraintViolation.getMessage());
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // その他の例外
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleOtherException(Exception ex){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("サーバーエラーが発生しました。：" + ex.getMessage());
  }
}
