package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;

/**
 * 受講生詳細を受講生情報とコース情報に変換、またはその逆の変換を行うConverterです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づくコース情報をマッピングする。 コース情報は受講生に対して複数存在するので、ループを回して受講生詳細情報を組み立てる。
   *
   * @param students      受講生一覧
   * @param courseDetails コース情報状況一覧
   * @return 受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<CourseDetail> courseDetails) {
    List<StudentDetail> studentDetails = new ArrayList<>();

    for (Student student : students) {
      // student が null の場合はスキップ
      if (Objects.isNull(student)) {
        continue;
      }

      // studentIdに紐づくcourseDetailsフィルタリング
      List<CourseDetail> convertCourseDetails = courseDetails.stream()
          .filter(courseDetail -> student.getStudentId()
              .equals(courseDetail.getCourse().getStudentId()))
          .toList();

      // コース情報がない場合は追加しない
      if (!convertCourseDetails.isEmpty()) {
        studentDetails.add(new StudentDetail(student, convertCourseDetails));
      }
    }
    return studentDetails;
  }

  /**
   * コース受講情報に紐づく受講状況・申し込み状況をマッピングする。
   *
   * @param courses  コース受講情報一覧
   * @param statuses 受講状況・申し込み状況一覧
   * @return コース受講状況一覧
   */
  public List<CourseDetail> convertCourseDetails(List<StudentsCourse> courses,
      List<CourseStatus> statuses) {
    List<CourseDetail> courseDetails = new ArrayList<>();

    for (StudentsCourse course : courses) {
      // course == null の場合はスキップ
      if (course == null) {
        continue;
      }

      var courseDetail = new CourseDetail();
      courseDetail.setCourse(course);

      for (CourseStatus status : statuses) {
        if (status != null && Objects.equals(course.getAttendingId(), status.getAttendingId())) {
          courseDetail.setStatus(status);
          break;
        }
      }

      // CourseStatusがnullでない場合のみ追加
      if (courseDetail.getStatus() != null) {
        courseDetails.add(courseDetail);
      }
    }
    return courseDetails;
  }

}
