package com.backend.curi.frontoffice.repository.entity;

import com.backend.curi.launched.repository.entity.LaunchedSequence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Frontoffice {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    private LaunchedSequence launchedSequence;

    private UUID accessToken;
}
