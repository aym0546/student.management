package raisetech.student.management.repository.course;

import jakarta.validation.Valid;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.student.management.data.Course;

/**
 * コースマスタを扱うRepository（インターフェース） コースマスタ情報テーブルと紐づいています。 全件検索や登録・更新・削除が行えるクラスです。
 */
@Mapper
public interface CourseRepository {
  
  /**
   * 【コースマスタの全件取得】
   *
   * @return コースマスタリスト
   */
  List<Course> displayCourseMaster();

  /**
   * 【コースマスタの新規登録】
   *
   * @param course 登録内容（コースマスタ情報）
   */
  int registerCourseMaster(@Valid Course course);

  /**
   * 【コースマスタのID検索】
   *
   * @param courseId 検索するID
   * @return 該当するコースマスタ
   */
  Course searchCourseMaster(Integer courseId);

  /**
   * 【コースマスタの更新】
   *
   * @param course 更新内容（コースマスタ）
   */
  int updateCourseMaster(Course course);

  /**
   * 【コースマスタの削除】
   *
   * @param courseId 削除対象のコースID
   */
  void deleteCourseMaster(@Valid Integer courseId);
}
