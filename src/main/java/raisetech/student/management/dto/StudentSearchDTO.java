package raisetech.student.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "検索条件オブジェクト")
public record StudentSearchDTO(

    @Schema(description = "氏名・ふりがな・ニックネームの検索条件", example = "たろ")
    String name,

    @Schema(description = "検索する生年月日期間の開始日", example = "2000-01-01")
    LocalDate startBirthDate,

    @Schema(description = "検索する生年月日期間の最終日", example = "2000-01-01")
    LocalDate endBirthDate,

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

    @Schema(description = "検索する受講ステータスID（リスト）", example = "[5, 7]（ = 受講中とキャンセル）")
    List<Integer> statusId

) {

}
