package com.backend.curi.launchedworkflow.repository.entity;

public class LaunchedModule{

}
/*
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LaunchedModule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LaunchedStatus status;

    // private ModuleType type;


    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "LaunchedSequenceId")
    private LaunchedSequence launchedSequence;

    private Long mongo_id;

    private Long order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;



}
*/