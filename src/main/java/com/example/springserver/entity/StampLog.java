package com.example.springserver.entity;

import com.example.springserver.domain.log.enums.StampLogStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "stamp_logs")
public class StampLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stamp_log_status", nullable = false)
    private StampLogStatus stampLogStatus;

    @Column(nullable = false)
    private Integer count;

    @Builder.Default
    @Column(name = "pending_review", nullable = false)
    private Boolean pendingReview = true;

    @ManyToOne
    @JoinColumn(name = "stamp_board_id", nullable = false)
    private StampBoard stampBoard;
}
