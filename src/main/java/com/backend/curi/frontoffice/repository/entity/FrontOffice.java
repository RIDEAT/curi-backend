package com.backend.curi.frontoffice.repository.entity;

import com.backend.curi.launched.repository.entity.LaunchedSequence;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrontOffice {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launchedSequenceId")
    private LaunchedSequence launchedSequence;

    private UUID accessToken;
}
