package raisetech.student.management.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Schema(description = "受講生情報")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Student {

  @Schema(description = "受講生ID", example = "12")
  private Integer studentId;

  @Schema(description = "受講生氏名", example = "山田 太郎")
  @NotBlank
  @Length(min = 1, max = 50)
  private String fullName;

  @Schema(description = "氏名ふりがな", example = "やまだ たろう")
  @NotBlank
  @Length(min = 1, max = 50)
  private String namePronunciation;

  @Schema(description = "ニックネーム", example = "たろ")
  @NotBlank
  @Length(min = 1, max = 20)
  private String nickname;

  @Schema(description = "Emailアドレス")
  @NotBlank
  @Email
  private String email;

  @Schema(description = "居住地域", example = "神奈川県横浜市")
  @NotBlank
  @Length(min = 1, max = 100)
  private String area;

  @Schema(description = "生年月日")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthDate;

  @Schema(description = "性別", example = "Female")
  @NotBlank
  @Pattern(regexp = "Male|Female|Other", message = "Male, Female, Other のいずれかを入力してください。")
  private String gender;

  @Schema(description = "備考情報")
  @Length(max = 200)
  private String remark;

  @Schema(description = "キャンセルフラグ")
  private boolean isDeleted;

  @Schema(description = "登録日時")
  private LocalDateTime createdAt;

  @Schema(description = "更新日時")
  private LocalDateTime updatedAt;

  public Student(Integer studentId, String fullName, String namePronunciation, String nickname,
      String email, String area, LocalDate birthDate, String gender, String remark) {
    this.studentId = studentId;
    this.fullName = fullName;
    this.namePronunciation = namePronunciation;
    this.nickname = nickname;
    this.email = email;
    this.area = area;
    this.birthDate = birthDate;
    this.gender = gender;
    this.remark = remark;
  }

}
