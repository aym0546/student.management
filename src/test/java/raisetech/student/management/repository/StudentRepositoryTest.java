package raisetech.student.management.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Course;
import raisetech.student.management.data.Course.CourseCategory;
import raisetech.student.management.data.Course.CourseName;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.dto.StudentSearchDTO;

@MybatisTest
class StudentRepositoryTest {

  Student student1 = new Student(1, "山田 太郎", "やまだ たろう", "たろちゃん",
      "taro.yamada@example.com", "東京", LocalDate.of(2000, 1, 1), "Male", "スポーツが好き");
  Student student2 = new Student(2, "鈴木 花子", "すずき はなこ", "はなちゃん",
      "hanako.suzuki@example.com", "大阪", LocalDate.of(2000, 2, 2), "Female", "読書が趣味");
  Student student3 = new Student(3, "佐藤 健", "さとう けん", "けんくん",
      "ken.sato@example.com", "福岡", LocalDate.of(2000, 3, 3), "Male", "");
  Student student4 = new Student(4, "高橋 美咲", "たかはし みさき", "みさみさ",
      "misaki.takahashi@example.com", "北海道", LocalDate.of(2000, 4, 4), "Female",
      "旅行が好き");
  Student student5 = new Student(5, "中村 俊介", "なかむら しゅんすけ", "しゅん",
      "shun.nakamura@example.com", "愛知", LocalDate.of(2000, 5, 5), "Male", "料理が得意");
  private final List<Student> studentList = List.of(student1, student2, student3, student4,
      student5);

