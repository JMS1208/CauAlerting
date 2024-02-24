package com.jms.alertmessaging.service.student;

import com.jms.alertmessaging.dto.student.KeywordDto;
import com.jms.alertmessaging.dto.student.StudentInfoBundle;
import com.jms.alertmessaging.entity.student.Frequency;
import com.jms.alertmessaging.entity.student.Student;

import java.util.List;

public interface StudentServiceV2 {

    void deleteUserById(Long userId);

    //유저 정보 가져오기
    Student findUserByEmail(String email);

    //유저의 모든 정보 가져오기
    StudentInfoBundle findUserBundleByEmail();

    void updateMyDepartment(long departmentId, boolean select);

    //학부를 등록한 유저 가져오기
    //TODO

    //키워드 업데이트
    List<String> updateKeyword(KeywordDto keywordDto);

    public String updateFrequency(Frequency frequency);
}
