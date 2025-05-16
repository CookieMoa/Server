package com.example.springserver.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "stamp_boards")
public class StampBoard extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stamps_count")
    private int stampsCount;
    @Column(name = "used_stamps")
    private int usedStamps;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @OneToMany(mappedBy = "stampBoard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Stamp> stamps;

    @OneToMany(mappedBy = "stampBoard", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StampLog> stampLogs;

    public void increaseStampCount(int count) {
        this.stampsCount += count;
    }

    public void increaseUsedStampCount(int count) {
        this.usedStamps += count;
    }
}