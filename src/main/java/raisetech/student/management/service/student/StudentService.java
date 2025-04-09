package raisetech.student.management.service.student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.converter.student.StudentConverter;
import raisetech.student.management.data.Course;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.dto.StudentSearchDTO;
import raisetech.student.management.exception.NoDataException;
import raisetech.student.management.exception.ProcessFailedException;
import raisetech.student.management.repository.course.CourseRepository;
import raisetech.student.management.repository.student.StudentRepository;

/**
 * 受講生情報を取り扱うServiceです。 受講生の検索や登録、更新処理を行います。
 */
@Service
public class StudentService {

  //	インターフェースStudentRepository、StudentConverter、CourseRepositoryを呼び出し
  private final StudentRepository studentRepository;
  private final StudentConverter converter;
  private final CourseRepository courseRepository;

  /**
   * コンストラクタ
   *
   * @param studentRepository 受講生を扱うリポジトリ
   * @param converter         受講生詳細と受講生情報・コース情報のコンバーター
   * @param courseRepository  コースマスタを扱うリポジトリ
   */
  @Autowired
  public StudentService(StudentRepository studentRepository, StudentConverter converter,
      CourseRepository courseRepository) {
    this.studentRepository = studentRepository;
    this.converter = converter;
    this.courseRepository = courseRepository;
  }

  /**
   * 【詳細情報検索】 リクエストに含まれるデータに基づいて検索を行う。
   *
   * @param searchDTO リクエストに含まれる検索データ
   * @return 該当する受講生詳細情報のリスト
   */
  public List<StudentDetail> getStudentList(StudentSearchDTO searchDTO) {

    List<Student> studentList = studentRepository.findStudent(searchDTO);
    List<StudentsCourse> studentsCourses = studentRepository.findCourse(searchDTO);
    List<CourseStatus> courseStatuses = studentRepository.findStatus(searchDTO);

    return converter.convertStudentDetails(studentList,
        converter.convertCourseDetails(studentsCourses, courseStatuses));
  }

  /**
   * 【受講生検索】 IDに紐づく受講生情報を取得し、次いでその受講生に紐づく受講生コース情報を取得して設定。
   *
   * @param studentId 受講生ID
   * @return 受講生詳細情報
   */
  public StudentDetail searchStudent(Integer studentId) {
    // 受け取ったstudentIDを元に受講生情報を検索
    Student student = studentRepository.searchStudent(studentId);
    // nullチェック
    if (Objects.isNull(student)) {
      throw new NoDataException("該当する受講生が見つかりません。ID：" + studentId);
    }
    // 受講生情報のstudentIDに基づいてコース情報を検索（nullの場合は空リスト）
    List<StudentsCourse> studentsCourses = Optional.ofNullable(
            studentRepository.searchStudentsCourses(student.getStudentId()))
        .orElse(Collections.emptyList());
    // コース情報のattendingIDに基づいて受講状況を検索、受講状況と紐付け
    List<CourseDetail> courseDetails = new ArrayList<>();  // 空の受講コース状況リストを作成
    for (StudentsCourse course : studentsCourses) {
      courseDetails.add(
          new CourseDetail(course, studentRepository.searchCourseStatus(course.getAttendingId())));
    }

    // ↑の受講生情報・コース情報を持つnew StudentDetailを生成してreturn
    return new StudentDetail(student, courseDetails);
  }

