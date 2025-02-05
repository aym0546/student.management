package raisetech.student.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
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
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("message", "データの制約違反です。入力データを確認してください。");
    error.put("error", ex.getCause().getMessage());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 検索不一致
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex, HttpServletRequest req) {
    Map<String, Object> errors = new HashMap<>();
    errors.put("status", HttpStatus.BAD_REQUEST.value());
    errors.put("path", req.getRequestURI());
    errors.put("message", "該当するデータが見つかりません。");
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
   }

  // その他の例外
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleOtherException(Exception ex){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("サーバーエラーが発生しました。：" + ex.getMessage());
  }
}
