package com.jms.alertmessaging.entity.notification;

import com.jms.alertmessaging.entity.board.Board;
import com.jms.alertmessaging.entity.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @CreationTimestamp
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
