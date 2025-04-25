package com.example.springserver.entity;

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
@Table(name = "stamp_rewards")
public class StampReward extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int stampCount;

    @Column(nullable = false)
    private String rewardName;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;
}