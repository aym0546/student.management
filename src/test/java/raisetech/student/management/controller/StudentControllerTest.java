package raisetech.student.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
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
import raisetech.student.management.exception.NoDataException;
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
  // 日時固定
  LocalDateTime fixedDateTime = LocalDateTime.of(2021, 5, 7, 16, 0, 0);


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
        new StudentsCourse(998L, 999, "Javaコース", fixedDateTime, fixedDateTime.plusYears(1)),
        new StudentsCourse(999L, 999, "AWSコース", fixedDateTime, fixedDateTime.plusYears(1)));
    studentDetail = new StudentDetail(baseStudent, baseCourses);

  }

  @Test
  void 受講生一覧表示が実行でき_空のリストが返ってくること() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).getStudentList();
  }

  @Test
  void 受講生登録が実行でき_登録された受講生情報が返ってくること() throws Exception {

    var objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String request = objectMapper.writeValueAsString(studentDetail);

    when(service.registerStudent(any())).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
          .contentType(MediaType.APPLICATION_JSON)
          .content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.studentId").value("999"))
        .andExpect(jsonPath("$.student.fullName").value("テスト花子"))
        .andExpect(jsonPath("$.student.namePronunciation").value("てすとはなこ"))
        .andExpect(jsonPath("$.student.nickname").value("てすこ"))
        .andExpect(jsonPath("$.student.email").value("test@email"))
        .andExpect(jsonPath("$.student.area").value("テスト区"))
        .andExpect(jsonPath("$.student.age").value("19"))
        .andExpect(jsonPath("$.student.gender").value("Other"))
        .andExpect(jsonPath("$.student.remark").value(""))
        .andExpect(jsonPath("$.studentsCourses[0].attendingId").value("998"))
        .andExpect(jsonPath("$.studentsCourses[0].studentId").value("999"))
        .andExpect(jsonPath("$.studentsCourses[0].course").value("Javaコース"))
        .andExpect(jsonPath("$.studentsCourses[0].startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.studentsCourses[0].deadline").value("2022-05-07T16:00:00"))
        .andExpect(jsonPath("$.studentsCourses[1].attendingId").value("999"))
        .andExpect(jsonPath("$.studentsCourses[1].studentId").value("999"))
        .andExpect(jsonPath("$.studentsCourses[1].course").value("AWSコース"))
        .andExpect(jsonPath("$.studentsCourses[1].startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.studentsCourses[1].deadline").value("2022-05-07T16:00:00"));

    verify(service, times(1)).registerStudent(any());
  }

  @Test
  void 受講生検索が実行でき_受講生情報が返ってくること() throws Exception {
    int studentId = 999;

    var objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    when(service.searchStudent(studentId)).thenReturn(studentDetail);

    // リクエストの送信
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{studentId}", studentId)
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.studentId").value("999"))
        .andExpect(jsonPath("$.student.fullName").value("テスト花子"))
        .andExpect(jsonPath("$.student.namePronunciation").value("てすとはなこ"))
        .andExpect(jsonPath("$.student.nickname").value("てすこ"))
        .andExpect(jsonPath("$.student.email").value("test@email"))
        .andExpect(jsonPath("$.student.area").value("テスト区"))
        .andExpect(jsonPath("$.student.age").value("19"))
        .andExpect(jsonPath("$.student.gender").value("Other"))
        .andExpect(jsonPath("$.student.remark").value(""))
        .andExpect(jsonPath("$.studentsCourses[0].attendingId").value("998"))
        .andExpect(jsonPath("$.studentsCourses[0].studentId").value("999"))
        .andExpect(jsonPath("$.studentsCourses[0].course").value("Javaコース"))
        .andExpect(jsonPath("$.studentsCourses[0].startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.studentsCourses[0].deadline").value("2022-05-07T16:00:00"))
        .andExpect(jsonPath("$.studentsCourses[1].attendingId").value("999"))
        .andExpect(jsonPath("$.studentsCourses[1].studentId").value("999"))
        .andExpect(jsonPath("$.studentsCourses[1].course").value("AWSコース"))
        .andExpect(jsonPath("$.studentsCourses[1].startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.studentsCourses[1].deadline").value("2022-05-07T16:00:00"));

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

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生検索で存在しないstudentIdを指定した時にエラーメッセージが返ること() throws Exception {
    int testStudentId = 1234567890;

    // Service 例外スロー
    Mockito.when(service.searchStudent(testStudentId))
        .thenThrow(new NoDataException("受講生情報が見つかりませんでした。ID: " + testStudentId));

    // Controller エラーハンドリングを検証
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{studentId}", testStudentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("受講生情報が見つかりませんでした。ID: 1234567890"));  // エラーメッセージの確認
  }

}