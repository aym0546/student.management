package raisetech.student.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
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

  private Student student;
  private CourseDetail courseDetail1;
  private CourseDetail courseDetail2;
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
    student = new Student(
        999, "テスト花子", "てすとはなこ", "てすこ", "test@email", "テスト区",
        LocalDate.of(2000, 1, 1), "Other", "");

    courseDetail1 = new CourseDetail(
        new StudentsCourse(998L, 999, 1, fixedDateTime, fixedDateTime.plusYears(1)),
        new CourseStatus(1, 998L, Status.受講終了)
    );
    courseDetail2 = new CourseDetail(
        new StudentsCourse(999L, 999, 2, fixedDateTime, fixedDateTime.plusYears(1)),
        new CourseStatus(2, 999L, Status.受講中)
    );

    studentDetail = new StudentDetail(student, List.of(courseDetail1, courseDetail2));
  }

  @Test
  void 受講生一覧表示が実行でき_空のリストが返ってくること() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/students"))
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

    mockMvc.perform(MockMvcRequestBuilders.post("/students")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.student.studentId").value("999"))
        .andExpect(jsonPath("$.student.fullName").value("テスト花子"))
        .andExpect(jsonPath("$.student.namePronunciation").value("てすとはなこ"))
        .andExpect(jsonPath("$.student.nickname").value("てすこ"))
        .andExpect(jsonPath("$.student.email").value("test@email"))
        .andExpect(jsonPath("$.student.area").value("テスト区"))
        .andExpect(jsonPath("$.student.birthDate").value("2000-01-01"))
        .andExpect(jsonPath("$.student.gender").value("Other"))
        .andExpect(jsonPath("$.student.remark").value(""))
        .andExpect(jsonPath("$.courseDetailList[0].course.attendingId").value("998"))
        .andExpect(jsonPath("$.courseDetailList[0].course.studentId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[0].course.courseId").value("1"))
        .andExpect(jsonPath("$.courseDetailList[0].course.startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[0].course.endDate").value("2022-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[0].status.id").value("1"))
        .andExpect(jsonPath("$.courseDetailList[0].status.attendingId").value("998"))
        .andExpect(jsonPath("$.courseDetailList[0].status.status").value("受講終了"))
        .andExpect(jsonPath("$.courseDetailList[1].course.attendingId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[1].course.studentId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[1].course.courseId").value("2"))
        .andExpect(jsonPath("$.courseDetailList[1].course.startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[1].course.endDate").value("2022-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[1].status.id").value("2"))
        .andExpect(jsonPath("$.courseDetailList[1].status.attendingId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[1].status.status").value("受講中"));

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
    mockMvc.perform(MockMvcRequestBuilders.get("/students/{studentId}", studentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.studentId").value("999"))
        .andExpect(jsonPath("$.student.fullName").value("テスト花子"))
        .andExpect(jsonPath("$.student.namePronunciation").value("てすとはなこ"))
        .andExpect(jsonPath("$.student.nickname").value("てすこ"))
        .andExpect(jsonPath("$.student.email").value("test@email"))
        .andExpect(jsonPath("$.student.area").value("テスト区"))
        .andExpect(jsonPath("$.student.birthDate").value("2000-01-01"))
        .andExpect(jsonPath("$.student.gender").value("Other"))
        .andExpect(jsonPath("$.student.remark").value(""))
        .andExpect(jsonPath("$.courseDetailList[0].course.attendingId").value("998"))
        .andExpect(jsonPath("$.courseDetailList[0].course.studentId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[0].course.courseId").value("1"))
        .andExpect(jsonPath("$.courseDetailList[0].course.startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[0].course.endDate").value("2022-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[0].status.id").value("1"))
        .andExpect(jsonPath("$.courseDetailList[0].status.attendingId").value("998"))
        .andExpect(jsonPath("$.courseDetailList[0].status.status").value("受講終了"))
        .andExpect(jsonPath("$.courseDetailList[1].course.attendingId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[1].course.studentId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[1].course.courseId").value("2"))
        .andExpect(jsonPath("$.courseDetailList[1].course.startDate").value("2021-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[1].course.endDate").value("2022-05-07T16:00:00"))
        .andExpect(jsonPath("$.courseDetailList[1].status.id").value("2"))
        .andExpect(jsonPath("$.courseDetailList[1].status.attendingId").value("999"))
        .andExpect(jsonPath("$.courseDetailList[1].status.status").value("受講中"));

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
    mockMvc.perform(MockMvcRequestBuilders.put("/students")
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
    mockMvc.perform(MockMvcRequestBuilders.get("/students/{studentId}", testStudentId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            content().string("受講生情報が見つかりませんでした。ID: 1234567890"));  // エラーメッセージの確認
  }

}
