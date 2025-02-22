package raisetech.student.management.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;

@Schema(description = "受講状況履歴")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetail {

  @Schema(description = "受講コース情報")
  @Valid
  private StudentsCourse studentsCourse;

  @Schema(description = "受講状況履歴")
  @Valid
  private List<StudentsCoursesStatus> statusHistory;

}
