package raisetech.student.management.repository.course;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Course;
import raisetech.student.management.data.Course.CourseCategory;
import raisetech.student.management.data.Course.CourseName;

@MybatisTest
class CourseRepositoryTest {

  Course master1 = new Course(1, CourseName.Javaコース, CourseCategory.開発系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  Course master2 = new Course(2, CourseName.AWSコース, CourseCategory.開発系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  Course master3 = new Course(3, CourseName.WordPressコース, CourseCategory.制作系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  Course master4 = new Course(4, CourseName.デザインコース, CourseCategory.制作系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  Course master5 = new Course(5, CourseName.webマーケティングコース, CourseCategory.制作系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  Course master6 = new Course(6, CourseName.映像制作コース, CourseCategory.制作系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  Course master7 = new Course(7, CourseName.フロントエンドコース, CourseCategory.開発系コース, 6,
      false, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
  private final List<Course> masterList = List.of(master1, master2, master3, master4, master5,
      master6, master7);

  @Autowired
  private CourseRepository sut;

  @Test
  void コースマスタの全件取得が行えること() {
    List<Course> actual = sut.displayCourseMaster();
    assertEquals(actual, masterList);
  }

  @Test
  void コースマスタの新規登録が行えること() {
    var now = LocalDateTime.now();
    var master = new Course(null, CourseName.Javaコース, CourseCategory.開発系コース, 999, false,
        Timestamp.valueOf(now), Timestamp.valueOf(now));
    int actual = sut.registerCourseMaster(master);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void コースマスタのID検索が行えること() {
    var actual = sut.searchCourseMaster(1);
    assertThat(actual).isEqualTo(master1);
  }

  @Test
  void コースマスタの更新が行えること() {

    // 更新するコースマスタオブジェクト
    var course = new Course(1, CourseName.Javaコース, CourseCategory.開発系コース, 6, false,
        Timestamp.valueOf("2025-01-01 00:00:00"), Timestamp.valueOf("2025-01-01 00:00:0"));

    // 更新件数が1件であることを確認
    int updated = sut.updateCourseMaster(course);
    assertEquals(1, updated);

    // 更新後にDBから確認
    var updatedCourse = sut.searchCourseMaster(1);
    assertNotNull(updatedCourse);
    assertEquals(course, updatedCourse);

  }

  @Test
  void コースマスタの削除が行えること() {
    // テスト用データを挿入
    var course = new Course(CourseName.Javaコース, CourseCategory.開発系コース, 6);
    sut.registerCourseMaster(course);

    // 挿入後のテストデータ数をチェック
    assertThat(sut.displayCourseMaster().size()).isEqualTo(8);

    // 削除実行
    sut.deleteCourseMaster(8);

    // データ数が1減っていることを確認
    assertThat(sut.displayCourseMaster().size()).isEqualTo(7);
  }

  @Test
  void コースマスタの削除ができ_存在しないIDで削除してもエラーが起きないこと() {

    // 存在しないIDで実行
    sut.deleteCourseMaster(999);

    // 他のデータが影響を受けないことを確認
    assertThat(sut.displayCourseMaster().size()).isEqualTo(7);

  }

}
