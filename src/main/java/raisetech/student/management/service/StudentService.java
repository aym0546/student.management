package raisetech.student.management.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentsCourse;
import raisetech.student.management.data.StudentsCoursesStatus;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.exception.NoDataException;
import raisetech.student.management.exception.ProcessFailedException;
import raisetech.student.management.repository.StudentRepository;

/**
 * 受講生情報を取り扱うServiceです。 受講生の検索や登録、更新処理を行います。
 */
@Service
public class StudentService {

  //	インターフェースStudentRepository、StudentConverterを呼び出し
  private final StudentRepository repository;
  private final StudentConverter converter;

  private static final Logger log = LoggerFactory.getLogger(StudentService.class);

  /**
   * コンストラクタ
   *
   * @param repository 受講生を扱うリポジトリ
   * @param converter  受講生詳細と受講生情報・コース情報のコンバーター
   */
  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 【受講生一覧表示】 全件検索のため、条件指定は行わない。
   *
   * @return 受講生詳細情報一覧（全件）
   */
  public List<StudentDetail> getStudentList() {
    List<Student> studentList = repository.displayStudent();
    List<StudentsCourse> studentsCourses = repository.displayCourse();
    List<StudentsCoursesStatus> studentsCoursesStatuses = repository.displayStatus();
    List<CourseDetail> courseDetail = converter.convertCourseDetails(studentsCourses,
        studentsCoursesStatuses);
    return converter.convertStudentDetails(studentList, courseDetail);
  }

