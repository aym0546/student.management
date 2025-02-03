package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生情報")
@Getter
@Setter
public class Student {

  @Schema(description = "受講生ID", example = "STU000000001")
  @Pattern(regexp = "^STU\\d{9}$")
  private String studentId;

  @Schema(description = "受講生氏名", example = "山田 太郎")
  @Size( min = 1, max = 50 )
  private String fullName;

  @Schema(description = "氏名ふりがな", example = "やまだ たろう")
  @Size( min = 1, max = 50 )
  private String namePronunciation;

  @Schema(description = "ニックネーム", example = "たろ")
  @Size( min = 1, max = 20 )
  private String nickname;

  @Schema(description = "Emailアドレス")
  @Email
  private String email;

  @Schema(description = "居住地域", example = "神奈川県横浜市")
  @Size( min = 1, max = 100 )
  private String area;

  @Schema(description = "年齢")
  @Min(0)
  @Max(127)
  private short age;

  @Schema(description = "性別", example = "Female")
  private String gender;

  @Schema(description = "備考情報")
  @Size( max = 200 )
  private String remark;

  @Schema(description = "キャンセルフラグ")
  private boolean isDeleted;
}
