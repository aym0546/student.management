package raisetech.student.management.controller.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
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
import raisetech.student.management.data.Course;
import raisetech.student.management.data.Course.CourseCategory;
import raisetech.student.management.exception.ProcessFailedException;
import raisetech.student.management.service.course.CourseService;

@WebMvcTest(CourseController.class)
@Import(CourseControllerTest.MockConfig.class) // モックBeanを定義したクラスをインポート
class CourseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CourseService service; // モックBeanを注入

  @Autowired
  private ObjectMapper objectMapper;

  private Course course;
  private final int courseId = 1000;
  private Map<String, Boolean> requestBody;

  // テスト用のモックBeanを定義
  @TestConfiguration
  static class MockConfig {

    @Bean
    public CourseService courseService() {
      return Mockito.mock(CourseService.class); // Mockitoでモック化
    }
  }

  @BeforeAll
  static void setupTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @BeforeEach
  void beforeEach() {

    course = new Course(
        1, "Javaコース（Test）", CourseCategory.開発系コース, 6, false,
        Timestamp.valueOf("2021-05-07 16:00:00"), Timestamp.valueOf("2021-05-07 16:00:00"));

    Mockito.reset(service); // モックをリセット

  }

  @Test
  void コースマスタ全件取得_正常完了_200OKとマスタリストが返ってくること()
      throws Exception {
    when(service.getCourseList()).thenReturn(List.of(course));

    // リクエストを想定
    mockMvc.perform(MockMvcRequestBuilders.get("/courses")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())  // ステータスコード200
        .andExpect(
            content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].courseId").value(1))
        .andExpect(jsonPath("$[0].courseName").value("Javaコース（Test）"))
        .andExpect(jsonPath("$[0].category").value("開発系コース"))
        .andExpect(jsonPath("$[0].createdAt").value("2021-05-07T16:00:00.000+00:00"))
        .andExpect(jsonPath("$[0].updatedAt").value("2021-05-07T16:00:00.000+00:00"))
    ;

    // serviceメソッドが呼ばれたことを確認
    verify(service, times(1)).getCourseList();
  }

  @Test
  void コースマスタ全件取得_コースマスタが存在しない場合_404NotFoundが返ること()
      throws Exception {
    when(service.getCourseList()).thenReturn(List.of());

    mockMvc.perform(MockMvcRequestBuilders.get("/courses")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(service, times(1)).getCourseList();
  }

  @Test
  void コースマスタ登録_正常完了_201Createdと登録された受講生情報が返ってくること()
      throws Exception {

    objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String request = objectMapper.writeValueAsString(course);

    when(service.getCourseMaster(anyInt())).thenReturn(course);

    mockMvc.perform(MockMvcRequestBuilders.post("/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.courseId").value("1"))
        .andExpect(jsonPath("$.courseName").value("Javaコース（Test）"))
        .andExpect(jsonPath("$.duration").value("6"))
        .andExpect(jsonPath("$.closed").value("false"))
        .andExpect(jsonPath("$.createdAt").value("2021-05-07T16:00:00.000+00:00"))
        .andExpect(jsonPath("$.updatedAt").value("2021-05-07T16:00:00.000+00:00"))
    ;

    verify(service, times(1)).registerCourseMaster(any());
    verify(service, times(1)).getCourseMaster(anyInt());
  }

  @Test
  void コースマスタ更新_正常完了_200OKと実行結果が返ってくること() throws Exception {

    objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    String request = objectMapper.writeValueAsString(course);

    mockMvc.perform(MockMvcRequestBuilders.put("/courses/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isOk())
        .andExpect(content().string("コース名：【 Javaコース（Test） 】の更新処理が成功しました。"));

    verify(service, times(1)).updateCourseMaster(eq(1), any());
  }

  @Test
  void コースマスタのクローズ_正常完了_204NoContentが返ってくること()
      throws Exception {
    requestBody = Map.of("closed", true);

    mockMvc.perform(MockMvcRequestBuilders.patch("/courses/{courseId}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isNoContent());
  }

  @Test
  void コースマスタのクローズ_リクエストにclosedキーが足りない時_400が返ってくること()
      throws Exception {
    requestBody = new HashMap<>();

    mockMvc.perform(MockMvcRequestBuilders.patch("/courses/{courseId}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void コースマスタのクローズ_サービス層で例外がスローされた場合_500を返すこと() throws Exception {
    requestBody = Map.of("closed", true);

    doThrow(new ProcessFailedException("サーバーエラー"))
        .when(service).updateCourseMasterIsClosed(courseId, true);

    mockMvc.perform(MockMvcRequestBuilders.patch("/courses/{courseId}", courseId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isInternalServerError());
  }

}
