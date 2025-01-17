package raisetech.student.management.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsCourse {
  private String studentId;
  private String course;
  private LocalDateTime startDate;
  private LocalDateTime deadline;
}
