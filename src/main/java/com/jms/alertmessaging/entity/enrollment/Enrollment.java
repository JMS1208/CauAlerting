package com.jms.alertmessaging.entity.enrollment;

import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.keyword.Keyword;
import com.jms.alertmessaging.entity.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "enrollment")
    private Set<Keyword> keywords = new HashSet<>();


}
