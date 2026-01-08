package com.luzdorefugio.domain;

import com.luzdorefugio.domain.base.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reviews")
public class Review extends Auditable {
    @Id
    @UuidGenerator
    private UUID id;
    private String authorName;
    private String content;
    private Integer rating;
    private boolean active;
}