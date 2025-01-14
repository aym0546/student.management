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
	public String getStudent(@RequestParam String name) {
		Student student = repository.searchByName(name);
		return student.getName() + " " + student.getAge() + "歳";
	}

//	生徒情報の一覧表示
	@GetMapping("/students")
	public List<Student> listDisplay() {
		return repository.display();
	}

//	生徒情報の登録
	@PostMapping("/student")
	public void registerStudent(String name, int age) {
		repository.registerStudent(name, age);
	}

//	生徒情報の変更
	@PatchMapping("/student")
	public void updateStudent(String name, int age) {
		repository.updateStudent(name, age);
	}

//	生徒情報の削除
	@DeleteMapping("/student")
	public void deleteStudent(String name) {
		repository.deleteStudent(name);
	}

}
