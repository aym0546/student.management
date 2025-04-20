package raisetech.student.management.service.course;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.data.Course;
import raisetech.student.management.exception.NoDataException;
import raisetech.student.management.exception.ProcessFailedException;
import raisetech.student.management.repository.course.CourseRepository;

/**
 * コースマスタを取り扱うServiceです。 コースマスタの検索や登録、更新、削除処理を行います。
 */
@Service
public class CourseService {

  //	インターフェースCourseを呼び出し
  private final CourseRepository repository;

  /**
   * コンストラクタ
   *
   * @param repository コースマスタを扱うリポジトリ
   */
  @Autowired
  public CourseService(CourseRepository repository) {
    this.repository = repository;
  }

  /**
   * 【コースマスタの全件取得】
   *
   * @return コースマスタリスト（全件）
   */
  public List<Course> getCourseList() {
    return repository.displayCourseMaster();
  }

  /**
   * 【コースマスタの新規作成】
   *
   * @param course 入力情報（マスタ情報）
   */
  public void registerCourseMaster(Course course) {
    repository.registerCourseMaster(course);
  }

  /**
   * 【コースマスタのID検索】
   *
   * @param courseId 検索ID
   * @return 該当するコースマスタ
   */
  public Course getCourseMaster(Integer courseId) {
    return repository.searchCourseMaster(courseId);
  }

  /**
   * 【コースマスタの更新】
   *
   * @param course 入力された更新情報（コースマスタ）
   */
  public void updateCourseMaster(Integer courseId, Course course) {

    // 事前に対象のコースマスタを検索（なければ例外throw）
    Course courseExist = repository.searchCourseMaster(courseId);
    if (courseExist == null) {
      throw new NoDataException(
          "更新対象のコースマスタが見つかりません。[ID: " + courseId + " ]"
      );
    }

    // リクエストボディのcourseIdをパスパラメータのものに統一
    course.setCourseId(courseId);

    int updateMasterData = repository.updateCourseMaster(course);

    // 更新されなかった場合に例外をスルー
    if (updateMasterData == 0) {
      throw new ProcessFailedException("コースマスタは更新されませんでした。");
    }

  }

  /**
   * [【コースマスタのクローズ】
   *
   * @param courseId 対象コースマスタのID
   */
  @Transactional
  public void updateCourseMasterIsClosed(Integer courseId, Boolean isClosed) {

    // 事前に対象のコースマスタを検索（なければ例外throw）
    Course courseExist = repository.searchCourseMaster(courseId);
    if (courseExist == null) {
      throw new NoDataException("削除対象が見つかりません。[ID: " + courseId + " ]");
    }

    Course update = new Course();
    update.setCourseId(courseId);
    update.setClosed(isClosed);

    int updated = repository.updateCourseMaster(update);
    if (updated == 0) {
      throw new ProcessFailedException("更新が反映されませんでした");
    }
  }
}
