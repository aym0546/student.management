package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Update;

@Schema(description = "受講コース情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentsCourse {

  @Schema(description = "受講コースID", example = "19")
  @NotNull(groups = Update.class, message = "更新時には受講コースIDが必須です。")
  private Long attendingId;

  @Schema(description = "受講生ID", example = "21")
  @Positive
  private Integer studentId;

  @Schema(description = "受講コース名", example = "Javaコース")
  @NotBlank
  @Pattern(
      regexp = "Javaコース|AWSコース|WordPressコース|デザインコース|webマーケティングコース|映像制作コース|フロントエンドコース",
      message = "Javaコース|AWSコース|WordPressコース|デザインコース|webマーケティングコース|映像制作コース|フロントエンドコース のいずれかを入力してください。"
  )
  private String course;

  @Schema(description = "コース受講開始日", example = "2025-01-01")
  private LocalDateTime startDate;

  @Schema(description = "コース受講期限日", example = "2025-06-30")
  private LocalDateTime deadline;

}
