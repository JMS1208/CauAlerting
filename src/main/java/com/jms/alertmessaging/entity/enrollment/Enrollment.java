package com.jms.alertmessaging.entity.enrollment;

import com.jms.alertmessaging.entity.department.Department;
import com.jms.alertmessaging.entity.keyword.Keyword;
import com.jms.alertmessaging.entity.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
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
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private Set<Keyword> keywords = new HashSet<>();

    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
        keyword.setEnrollment(this);
    }

    public void addKeywords(Collection<Keyword> keywords) {
        this.keywords.addAll(keywords);
        keywords.forEach(keyword -> keyword.setEnrollment(this));
    }

    public void removeKeyword(Keyword keyword) {
        this.keywords.remove(keyword);
        keyword.setEnrollment(null);
    }

    public void removeKeywords(Collection<Keyword> keywords) {
        keywords.forEach(this::removeKeyword);
    }


}
