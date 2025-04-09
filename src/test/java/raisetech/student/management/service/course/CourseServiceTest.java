package raisetech.student.management.service.course;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.data.Course;
import raisetech.student.management.data.Course.CourseCategory;
import raisetech.student.management.data.Course.CourseName;
import raisetech.student.management.exception.NoDataException;
import raisetech.student.management.repository.course.CourseRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

  @Mock
  private CourseRepository repository;

  private CourseService sut;

  @BeforeEach
  void before() {
    sut = new CourseService(repository);
  }

  @Test
  void コースマスタの全件取得_リポジトリが適切に呼び出せていること() {
    List<Course> expected = new ArrayList<>();
    when(repository.displayCourseMaster()).thenReturn(expected);
    List<Course> actual = sut.getCourseList();
    assertNotNull(actual);
    assertEquals(expected, actual);
  }

  @Test
  void コースマスタの新規作成_リポジトリを適切に呼び出し引数を渡せていること() {
    var master = new Course(CourseName.Javaコース, CourseCategory.開発系コース, 999);
    sut.registerCourseMaster(master);
    verify(repository, times(1)).registerCourseMaster(master);
  }

  @Test
  void コースマスタのID検索_リポジトリを適切に呼び出し引数を渡せていること() {
    sut.getCourseMaster(2);
    verify(repository, times(1)).searchCourseMaster(2);
  }

  @Test
  void コースマスタの削除_リポジトリを適切に呼び出し引数を渡せていること() {
    var courseId = 1;
    var master = new Course(courseId, CourseName.Javaコース, CourseCategory.開発系コース, 999,
        false, Timestamp.valueOf(LocalDateTime.now()),
        Timestamp.valueOf(LocalDateTime.now().plusMonths(6)));
    when(repository.searchCourseMaster(courseId)).thenReturn(master);

    sut.deleteCourseMaster(courseId);

    verify(repository, times(1)).searchCourseMaster(courseId);
    verify(repository, times(1)).deleteCourseMaster(courseId);
  }

  @Test
  void コースマスタの削除_存在しないIDの場合_例外が発生すること() {

    // 存在しないIDを検索するとnullを返す
    Integer courseId = 999;
    when(repository.searchCourseMaster(courseId)).thenReturn(null);

    // 実行によりNoDataExceptionが発生することを確認
    NoDataException exception = assertThrows(
        NoDataException.class, () -> sut.deleteCourseMaster(courseId)
    );
    assertThat(exception.getMessage()).isEqualTo("削除対象が見つかりません。[ID: 999 ]");

    // deleteCourseMasterメソッドが一度も呼ばれていないことを確認
    verify(repository, never()).deleteCourseMaster(any());
  }
}
