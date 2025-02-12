package raisetech.student.management.controller;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

@WebMvcTest(StudentController.class)
@Import(StudentControllerTest.MockConfig.class) // モックBeanを定義したクラスをインポート
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudentService service; // モックBeanを注入

  private Student baseStudent;
  private List<StudentsCourse> baseCourses;
  private StudentDetail studentDetail;

  // テスト用のモックBeanを定義
  @TestConfiguration
  static class MockConfig {
    @Bean
    public StudentService studentService() {
      return Mockito.mock(StudentService.class); // Mockitoでモック化
    }
  }

  @BeforeEach
  void before() {


    // テストデータ作成
    baseStudent = new Student(
        999, "テスト花子", "てすとはなこ", "てすこ", "test@email", "テスト区", (short) 19, "Other",
        "");
    baseCourses = List.of(
        new StudentsCourse(998L, 999, "Javaコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1)),
        new StudentsCourse(999L, 999, "AWSコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1)));
    studentDetail = new StudentDetail(baseStudent, baseCourses);

  }

  private final Validator validator = buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生一覧表示が実行でき_空のリストが返ってくること() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).getStudentList();
  }

  @Test
  void 受講生登録が実行でき_登録された受講生情報が返ってくること() throws Exception {

    Mockito.when(service.registerStudent(Mockito.any())).thenReturn(studentDetail);

    var objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String request = objectMapper.writeValueAsString(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
          .contentType(MediaType.APPLICATION_JSON)
          .content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.nickname").value("てすこ"));

    verify(service, times(1)).registerStudent(Mockito.any());
  }

  @Test
  void 受講生検索が実行でき_受講生情報が返ってくること() throws Exception {
    int studentId = 999;

    Mockito.when(service.searchStudent(studentId)).thenReturn(studentDetail);

    // リクエストの送信
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{studentId}", studentId)
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.nickname").value("てすこ"));

    verify(service, times(1)).searchStudent(studentId);
  }

  @Test
  void 受講生更新が実行でき_実行結果が返ってくること() throws Exception {

    // studentDetailをJSONに変換（日時加工）
    var objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())  // 日付時刻をJSONに変換
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // タイムスタンプ形式を防ぐ
    String request = objectMapper.writeValueAsString(studentDetail);

    // PUT /updateStudent にJSONを送信
    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
          .contentType(MediaType.APPLICATION_JSON)
          .content(request))
        // レスポンスの検証
        .andExpect(status().isOk())
        .andExpect(content().string("テスト花子さんの更新処理が成功しました。"));

    verify(service, times(1)).updateStudent(Mockito.any());
  }

  @Test
  void 入力チェック_正常時はエラーなし_異常時はバリデーションエラーが発生すること() {

    // 正常データ：エラーなし
    Set<ConstraintViolation<Student>> violations = validator.validate(baseStudent);
    assertThat(violations.size()).isEqualTo(0);  // エラーなし

    // 異常値データ：エラー発生
    var testStudent = new Student(
        -1, "", "", "", "non.email", "", (short) -1, "", ""
    );
    Set<ConstraintViolation<Student>> testViolations = validator.validate(testStudent);
    assertThat(testViolations.size()).isEqualTo(13);  // エラー数検証
    for (ConstraintViolation<Student> v : testViolations) {  // エラー項目出力
      System.out.println(v.getPropertyPath() + " :: " + v.getMessage());
    }
  }

}