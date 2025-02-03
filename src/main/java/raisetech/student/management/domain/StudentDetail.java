package raisetech.student.management.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;

@Schema(description = "受講生詳細情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {

  @Schema(description = "受講生情報")
  @Valid
  private Student student;

  @Schema(description = "受講コース情報リスト")
  @Valid
  private List<StudentsCourse> studentsCourses = new ArrayList<>();

}
