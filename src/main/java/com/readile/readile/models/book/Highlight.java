package com.readile.readile.models.book;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Highlight")
public class Highlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_book_id")
    private UserBook userBook;

    @Column(length = 512, nullable = false)
    private String highlight;

    public Highlight(UserBook userBook, String highlight) {
        this.userBook = userBook;
        this.highlight = highlight;
    }
}