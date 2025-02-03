package raisetech.student.management.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  @NotBlank
  @Pattern(regexp = "^STU\\d{9}$")
  private String studentId;

  @NotBlank
  @Size( min = 1, max = 50 )
  private String fullName;

  @NotBlank
  @Size( min = 1, max = 50 )
  private String namePronunciation;

  @NotBlank
  @Size( min = 1, max = 20 )
  private String nickname;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size( min = 1, max = 100 )
  private String area;

  @NotBlank
  @Min(0)
  @Max(127)
  private short age;

  private String gender;

  @Size( max = 200 )
  private String remark;

  private boolean isDeleted;
}
