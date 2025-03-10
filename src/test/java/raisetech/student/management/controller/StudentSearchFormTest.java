package raisetech.student.management.controller;

import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.service.StudentSearchEntity;

class StudentSearchFormTest {

  @Test
  void toEntity_すべての値が正しく変換されること() {
    // 入力データの準備
    var form = new StudentSearchForm("テスト", 25, 30,
        "テスト", "test@email.com", "Other",
        "", 1, "開発系コース", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 7, 1),
        List.of("受講中"));

    StudentSearchEntity entity = form.toEntity();

    // 日付の固定 * 生年月日範囲の計算 *
    var today = LocalDate.now();
    var expectedStartBirthDate = today.minusYears(31).plusDays(1);
    var expectedEndBirthDate = today.minusYears(26).minusDays(1);

    // 検証
    assertThat(entity.name()).isEqualTo("テスト");
    assertThat(entity.area()).isEqualTo("テスト");
    assertThat(entity.email()).isEqualTo("test@email.com");
    assertThat(entity.remark()).isEqualTo("");
    assertThat(entity.courseId()).isEqualTo(1);
    assertThat(entity.category()).isEqualTo("開発系コース");
    assertThat(entity.name()).isEqualTo("テスト");
    assertThat(entity.startDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(entity.endDate()).isEqualTo(LocalDate.of(2025, 7, 1));

    assertThat(entity.startBirthDate()).isEqualTo(expectedStartBirthDate);
    assertThat(entity.endBirthDate()).isEqualTo(expectedEndBirthDate);

    assertThat(entity.status()).containsExactly(Status.受講中);
  }

  @Test
  void toEntity_年齢指定なしの場合_生年月日指定がnullになること() {
    // 入力データの準備
    var form = new StudentSearchForm("テスト", null, null,
        "テスト", "test@email.com", "Other",
        "", 1, "開発系コース", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 7, 1),
        List.of("受講中"));

    var entity = form.toEntity();

    assertThat(entity.startBirthDate()).isNull();
    assertThat(entity.endBirthDate()).isNull();
  }

  @Test
  void toEntity_statusがnullの場合_空リストになること() {
    var form = new StudentSearchForm("ステータスがnullのテスト", 0, 100,
        "テスト", "test@email.com", "Other",
        "", 1, "開発系コース", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 7, 1), null);

    StudentSearchEntity entity = form.toEntity();

    assertThat(entity.status()).isEmpty();
  }

}
