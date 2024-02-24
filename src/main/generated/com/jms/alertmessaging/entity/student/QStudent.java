package com.jms.alertmessaging.entity.student;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudent is a Querydsl query type for Student
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudent extends EntityPathBase<Student> {

    private static final long serialVersionUID = 502387928L;

    public static final QStudent student = new QStudent("student");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final SetPath<com.jms.alertmessaging.entity.enrollment.Enrollment, com.jms.alertmessaging.entity.enrollment.QEnrollment> enrollments = this.<com.jms.alertmessaging.entity.enrollment.Enrollment, com.jms.alertmessaging.entity.enrollment.QEnrollment>createSet("enrollments", com.jms.alertmessaging.entity.enrollment.Enrollment.class, com.jms.alertmessaging.entity.enrollment.QEnrollment.class, PathInits.DIRECT2);

    public final EnumPath<Frequency> frequency = createEnum("frequency", Frequency.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final ListPath<String, StringPath> roles = this.<String, StringPath>createList("roles", String.class, StringPath.class, PathInits.DIRECT2);

    public QStudent(String variable) {
        super(Student.class, forVariable(variable));
    }

    public QStudent(Path<? extends Student> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudent(PathMetadata metadata) {
        super(Student.class, metadata);
    }

}

