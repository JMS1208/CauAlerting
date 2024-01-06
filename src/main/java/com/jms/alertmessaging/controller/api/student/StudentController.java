package com.jms.alertmessaging.controller.api.student;

import com.jms.alertmessaging.dto.student.StudentInfoBundle;
import com.jms.alertmessaging.dto.student.info.UpdateDepartmentRequest;
import com.jms.alertmessaging.service.auth.AuthService;
import com.jms.alertmessaging.service.student.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final AuthService authService;

    private final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    @GetMapping("/user-info")
    public ResponseEntity<?> getStudentInfo() {
        StudentInfoBundle bundle = studentService.findUserBundleByEmail();
        return ResponseEntity.ok(bundle);
    }

    @PostMapping("/department")
    public ResponseEntity<?> updateMyDepartment(@RequestBody UpdateDepartmentRequest requestDto) {
        studentService.updateMyDepartment(requestDto.getDepartmentId(), requestDto.isSelect());
        return ResponseEntity.ok().build();
    }

    //로그아웃 시키기
    @GetMapping("/sign-out")
    public ResponseEntity<?> signOut(HttpServletResponse response) {
        authService.signOut(response);
        return ResponseEntity.ok().build();
    }

    //회원탈퇴
    @GetMapping("/unregister")
    public ResponseEntity<?> unregister(HttpServletResponse response) {
        authService.deleteUser(response);
        return ResponseEntity.ok().build();
    }
}
