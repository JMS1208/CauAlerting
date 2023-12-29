package com.jms.alertmessaging.repository.student;


import com.jms.alertmessaging.entity.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentJpaRepository extends JpaRepository<Student, Long> {

    public boolean existsByEmail(String email);


    public void deleteByEmail(String email);

    public Student findByEmail(String email);
}
