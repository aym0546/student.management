package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "コースマスタ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

  @Schema(description = "コースID", example = "1")
  private Integer courseId;

  @Schema(description = "コース名", example = "Javaコース")
  @NotNull
  private CourseName courseName;

  @Schema(description = "コースカテゴリ", example = "開発系コース")
  @NotNull
  private CourseCategory category;

  @Schema(description = "コース開講期間（月単位）", example = "6")
  @NotNull
  private int duration;

  @Schema(description = "開講状況")
  private boolean isClosed;

  @Schema(description = "登録日時")
  private Timestamp createdAt;

  @Schema(description = "更新日時")
  private Timestamp updatedAt;

  public enum CourseName {
    Javaコース, AWSコース, WordPressコース, デザインコース, webマーケティングコース, 映像制作コース, フロントエンドコース
  }

  public enum CourseCategory {
    開発系コース, 制作系コース
  }

  public Course(CourseName courseName, CourseCategory category, int duration) {
    this.courseName = courseName;
    this.category = category;
    this.duration = duration;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Course course)) {
      return false;
    }
    return duration == course.duration && Objects.equals(courseId, course.courseId)
        && courseName == course.courseName && category == course.category &&
        Objects.equals(isClosed, course.isClosed);
  }
}
