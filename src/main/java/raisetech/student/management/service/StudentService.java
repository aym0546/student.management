package raisetech.student.management.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

//	インターフェースStudentRepositoryを呼び出し
  private StudentRepository repository;

//  コンストラクタに対しDI  ※ StudentServiceクラスにStudentRepositoryインターフェースを注入
  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

//  生徒情報表示のメソッド
  public List<Student> getStudentList() {
    return repository.displayStudent();
  }

//  20代受講生の絞り込み
  public List<Student> get20s() {
    List<Student> students = repository.displayStudent();
    List<Student> students20s = new ArrayList<>();
    for (Student student : students) {
      if (student.getAge() >= 20 && student.getAge() < 30) {
        students20s.add(student);
      }
    }
    return students20s;
  }

//  受講情報表示のメソッド
  public List<StudentsCourse> getStudentCourseList() {
    return repository.displayCourse();
  }

//  Javaコースのコース情報のみを絞り込み
  public List<StudentsCourse> getJavaCourseList() {
    List<StudentsCourse> courses = repository.displayCourse();
    List<StudentsCourse> javaList = new ArrayList<>();
    for (StudentsCourse course : courses) {
      if (course.getCourse().equals("Javaコース")) {
        javaList.add(course);
      }
    }
    return javaList;
  }

}
