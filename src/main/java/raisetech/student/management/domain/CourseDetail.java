package raisetech.student.management.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.StudentsCourse;

@Schema(description = "受講コース詳細情報")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetail {

  @Schema(description = "受講コース情報")
  @Valid
  private StudentsCourse course;

  @Schema(description = "受講ステータス")
  @Valid
  private CourseStatus status;

}
