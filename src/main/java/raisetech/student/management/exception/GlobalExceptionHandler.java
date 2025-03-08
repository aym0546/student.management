package raisetech.student.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  public ResponseEntity<String> handleTestExceptions(Exception ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("エラーが発生しました。：" + ex.getMessage());
  }

  // バリデーションエラー
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    // エラー元のフィールドkeyとエラーメッセージvalueをMapに格納
    for (ObjectError error : ex.getBindingResult().getAllErrors()) {
      if (error instanceof FieldError fieldError) {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
      } else {
        errors.put(error.getObjectName(), error.getDefaultMessage());
      }
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // 制約違反エラー
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<List<String>> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {

    // エラーメッセージ取得
    String causeMessage;
    if (ex.getRootCause() != null) {
      causeMessage = ex.getRootCause().getMessage();
    } else {
      causeMessage = ex.getMessage();
    }

    List<String> errorMessages = toErrorMessages(causeMessage); //
    return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
  }

  // エラーメッセージの場合分け
  private List<String> toErrorMessages(String errorMessage) {
    List<String> errorList = new ArrayList<>();

    if (errorMessage == null) {
      return errorList;
    }

    Map<Pattern, String> errorPatterns = new LinkedHashMap<>();
    errorPatterns.put(Pattern.compile("Duplicate entry '(.+?)' for key '(.+?)'"),
        "%s の値が重複しています。入力値を確認してください。"); // 重複エラー
    errorPatterns.put(Pattern.compile("Column '(.+?)' cannot be null"),
        "%s は必須項目です。値を入力してください。"); // NotNullエラー
    errorPatterns.put(Pattern.compile("FOREIGN KEY \\(`(.+?)`\\) REFERENCES"),
        "%s に指定されたデータが存在しません。"); // 外部キー制約エラー

    for (Map.Entry<Pattern, String> entry : errorPatterns.entrySet()) {
      Matcher matcher = entry.getKey().matcher(errorMessage);
      while (matcher.find()) {
        String key = matcher.group(matcher.groupCount());  // 最後のグループを取得
        if (entry.getKey().pattern().contains("for key")) {  // 重複エラーの場合はカラム名変換メソッド適用
          key = columnRename(key);
        }
        errorList.add(String.format(entry.getValue(), key));
      }
    }
    return errorList;
  }

  // 不正なカラム名の変換メソッド
  private String columnRename(String key) {
    if (key == null) {
      return "不明なプロパティ";
    } else if (key.endsWith(".PRIMARY")) {
      return key.replace(".PRIMARY", "_id"); // PRIMARYキーの変換
    }
    return key;
  }

  // 検索不一致
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException ex,
      HttpServletRequest req) {
    Map<String, Object> errors = new HashMap<>();
    errors.put("status", HttpStatus.NOT_FOUND.value());
    errors.put("path", req.getRequestURI());
    errors.put("message", "該当するデータが見つかりません。");
    return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
  }

  // 受講生情報が確認できない
  @ExceptionHandler(NoDataException.class)
  public ResponseEntity<String> handleNoDataException(NoDataException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  // その他の例外
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleOtherException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("サーバーエラーが発生しました。：" + ex.getMessage());
  }
}
