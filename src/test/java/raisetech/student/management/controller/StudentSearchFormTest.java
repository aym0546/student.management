package raisetech.student.management.controller;

import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.dto.StudentSearchDTO;

class StudentSearchFormTest {

  @Test
  void DTOへの変換メソッド_すべての値が正しく変換されること() {
    // 入力データの準備
    var form = new StudentSearchForm("テスト", 25, 30,
        "テスト", "test@email.com", "Other",
        "", 1, "開発系コース", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 7, 1),
        List.of("受講中"));

    StudentSearchDTO actual = form.toDTO();

    // 日付の固定 * 生年月日範囲の計算 *
    var today = LocalDate.now();
    var expectedStartBirthDate = today.minusYears(31).plusDays(1);
    var expectedEndBirthDate = today.minusYears(26).minusDays(1);

    var expected = new StudentSearchDTO("テスト", expectedStartBirthDate, expectedEndBirthDate,
        "テスト", "test@email.com", "Other", "", 1, "開発系コース", LocalDate.of(2025, 1, 1),
        LocalDate.of(2025, 7, 1), List.of(Status.受講中));

    // 検証
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void DTOへの変換メソッド_年齢指定なしの場合_生年月日指定がNullになること() {
    // 入力データの準備
    var form = new StudentSearchForm("テスト", null, null,
        "テスト", "test@email.com", "Other",
        "", 1, "開発系コース", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 7, 1),
        List.of("受講中"));

    var dto = form.toDTO();

    assertThat(dto.startBirthDate()).isNull();
    assertThat(dto.endBirthDate()).isNull();
  }

  @Test
  void DTOへの変換メソッド_statusがNullの場合_空リストになること() {
    var form = new StudentSearchForm("ステータスがnullのテスト", 0, 100,
        "テスト", "test@email.com", "Other",
        "", 1, "開発系コース", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 7, 1), null);

    StudentSearchDTO dto = form.toDTO();

    assertThat(dto.status()).isEmpty();
  }

}
