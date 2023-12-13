package com.jms.alertmessaging.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

@Setter
@Getter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements Comparable<Post> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    public LocalDateTime createdAt;

    public int postNumber;
    public String title;
    public String author;
    public LocalDate date;
    public String link;

    //순차 정렬
    @Override
    public int compareTo(Post other) {
        return Integer.compare(this.postNumber, other.postNumber);
    }

}
