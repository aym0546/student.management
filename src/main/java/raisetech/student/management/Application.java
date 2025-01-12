package raisetech.student.management;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

//	生徒情報のマップstudent<名前,年齢>作成
	private Map<String, String> student = new HashMap<>();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	// 情報取得メソッド（全員）
	@GetMapping("/studentInfo")
	public Map<String, String> getStudentInfo() {
		return student;
	}

//	生徒情報のセット
	@PostMapping("/studentInfo")
	public void setStudentInfo(String name, String age) {
		student.put(name, age);
	}

//	生徒の名前をキーに年齢を表示
	@PostMapping("/studentAge")
	public String getStudentAge(String name) {
		return student.get(name) + "歳";
	}

}
