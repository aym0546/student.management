package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Update;

@Schema(description = "受講コース情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudentsCourse {

  @Schema(description = "受講ID", example = "19")
  @NotNull(groups = Update.class, message = "更新時には受講IDが必須です。")
  private Long attendingId;

  @Schema(description = "受講生ID", example = "21")
  @Positive
  private Integer studentId;

  @Schema(description = "受講コースID", example = "1")
  @NotNull
  private int courseId;

  @Schema(description = "コース受講開始日", example = "2025-01-01")
  private LocalDateTime startDate;

  @Schema(description = "コース受講終了日", example = "2025-06-30")
  private LocalDateTime endDate;
}
