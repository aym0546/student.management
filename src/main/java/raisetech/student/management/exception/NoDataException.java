package raisetech.student.management.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoDataException extends RuntimeException {

  public NoDataException(String message) {
    super(message);
  }

}
