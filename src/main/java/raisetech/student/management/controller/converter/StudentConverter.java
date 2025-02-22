package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;

/**
 * 受講生詳細情報を受講生情報とコース情報、受講ステータス履歴情報に変換、またはその逆の変換を行うConverterです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づくコース受講情報をマッピングする. コース受講情報は受講生に対して複数存在するので、ループを回して受講生詳細情報を組み立てる.
   *
   * @param students      受講生一覧
   * @param courseDetails コース受講履歴情報一覧
   * @return 受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,
      List<CourseDetail> courseDetails) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);

      List<CourseDetail> convertCourseDetails = new ArrayList<>();
      for (CourseDetail courseDetail : courseDetails) {
        if (Objects.equals(student.getStudentId(),
            courseDetail.getStudentsCourse().getStudentId())) {
          convertCourseDetails.add(courseDetail);
        }
      }
      studentDetail.setCourseDetails(convertCourseDetails);

      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }

  /**
   * コース受講情報に紐づく受講ステータス履歴をマッピングする. 受講ステータス履歴はコース受講情報に対して複数存在するので、ループを回して受講履歴情報を組み立てる.
   *
   * @param studentsCourses         コース受講情報一覧
   * @param studentsCoursesStatuses ステータス履歴一覧
   * @return コース受講履歴情報
   */
  public List<CourseDetail> convertCourseDetails(List<StudentsCourse> studentsCourses,
      List<StudentsCoursesStatus> studentsCoursesStatuses) {
    List<CourseDetail> courseDetails = new ArrayList<>();
    studentsCourses.forEach(course -> {
      CourseDetail courseDetail = new CourseDetail();
      courseDetail.setStudentsCourse(course);

      List<StudentsCoursesStatus> convertStudentCoursesStatus = new ArrayList<>();
      for (StudentsCoursesStatus status : studentsCoursesStatuses) {
        if (Objects.equals(status.getAttendingId(), course.getAttendingId())) {
          convertStudentCoursesStatus.add(status);
        }
      }
      courseDetail.setStatusHistory(convertStudentCoursesStatus);

      courseDetails.add(courseDetail);
    });
    return courseDetails;
  }

}
