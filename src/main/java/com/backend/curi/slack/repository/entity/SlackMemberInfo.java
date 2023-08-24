package com.backend.curi.slack.repository.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlackMemberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Long memberId;

    @Column
    String memberSlackId;

    @Column
    String accessToken;
}
