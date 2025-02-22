package raisetech.student.management.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Schema(description = "コース受講履歴")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentsCoursesStatus {

  @Schema(description = "コース受講履歴ID")
  private Integer statusHistoryId;

  @Schema(description = "コース受講ID")
  private Long attendingId;

  @Schema(description = "ステータスID")
  @NotNull
  private int statusId;

  @Schema(description = "ステータス開始日")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate statusStartDate;

  @Schema(description = "ステータス終了日")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate statusEndDate;

  @Schema(description = "ステータス変更理由")
  @Length(max = 100)
  private String changeReason;

  @Schema(description = "登録日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

  public StudentsCoursesStatus(Integer statusHistoryId, Long attendingId, int statusId,
      LocalDate statusStartDate, LocalDate statusEndDate, String changeReason) {
    this.statusHistoryId = statusHistoryId;
    this.attendingId = attendingId;
    this.statusId = statusId;
    this.statusStartDate = statusStartDate;
    this.statusEndDate = statusEndDate;
    this.changeReason = changeReason;
  }
}
