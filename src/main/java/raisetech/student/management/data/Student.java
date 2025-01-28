package raisetech.student.management.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
  private String studentId;
  private String fullName;
  private String namePronunciation;
  private String nickname;
  private String email;
  private String area;
  private short age;
  private String gender;
  private String remark;
  private boolean isDeleted;
}
