package com.jms.alertmessaging.entity.board;

import com.jms.alertmessaging.entity.department.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board implements Comparable<Board> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public int postNumber;
    public String title;
    public String writer;
    public String link;
    public LocalDate postAt;


    //순차 정렬
    @Override
    public int compareTo(Board other) {
        return Integer.compare(this.postNumber, other.postNumber);
    }

}