  /**
   * 【受講生情報の登録】 新規受講生の受講生情報と受講コース情報を登録。 StudentIDで2つの情報を紐付け。
   * attendingID,StartDate,DeadLineは自動設定されます。
   *
   * @param studentDetail 入力情報（受講生詳細情報）
   * @return 登録された受講生詳細情報
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    // 受講生情報の登録
    int registeredStudent = studentRepository.registerStudent(studentDetail.getStudent());
    // 受講生登録が行われなかった場合にexをthrow -> 処理中断
    if (registeredStudent == 0) {
      throw new ProcessFailedException("受講生の登録に失敗しました。");
    }

    // コース情報・ステータス情報の登録
    int registeredCourse = 0;
    int registeredStatus = 0;

    // 現在日時を取得
    LocalDateTime now = LocalDateTime.now();

    // studentDetailのcourseDetailの数だけコース情報・ステータス情報を登録する
    for (CourseDetail courseDetail : studentDetail.getCourseDetailList()) {
      // コース情報の登録
      Optional.ofNullable(courseDetail.getCourse()).ifPresent(
          course -> {
            // courseのstudent_idは↑で自動生成・登録されたものを使用
            course.setStudentId(studentDetail.getStudent().getStudentId());
            // start_dateは現在日時に設定
            course.setStartDate(now);
            // courseIdに対応するdurationを取得し、end_dateを計算
            int duration = courseRepository.displayCourseMaster()
                .stream()
                .filter(c -> c.getCourseId() == course.getCourseId())
                .map(Course::getDuration)
                .findFirst()
                .orElse(0);
            course.setEndDate(now.plusMonths(duration));
          }
      );
      registeredCourse += studentRepository.registerStudentsCourses(courseDetail.getCourse());

      // コース情報の登録が行われなかった場合に例外をthrow -> 処理中断
      if (registeredCourse == 0) {
        throw new ProcessFailedException("受講コース情報の登録に失敗しました。");
      }

      // ステータス情報の登録
      Optional.ofNullable(courseDetail.getStatus()).ifPresent(
          status ->
              status.setAttendingId(courseDetail.getCourse().getAttendingId())
      );
      registeredStatus += studentRepository.registerCourseStatus(courseDetail.getStatus());
    }

    // ステータス情報の登録が行われなかった場合に例外をthrow -> 処理中断
    if (registeredStatus == 0) {
      throw new ProcessFailedException("受講ステータス情報の登録に失敗しました。");
    }

    return studentDetail;
  }

  /**
   * 【受講生更新】 指定されたIDの受講生情報を更新。
   *
   * @param studentDetail 入力された更新情報（受講生詳細）
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    // ここに遷移した時点で既に特定のstudentIdのstudentDetailが呼び出されている

    // 事前に対象の受講生情報を検索（なければ例外throw）
    Integer targetStudentId = studentDetail.getStudent().getStudentId();
    Student studentExist = studentRepository.searchStudent(targetStudentId);
    if (studentExist == null) {
      throw new NoDataException(
          "更新対象の受講生情報が見つかりません。[ID: " + targetStudentId + " ]");
    }

    // 事前に対象の受講生のコース情報リストを取得
    List<StudentsCourse> courseExist = studentRepository.searchStudentsCourses(targetStudentId);
    if (courseExist.isEmpty()) {
      throw new NoDataException(
          "更新対象のコース受講情報が見つかりません。[ID: " + targetStudentId + " ]");
    }

    // 事前に対象のコースのステータス情報リストを取得
    List<CourseStatus> statusExist = courseExist.stream()
        .map(course -> studentRepository.searchCourseStatus(course.getAttendingId()))
        .toList();
    if (statusExist.isEmpty()) {
      throw new NoDataException(
          "更新対象のステータス情報が見つかりません。[ID: " + targetStudentId + " ]");
    }

    // 受講生情報の更新
    int updatedStudentData = studentRepository.updateStudent(studentDetail.getStudent());
    // コース情報・ステータス情報の更新
    int updatedStudentsCourseData = 0;
    int updatedCourseStatusData = 0;
    // studentDetailに含まれるCourseDetailを一つづつ取り出して処理
    for (CourseDetail courseDetail : studentDetail.getCourseDetailList()) {
      // ↓ studentsCourseにはデータベースから取得した時点でattending_idが設定済みであるため、自動的にattending_idは@Updateに渡される
      int updateCourseData = studentRepository.updateStudentsCourses(courseDetail.getCourse());
      if (updateCourseData > 0) {
        updatedStudentsCourseData += updateCourseData;
      }
      int updateStatusData = studentRepository.updateCourseStatus(courseDetail.getStatus());
      if (updateStatusData > 0) {
        updatedCourseStatusData += updateStatusData;
      }
    }

    // 更新されたデータが0の場合に例外をスルー
    if (updatedStudentData == 0 && updatedStudentsCourseData == 0 && updatedCourseStatusData == 0) {
      throw new ProcessFailedException(
          "受講生情報・コース受講情報・受講ステータス情報 いずれも更新されませんでした。");
    }

  }

//  /**
//   * 【コースマスタの全件取得】
//   *
//   * @return コースマスタリスト（全件）
//   */
//  public List<Course> getCourseList() {
//    return studentRepository.displayCourseMaster();
//  }
//
//  /**
//   * 【コースマスタの新規作成】
//   *
//   * @param course 入力情報（マスタ情報）
//   */
//  public void registerCourseMaster(Course course) {
//    studentRepository.registerCourseMaster(course);
//  }
//
//  /**
//   * 【コースマスタのID検索】
//   *
//   * @param courseId 検索ID
//   * @return 該当するコースマスタ
//   */
//  public Course getCourseMaster(Integer courseId) {
//    return studentRepository.searchCourseMaster(courseId);
//  }
//
//  /**
//   * 【コースマスタの更新】
//   *
//   * @param course 入力された更新情報（コースマスタ）
//   */
//  public void updateCourseMaster(Course course) {
//
//    // 事前に対象のコースマスタを検索（なければ例外throw）
//    Integer targetCourseId = course.getCourseId();
//    Course courseExist = studentRepository.searchCourseMaster(targetCourseId);
//    if (courseExist == null) {
//      throw new NoDataException(
//          "更新対象のコースマスタが見つかりません。[ID: " + targetCourseId + " ]"
//      );
//    }
//
//    int updateMasterData = studentRepository.updateCourseMaster(course);
//
//    // 更新されなかった場合に例外をスルー
//    if (updateMasterData == 0) {
//      throw new ProcessFailedException("コースマスタは更新されませんでした。");
//    }
//
//  }
//
//  /**
//   * 【コースマスタの削除】
//   *
//   * @param courseId 削除対象のコースID
//   */
//  public void deleteCourseMaster(@Valid Integer courseId) {
//
//    // 事前に対象のコースマスタを検索（なければ例外throw）
//    Course courseExist = studentRepository.searchCourseMaster(courseId);
//    if (courseExist == null) {
//      throw new NoDataException("削除対象が見つかりません。[ID: " + courseId + " ]");
//    }
//
//    studentRepository.deleteCourseMaster(courseId);
//  }
}
