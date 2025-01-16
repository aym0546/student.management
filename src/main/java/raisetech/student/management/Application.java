package raisetech.student.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

//	インターフェースStudentRepositoryを呼び出し
	@Autowired
	private StudentRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	情報取得メソッド
	@GetMapping("/student")
	public List<Student> getStudentList(@RequestParam String name) {
		return repository.searchByName(name);
	}

//	生徒情報の一覧表示
	@GetMapping("/students")
	public List<Student> listDisplay() {
		return repository.displayStudent();
	}

//	コース情報の一覧表示
	@GetMapping("/students_courses")
	public List<StudentsCourse> getCousesList() {
		return repository.displayCourse();
	}

}
