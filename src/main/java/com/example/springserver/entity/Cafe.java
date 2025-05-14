package com.example.springserver.entity;

import com.example.springserver.domain.cafe.enums.CafeStatus;
import com.example.springserver.domain.user.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "cafes")
public class Cafe extends BaseEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "img_url")
    private String imgUrl;
    private String contact;
    private String intro;
    @Column(name = "adv_img_url")
    private String advImgUrl;

    @Column(name = "open_time")
    private LocalTime openTime;
    @Column(name = "close_time")
    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'", name = "cafe_status")
    private CafeStatus cafeStatus;

    @Builder.Default
    @Column(name = "total_stamp_count")
    private long totalStampCount = 0L;

    @Builder.Default
    @Column(name = "total_used_stamp_count")
    private long totalUsedStampCount = 0L;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<KeywordMapping> keywordMappings;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StampReward> stampRewards;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StampBoard> stampBoards;

    public void increaseTotalStampCount(int count) {
        this.totalStampCount += count;
    }

    public void increaseTotalUsedStampCount(int count) {
        this.totalUsedStampCount += count;
    }
}