  StudentsCourse course1 = new StudentsCourse(1L, 1, 1,
      LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 6, 30, 0, 0));
  StudentsCourse course2 = new StudentsCourse(2L, 2, 2,
      LocalDateTime.of(2025, 2, 15, 0, 0), LocalDateTime.of(2025, 8, 15, 0, 0));
  StudentsCourse course3 = new StudentsCourse(3L, 3, 3,
      LocalDateTime.of(2025, 3, 1, 0, 0), LocalDateTime.of(2025, 9, 1, 0, 0));
  StudentsCourse course4 = new StudentsCourse(4L, 4, 4,
      LocalDateTime.of(2025, 4, 20, 0, 0), LocalDateTime.of(2025, 10, 20, 0, 0));
  StudentsCourse course5 = new StudentsCourse(5L, 5, 5,
      LocalDateTime.of(2025, 5, 5, 0, 0), LocalDateTime.of(2025, 11, 5, 0, 0));
  StudentsCourse course6 = new StudentsCourse(6L, 1, 6,
      LocalDateTime.of(2025, 6, 1, 0, 0), LocalDateTime.of(2025, 12, 1, 0, 0));
  StudentsCourse course7 = new StudentsCourse(7L, 2, 7,
      LocalDateTime.of(2025, 7, 10, 0, 0), LocalDateTime.of(2026, 1, 10, 0, 0));
  private final List<StudentsCourse> studentsCourseList = List.of(course1, course2, course3,
      course4, course5, course6, course7);

  CourseStatus status1 = new CourseStatus(1, 1L, Status.受講終了);
  CourseStatus status2 = new CourseStatus(2, 2L, Status.受講終了);
  CourseStatus status3 = new CourseStatus(3, 3L, Status.受講中);
  CourseStatus status4 = new CourseStatus(4, 4L, Status.受講中);
  CourseStatus status5 = new CourseStatus(5, 5L, Status.本申し込み);
  CourseStatus status6 = new CourseStatus(6, 6L, Status.本申し込み);
  CourseStatus status7 = new CourseStatus(7, 7L, Status.仮申し込み);
  private final List<CourseStatus> courseStatusList = List.of(status1, status2, status3, status4,
      status5, status6, status7);

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
  private StudentRepository sut;

  @Test
  void 受講生の一覧表示が行えること() {
    List<Student> actual = sut.displayStudent();
    assertEquals(actual, studentList);
  }

  @Test
  void コース情報の一覧表示が行えること() {
    List<StudentsCourse> actual = sut.displayCourse();
    assertEquals(actual, studentsCourseList);
  }

  @Test
  void 受講ステータスの一覧表示が行えること() {
    List<CourseStatus> actual = sut.displayStatus();
    assertEquals(actual, courseStatusList);
  }

  @Test
  void 受講生IDから受講生検索が行えること() {
    int studentId = 1;
    Student actual = sut.searchStudent(studentId);
    Student expected = student1;
    assertEquals(actual, expected);
  }

  @Test
  void 受講生IDからコース情報検索が行えること() {
    int studentId = 1;
    List<StudentsCourse> actual = sut.searchStudentsCourses(studentId);
    List<StudentsCourse> expected = List.of(course1, course6);
    assertEquals(actual, expected);
  }

  @Test
  void 受講IDから受講ステータス検索が行えること() {
    Long attendingId = 1L;
    CourseStatus actual = sut.searchCourseStatus(attendingId);
    CourseStatus expected = status1;
    assertEquals(actual, expected);
  }

  @Test
  void 受講生の登録が行えること() {
    var student = new Student(null, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", LocalDate.of(2000, 1, 1), "Female", "");
    int actual = sut.registerStudent(student);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void コース情報の登録が行えること() {
    var course = new StudentsCourse(null, 1, 1, LocalDateTime.of(2000, 1, 1, 0, 0), null);
    int actual = sut.registerStudentsCourses(course);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 受講ステータスの登録が行えること() {
    var status = new CourseStatus(null, 1L, Status.受講終了);
    int actual = sut.registerCourseStatus(status);
    assertThat(actual).isEqualTo(1);
  }

  @Test
  void 受講生情報の更新が行えること() {
    int studentId = 2;
    var student = new Student(studentId, "テスト１子", "てすといちこ", "いちこ", "1@test.email",
        "テスト区", LocalDate.of(2000, 1, 1), "Female", "");
    int updated = sut.updateStudent(student);

    Student actual = sut.searchStudent(studentId);
    assertEquals(1, updated);  // (1) -> 更新成功
    assertEquals(actual, student);  // 前後の比較
  }

  @Test
  void コース情報の更新が行えること() {
    Long attendingId = 2L;
    int studentId = 2;
    var baseCourse = new StudentsCourse(attendingId, studentId, 1,
        LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 6, 30, 0, 0));

    // 実行
    int updated = sut.updateStudentsCourses(baseCourse);

    // 実行後のStudentsCourseを全件取得
    List<StudentsCourse> courseList = sut.displayCourse();

    // 該当するattendingIdのStudentsCourseをactualに設定
    Optional<StudentsCourse> actual = courseList.stream()
        .filter(studentsCourse -> studentsCourse.getAttendingId().equals(attendingId))
        .findFirst();

    assertEquals(1, updated);  // 戻り値が(1) -> 成功
    assertEquals(baseCourse.getCourseId(),
        actual.map(StudentsCourse::getCourseId).orElseThrow());  // コース名が更新されていることを確認
  }

  @Test
  void 受講ステータスの更新が行えること() {
    int id = 1;
    var status = new CourseStatus(id, 1L, Status.仮申し込み);

    int updated = sut.updateCourseStatus(status);
    var actual = sut.searchCourseStatus(1L);

    assertEquals(1, updated);
    assertEquals(status.getStatus(), actual.getStatus());
  }

  @Test
  void 受講生情報の詳細検索_検索条件に一致する受講生が取得できること() {
    // 検索条件
    var condition = new StudentSearchDTO(
        "中村", LocalDate.of(1999, 12, 31), LocalDate.of(2000, 12, 31), null, null, null, null,
        null, null, null, null, List.of());

    var students = sut.findStudent(condition);

    assertThat(students).isNotEmpty();
    assertThat(students.getFirst().getFullName()).contains("中村");
  }

  @Test
  void 受講コース情報の詳細検索_検索条件に一致する受講コースが取得できること() {
    // 検索条件
    var condition = new StudentSearchDTO(
        null, null, null, null, null, null, null,
        1, "開発系コース", null, null, List.of());

    var courses = sut.findCourse(condition);

    assertThat(courses).isNotEmpty();
    assertThat(courses.getFirst().getCourseId()).isEqualTo(1);
  }

  @Test
  void ステータス情報の詳細検索_検索条件に一致するステータスが取得できること() {
    // 検索条件
    var condition = new StudentSearchDTO(
        null, null, null, null, null, null, null,
        null, null, null, null, List.of(Status.受講中));

    var statuses = sut.findStatus(condition);

    assertThat(statuses).isNotEmpty();
    assertThat(statuses.getFirst().getStatus()).isEqualTo(Status.受講中);
  }

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
