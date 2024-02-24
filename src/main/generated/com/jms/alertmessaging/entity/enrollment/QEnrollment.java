package com.jms.alertmessaging.entity.enrollment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEnrollment is a Querydsl query type for Enrollment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEnrollment extends EntityPathBase<Enrollment> {

    private static final long serialVersionUID = -1306071106L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEnrollment enrollment = new QEnrollment("enrollment");

    public final com.jms.alertmessaging.entity.department.QDepartment department;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<com.jms.alertmessaging.entity.keyword.Keyword, com.jms.alertmessaging.entity.keyword.QKeyword> keywords = this.<com.jms.alertmessaging.entity.keyword.Keyword, com.jms.alertmessaging.entity.keyword.QKeyword>createSet("keywords", com.jms.alertmessaging.entity.keyword.Keyword.class, com.jms.alertmessaging.entity.keyword.QKeyword.class, PathInits.DIRECT2);

    public final com.jms.alertmessaging.entity.student.QStudent student;

    public QEnrollment(String variable) {
        this(Enrollment.class, forVariable(variable), INITS);
    }

    public QEnrollment(Path<? extends Enrollment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEnrollment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEnrollment(PathMetadata metadata, PathInits inits) {
        this(Enrollment.class, metadata, inits);
    }

    public QEnrollment(Class<? extends Enrollment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.department = inits.isInitialized("department") ? new com.jms.alertmessaging.entity.department.QDepartment(forProperty("department")) : null;
        this.student = inits.isInitialized("student") ? new com.jms.alertmessaging.entity.student.QStudent(forProperty("student")) : null;
    }

}

