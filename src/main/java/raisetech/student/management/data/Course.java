package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "コースマスタ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Course {

  private Integer courseId;
  private CourseName courseName;
  private CourseCategory category;
  private int duration;
  private boolean isClosed;
  private Timestamp createdAt;
  private Timestamp updatedAt;

  public enum CourseName {
    Javaコース, AWSコース, WordPressコース, デザインコース, webマーケティングコース, 映像制作コース, フロントエンドコース
  }

  public enum CourseCategory {
    開発系コース, 制作系コース
  }
}
