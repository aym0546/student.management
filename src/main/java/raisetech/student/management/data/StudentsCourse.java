package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

  @Schema(description = "受講コースID", example = "19")
  @NotNull(groups = Update.class, message = "更新時には受講コースIDが必須です。")
  private Long attendingId;

  @Schema(description = "受講生ID", example = "21")
  private Integer studentId;

  @Schema(description = "コースID", example = "1 = Javaコース")
  @NotNull
  private Integer courseId;

  @Schema(description = "登録日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

  public StudentsCourse(Long attendingId, Integer studentId, Integer courseId) {
    this.attendingId = attendingId;
    this.studentId = studentId;
    this.courseId = courseId;
  }
}
