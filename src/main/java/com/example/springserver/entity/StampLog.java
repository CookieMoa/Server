package com.example.springserver.entity;

import com.example.springserver.domain.log.enums.StampLogStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
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

    @Column(name = "stamp_log_status", nullable = false)
    private StampLogStatus stampLogStatus;

    @Column(nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "stamp_board_id", nullable = false)
    private StampBoard stampBoard;
}
