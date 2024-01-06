package com.jms.alertmessaging.repository.student;


import com.jms.alertmessaging.entity.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentJpaRepository extends JpaRepository<Student, Long> {

    public boolean existsByEmail(String email);

    public void deleteByEmail(String email);

    public Optional<Student> findByEmail(String email);
}
