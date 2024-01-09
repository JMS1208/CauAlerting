package com.jms.alertmessaging.entity.board;

import com.jms.alertmessaging.entity.department.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;

@Getter
@Entity(name = "board")
@ToString(exclude = "department")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board implements Comparable<Board> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @BatchSize(size = 100)
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "post_number")
    public int postNumber;

    @Column(name = "title")
    public String title;

    @Column(name = "writer")
    public String writer;

    @Column(name = "link")
    public String link;

    @Column(name = "post_at")
    public LocalDate postAt;


    //순차 정렬
    @Override
    public int compareTo(Board other) {
        return Integer.compare(this.postNumber, other.postNumber);
    }

}
