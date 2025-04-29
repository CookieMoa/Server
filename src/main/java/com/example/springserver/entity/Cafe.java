package com.example.springserver.entity;

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

    private String imgUrl;
    private String contact;
    private String intro;
    private String advImgUrl;

    private LocalTime openTime;
    private LocalTime closeTime;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<KeywordMapping> keywordMappings;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StampReward> stampRewards;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StampBoard> stampBoards;
}
