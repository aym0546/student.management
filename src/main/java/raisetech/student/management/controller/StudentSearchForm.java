package raisetech.student.management.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.dto.StudentSearchDTO;

@Schema(description = "検索フォーム")
public record StudentSearchForm(

    @Schema(description = "氏名・ふりがな・ニックネームの検索条件", example = "たろ")
    String name,

    @Schema(description = "検索する最少年齢", example = "25")
    Integer minAge,

    @Schema(description = "検索する最長年齢", example = "30")
    Integer maxAge,

    @Schema(description = "検索する地域(部分一致)", example = "東京")
    String area,

    @Schema(description = "検索するメールアドレス", example = "sample@test.com")
    String email,

    @Schema(description = "検索する性別", example = "Female")
    String gender,

    @Schema(description = "備考欄の検索条件(部分一致)", example = "料理")
    String remark,

    @Schema(description = "検索するコースID", example = "1 : Javaコース")
    Integer courseId,

    @Schema(description = "検索するコースカテゴリ", example = "開発系コース")
    String category,

    @Schema(description = "検索する受講開始日", example = "2000-01-01")
    LocalDate startDate,

    @Schema(description = "検索する受講終了日", example = "2000-01-01")
    LocalDate endDate,

    @Schema(description = "検索する受講ステータス（リスト）", example = "[仮申し込み, 受講中]")
    List<String> status

) {

  public StudentSearchDTO toDTO() {
    // 年齢指定をbirthDateに置き換え
    var today = LocalDate.now();
    var startBirthDate =
        (maxAge() != null) ? today.minusYears(maxAge() + 1).plusDays(1)
            : null;
    var endBirthDate =
        (minAge() != null) ? today.minusYears(minAge() + 1).minusDays(1)
            : null;

    // statusについて、List<String>からList<Status>に変換
    var statusDTOList = Optional.ofNullable(status())
        .map(statusList -> statusList.stream().map(Status::valueOf).toList())
        .orElse(List.of());  // statusがnullの時は空リストList.of()を返す

    // リクエストデータをStudentSearchFormからStudentSearchDTOに詰め替え
    return new StudentSearchDTO(
        name(), startBirthDate, endBirthDate, area(), email(), gender(), remark(), courseId(),
        category(),
        startDate(), endDate(), statusDTOList);

  }

}
