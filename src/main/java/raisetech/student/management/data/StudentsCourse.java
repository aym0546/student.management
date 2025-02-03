package raisetech.student.management.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsCourse {

  private Long attendingId;

  @NotBlank
  @Size( min = 12, max = 12 )
  private String studentId;

  @NotBlank
  private String course;

  private LocalDateTime startDate;
  private LocalDateTime deadline;

}