  /**
   * 【受講生検索】 IDに紐づく受講生情報を取得し、 次いでその受講生に紐づく受講コース情報・ステータス情報を取得して設定。
   *
   * @param studentId 受講生ID
   * @return 受講生詳細情報
   */
  public StudentDetail searchStudent(Integer studentId) {
    // 受け取ったstudentIDを元に受講生情報を検索
    Student student = repository.searchStudent(studentId);

    // nullチェック
    if (Objects.isNull(student)) {
      throw new NoDataException("該当する受講生が見つかりません。ID：" + studentId);
    }

    // ステータス情報の空リスト
    List<StudentsCoursesStatus> statuses = new ArrayList<>();

    // 受講生情報のstudentIDに基づいてコース情報を検索
    List<StudentsCourse> studentsCourses = repository.searchStudentsCourses(student.getStudentId());

    // 各受講コースのattendingIdに紐づく受講ステータスを検索し、statusesに追加
    for (StudentsCourse course : studentsCourses) {
      List<StudentsCoursesStatus> statusList = repository.searchStatuses(course.getAttendingId());
      statuses.addAll(statusList);
    }
    // コース情報studentsCoursesとステータス情報statusesをcourseDetailに変換
    List<CourseDetail> courseDetails = converter.convertCourseDetails(studentsCourses, statuses);
    // ↑の受講生情報・コース詳細情報を持つnew StudentDetailを生成してreturn
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
    int registeredStudent = repository.registerStudent(studentDetail.getStudent());
    if (registeredStudent == 0) {
      throw new ProcessFailedException("受講生の登録に失敗しました。");
    }

    // コース情報の登録（コースの数だけコース情報を取得する）
    int registeredCourse = 0;
    int registeredStatus = 0;
    for (CourseDetail courseDetail : studentDetail.getCourseDetails()) {
      // 詳細情報からコース情報を取り出し、studentIdを設定　※student_idは↑で登録した時に自動採番されたstudent_idを利用
      StudentsCourse course = courseDetail.getStudentsCourse();
      course.setStudentId(studentDetail.getStudent().getStudentId());
      registeredCourse += repository.registerStudentsCourses(course);

      // ステータス情報の登録（コース登録直後に紐づいたものを登録）
      for (StudentsCoursesStatus coursesStatus : courseDetail.getStatusHistory()) {
        // courseDetailからステータス履歴を取り出し、ステータスを登録する
        coursesStatus.setAttendingId(course.getAttendingId());  // 登録したコース情報からattendingIdを取得
        registeredStatus += repository.registerStudentsCoursesStatus(coursesStatus);
      }
    }
    if (registeredCourse == 0) {
      throw new ProcessFailedException("受講コース情報の登録に失敗しました。");
    } else if (registeredStatus == 0) {
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
  public void updateStudent(StudentDetail studentDetail) throws DataIntegrityViolationException {
    // ここに遷移した時点で既に特定のstudentIdのstudentDetailが呼び出されている

    // 事前に対象の受講生情報を検索（なければ例外throw）
    Integer targetStudentId = studentDetail.getStudent().getStudentId();
    Student studentExist = repository.searchStudent(targetStudentId);
    if (studentExist == null) {
      throw new NoDataException(
          "更新対象の受講生情報が見つかりません。[ID: " + targetStudentId + " ]");
    }

    // 事前に対象の受講生のコース情報リストを取得
    List<StudentsCourse> coursesExist = repository.searchStudentsCourses(targetStudentId);
    if (coursesExist.isEmpty()) {
      throw new NoDataException(
          "更新対象のコース受講情報が見つかりません。[ID: " + targetStudentId + " ]");
    }

    // 複数のコース情報IDからステータス情報を検索
    List<StudentsCoursesStatus> statusesExist = new ArrayList<>();
    for (StudentsCourse course : coursesExist) {
      statusesExist.addAll(repository.searchStatuses(course.getAttendingId()));
    }
    if (statusesExist.isEmpty()) {
      throw new NoDataException(
          "更新対象の受講ステータス情報が見つかりません。[ID: " + targetStudentId + " ]");
    }

    // 受講生情報の更新
    int updatedStudentData = repository.updateStudent(studentDetail.getStudent());

    // コース情報の更新
    int updatedStudentsCourseData = 0;
    int updatedStatus = 0;
    Map<Long, LocalDate> newRegisteredStatuses = new HashMap<>();  // 新規登録されたattendingIdとstartDateのセット
    // studentDetailに含まれるcourseDetailを分解し、一つずつ取り出して処理
    for (CourseDetail courseDetail : studentDetail.getCourseDetails()) {  // コース詳細情報を一つずつ取り出す
      Long attendingId = courseDetail.getStudentsCourse().getAttendingId();

      int updateData = repository.updateStudentsCourses(
          courseDetail.getStudentsCourse());  // コース情報を更新してカウント
      if (updateData > 0) {
        updatedStudentsCourseData += updateData;
      }

      // ステータス情報の更新
      int registerStatus = 0;
      int updateStatus = 0;
      for (StudentsCoursesStatus status : courseDetail.getStatusHistory()) {  // ステータス履歴（リスト）を取り出す
        // ユニーク制約（status_id/attending_id/status_start_date）違反チェック
        try {
          registerStatus = repository.registerStudentsCoursesStatus(status);  // 登録処理try

          if (registerStatus > 0) {
            updatedStatus += registerStatus;
            newRegisteredStatuses.put(attendingId,
                status.getStatusStartDate());  // 新規登録成功したattendingIdと開始日を追加
          }
        } catch (DataIntegrityViolationException e) {
          updateStatus = repository.updateStudentsCoursesStatus(status);  // 重複する場合は更新
          if (updateStatus > 0) {
            updatedStatus += updateStatus;
          }
        }
      }
    }

    // 新規登録されたattendingIdの未終了ステータスを更新
    for (Map.Entry<Long, LocalDate> entry : newRegisteredStatuses.entrySet()) {
      Long attendingId = entry.getKey();
      ;
      LocalDate statusStartDate = entry.getValue();

      int updatedStatusEndDate = repository.updateStatusEndDateForMysql(attendingId,
          statusStartDate);
      if (updatedStatusEndDate > 0) {
        log.info("attending_id {} の未終了ステータスを更新しました。", attendingId);
      }
    }
    // 更新されたデータが0の場合に例外をスルー
    if (updatedStudentData == 0 && updatedStudentsCourseData == 0 && updatedStatus == 0) {
      throw new ProcessFailedException(
          "受講生情報・コース受講情報・受講ステータス いずれも更新されませんでした。");
    }

  }

}
