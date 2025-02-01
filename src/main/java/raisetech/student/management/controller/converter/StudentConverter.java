package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.StudentDetail;

/**
 * 受講生詳細を受講生情報とコース情報に変換、またはその逆の変換を行うConverterです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づくコース情報をマッピングする。
   * コース情報は受講生に対して複数存在するので、ループを回して受講生詳細情報を組み立てる。
   * @param students 受講生一覧
   * @param studentsCourses コース情報一覧
   * @return 受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> students,List<StudentsCourse> studentsCourses) {
    List<StudentDetail> studentDetails = new ArrayList<>();
    students.forEach(student -> {
      StudentDetail studentDetail = new StudentDetail();
      studentDetail.setStudent(student);
      List<StudentsCourse> convertStudentCourses = studentsCourses.stream()
          .filter(studentsCourse -> student.getStudentId().equals(studentsCourse.getStudentId()))
          .collect(Collectors.toList());
      studentDetail.setStudentsCourses(convertStudentCourses);
      studentDetails.add(studentDetail);
    });
    return studentDetails;
  }

}
