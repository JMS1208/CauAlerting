package com.jms.alertmessaging.controller.api.student;

import com.jms.alertmessaging.dto.student.StudentInfoBundleDto;
import com.jms.alertmessaging.dto.student.info.StudentInfoRequestDto;
import com.jms.alertmessaging.service.student.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentService studentService;

    private final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getStudentInfo(@RequestParam String email) {
        logger.info("[getStudentInfo] 유저 정보가져오자: {}", email);
        StudentInfoBundleDto bundle = studentService.findUserBundleByEmail(email);
        logger.info("[getStudentInfo] 가져옴: {}", bundle);
        return ResponseEntity.ok(bundle);
    }
}
