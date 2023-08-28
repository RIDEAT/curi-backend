package com.backend.curi.slack.repository.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlackInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String userFirebaseId;

    @Column
    String userSlackId;

    @Column
    String accessToken;


}
