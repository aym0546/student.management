package raisetech.student.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
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
import raisetech.student.management.data.StudentsCoursesStatus;
import raisetech.student.management.domain.CourseDetail;
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

  @Autowired
  private ObjectMapper objectMapper;

  private Integer studentId;
  private Student student;
  private StudentsCourse course;
  private StudentsCoursesStatus status;
  private List<CourseDetail> courseDetails;
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

    @Bean
    public ObjectMapper objectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModuleを登録
      return objectMapper;
    }
  }

  @BeforeEach
  void before() {
    studentId = 123;
    student = new Student(
        studentId, "ベース テスト", "べーす てすと",
        "ベース", "base@email.com", "テスト区",
        LocalDate.of(2004, 12, 1), "Other", "");

    course = new StudentsCourse(1L, studentId, 1);
    status = new StudentsCoursesStatus(1, 1L, 1, LocalDate.of(2000, 1, 1), null, "新規申し込み");

    courseDetails = List.of(new CourseDetail(course, List.of(status)));
    studentDetail = new StudentDetail(student, courseDetails);
  }

  @Test
  void 受講生一覧表示_成功し空のリストが返ってくること() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).getStudentList();
  }

  @Test
  void 受講生登録_正常な実行に対し200OKと登録された受講生情報が返ってくること() throws Exception {

    when(service.registerStudent(Mockito.any(StudentDetail.class))).thenReturn(studentDetail);

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk())

        .andExpect(jsonPath("$.student.studentId").value(123))
        .andExpect(jsonPath("$.student.fullName").value("ベース テスト"))
        .andExpect(jsonPath("$.student.namePronunciation").value("べーす てすと"))
        .andExpect(jsonPath("$.student.nickname").value("ベース"))
        .andExpect(jsonPath("$.student.email").value("base@email.com"))
        .andExpect(jsonPath("$.student.area").value("テスト区"))
        .andExpect(jsonPath("$.student.birthDate").value("2004-12-01"))
        .andExpect(jsonPath("$.student.gender").value("Other"))
        .andExpect(jsonPath("$.student.remark").value(""))

        .andExpect(jsonPath("$.courseDetails[0].studentsCourse.attendingId").value(1))
        .andExpect(jsonPath("$.courseDetails[0].studentsCourse.studentId").value(123))
        .andExpect(jsonPath("$.courseDetails[0].studentsCourse.courseId").value(1))

        .andExpect(jsonPath("$.courseDetails[0].statusHistory[0].statusHistoryId").value(1))
        .andExpect(jsonPath("$.courseDetails[0].statusHistory[0].attendingId").value(1L))
        .andExpect(jsonPath("$.courseDetails[0].statusHistory[0].statusId").value(1))
        .andExpect(
            jsonPath("$.courseDetails[0].statusHistory[0].statusStartDate").value("2000-01-01"))
        .andExpect(
            jsonPath("$.courseDetails[0].statusHistory[0].changeReason").value("新規申し込み"))
    ;
  }

  @Test
  void 受講生登録_無効なリクエストに対して400エラーが返されること() throws Exception {
    // 無効なリクエストを準備
    StudentDetail invalidDetail = new StudentDetail(null, null);

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidDetail)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 受講生登録_サーバーエラー時に500エラーが返されること() throws Exception {
    when(service.registerStudent(any(StudentDetail.class))).thenThrow(
        new RuntimeException("サーバーエラー"));

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))  // リクエストデータは適切なものを使用
        .andExpect(status().isInternalServerError());
  }

  @Test
  void 受講生検索_正常に実行され200OKと受講生情報が返ってくること() throws Exception {
    when(service.searchStudent(studentId)).thenReturn(studentDetail);

    // リクエストの送信
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{studentId}", studentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())

        .andExpect(jsonPath("$.student.studentId").value(123))
        .andExpect(jsonPath("$.student.fullName").value("ベース テスト"))
        .andExpect(jsonPath("$.student.namePronunciation").value("べーす てすと"))
        .andExpect(jsonPath("$.student.nickname").value("ベース"))
        .andExpect(jsonPath("$.student.email").value("base@email.com"))
        .andExpect(jsonPath("$.student.area").value("テスト区"))
        .andExpect(jsonPath("$.student.birthDate").value("2004-12-01"))
        .andExpect(jsonPath("$.student.gender").value("Other"))
        .andExpect(jsonPath("$.student.remark").value(""))

        .andExpect(jsonPath("$.courseDetails[0].studentsCourse.attendingId").value(1))
        .andExpect(jsonPath("$.courseDetails[0].studentsCourse.studentId").value(123))
        .andExpect(jsonPath("$.courseDetails[0].studentsCourse.courseId").value(1))

        .andExpect(jsonPath("$.courseDetails[0].statusHistory[0].statusHistoryId").value(1))
        .andExpect(jsonPath("$.courseDetails[0].statusHistory[0].attendingId").value(1L))
        .andExpect(jsonPath("$.courseDetails[0].statusHistory[0].statusId").value(1))
        .andExpect(
            jsonPath("$.courseDetails[0].statusHistory[0].statusStartDate").value("2000-01-01"))
        .andExpect(
            jsonPath("$.courseDetails[0].statusHistory[0].changeReason").value("新規申し込み"))
    ;

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
        .andExpect(content().string("ベース テストさんの更新処理が成功しました。"));

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
        .andExpect(
            content().string("受講生情報が見つかりませんでした。ID: 1234567890"));  // エラーメッセージの確認
  }

}