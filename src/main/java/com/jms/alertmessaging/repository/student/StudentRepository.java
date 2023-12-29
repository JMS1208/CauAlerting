package com.jms.alertmessaging.repository.student;

import com.jms.alertmessaging.entity.student.Student;

import java.util.List;

public interface StudentRepository {

    //유저 저장
    public void saveUser(Student student);

    //모든 유저 가져오기
    public List<Student> findAllUserList();

    //유저 가져오기
    public Student findUserById(Long id);

    //이메일로 유저 가져오기
    public Student findUserByEmail(String email);


    //해당 이메일이 존재하는지
    public boolean existsUserByEmail(String email);

    //해당 이메일 가진 유저 삭제
    public void deleteUserByEmail(String email);

    //알림 조정하기
    public void enableNotification(String username, boolean enabled);
}
