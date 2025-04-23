package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "ステータスマスタ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Status {

  @Schema(description = "ステータスID", example = "1")
  private Integer statusId;

  @Schema(description = "ステータス名", example = "受講中")
  @NotNull
  private String statusName;

  @Schema(description = "表示順", example = "99")
  @NotNull
  private int displayOrder;

  @Schema(description = "進行中")
  private boolean isActive;

  @Schema(description = "最終ステータス")
  private boolean isFinal;

  @Schema(description = "登録日時")
  private Timestamp createdAt;

  @Schema(description = "更新日時")
  private Timestamp updatedAt;

}
