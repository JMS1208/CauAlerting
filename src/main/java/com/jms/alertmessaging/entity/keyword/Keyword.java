package com.jms.alertmessaging.entity.keyword;

import com.jms.alertmessaging.entity.enrollment.Enrollment;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Getter @Setter
@Entity
@Table(name = "keywords", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"enrollment_id", "content"})
})
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "content")
    private String content;

    public static int MAX_COUNT = 10;
    public static int MAX_LENGTH = 10;

    public void setEnrollment(Enrollment enrollment) {
        if(this.enrollment != null) {
            this.enrollment.getKeywords().remove(this);
        }
        this.enrollment = enrollment;
        if(enrollment != null) {
            enrollment.getKeywords().add(this);
        }
    }

}
