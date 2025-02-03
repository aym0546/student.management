package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講コース情報")
@Getter
@Setter
public class StudentsCourse {

  @Schema(description = "受講コースID", example = "19")
  private Long attendingId;

  @Schema(description = "受講生ID", example = "STU000000021")
  @Pattern(regexp = "^STU\\d{9}$")
  private String studentId;

  @Schema(description = "受講コース名", example = "Javaコース")
  @NotBlank
  private String course;

  @Schema(description = "コース受講開始日", example = "2025-01-01")
  private LocalDateTime startDate;

  @Schema(description = "コース受講期限日", example = "2025-06-30")
  private LocalDateTime deadline;

}
