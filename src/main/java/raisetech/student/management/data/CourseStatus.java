package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講ステータス")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CourseStatus {

  @Schema(description = "受講ステータスID", example = "100")
  private Integer id;

  @Schema(description = "受講ID", example = "12")
  private Long attendingId;

  @Schema(description = "受講・申し込み状況", example = "仮申し込み/本申し込み/受講中/受講終了")
  private Status status;

  public enum Status {
    仮申し込み, 本申し込み, 受講中, 受講終了
  }

}
