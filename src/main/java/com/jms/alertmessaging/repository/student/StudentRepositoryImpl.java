package com.jms.alertmessaging.repository.student;

import com.jms.alertmessaging.entity.student.QStudent;
import com.jms.alertmessaging.entity.student.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepositoryImpl extends QuerydslRepositorySupport implements StudentRepository {

    public StudentRepositoryImpl() {
        super(Student.class);
    }

    @Autowired
    private StudentJpaRepository studentJpaRepository;


    //모든 유저 가져오기
    @Override
    public List<Student> findAllUserList() {

        QStudent qStudent = QStudent.student;

        List<Student> allStudentListEntity = from(qStudent).fetch();

        return allStudentListEntity;
    }

    //유저 가져오기
    @Override
    public Student findUserById(Long id) {
        QStudent qStudent = QStudent.student;

        Student student = from(qStudent).where(qStudent.id.eq(id)).fetchOne();

        return student;
    }

    //이메일로 유저 가져오기
    @Override
    public Student findUserByEmail(String email) {
        QStudent qStudent = QStudent.student;

        Student student = from(qStudent).where(qStudent.email.eq(email)).fetchOne();

        return student;
    }

    //해당 이메일이 존재하는지
    @Override
    public boolean existsUserByEmail(String email) {
        return studentJpaRepository.existsByEmail(email);
    }

    //유저 저장
    @Override
    public void saveUser(Student student) {
        studentJpaRepository.save(student);
    }



    //해당 이메일 가진 유저 삭제
    @Override
    public void deleteUserByEmail(String email) {
        studentJpaRepository.deleteByEmail(email);
    }

    @Override
    public void enableNotification(String username, boolean enabled) {
        //TODO
    }


}
