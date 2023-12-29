package com.jms.alertmessaging.entity.enrollment.keyword;

import com.jms.alertmessaging.entity.enrollment.Enrollment;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@IdClass(EnrollmentKeywordId.class)
public class EnrollmentKeyword {

    @Id
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Id
    private String keyword;

}
