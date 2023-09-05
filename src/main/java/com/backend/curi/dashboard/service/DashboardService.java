package com.backend.curi.dashboard.service;

import com.backend.curi.dashboard.controller.dto.*;
import com.backend.curi.dashboard.repository.OverdueAlertRepository;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.launched.controller.dto.LaunchedSequenceResponse;
import com.backend.curi.launched.controller.dto.LaunchedWorkflowResponse;
import com.backend.curi.launched.repository.LaunchedSequenceRepository;
import com.backend.curi.launched.repository.entity.LaunchedSequence;
import com.backend.curi.launched.repository.entity.LaunchedStatus;
import com.backend.curi.launched.repository.entity.LaunchedWorkflow;
import com.backend.curi.launched.service.LaunchedSequenceService;
import com.backend.curi.launched.service.LaunchedWorkflowService;
import com.backend.curi.member.repository.entity.MemberType;
import com.backend.curi.workflow.controller.dto.WorkflowResponse;
import com.backend.curi.workflow.repository.entity.Workflow;
import com.backend.curi.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final WorkflowService workflowService;
    private final LaunchedWorkflowService launchedWorkflowService;
    private final LaunchedSequenceService launchedSequenceService;
    private final OverdueAlertRepository overdueAlertRepository;
    private final LaunchedSequenceRepository launchedSequenceRepository;
    public List<DashboardWorkflowResponse> getDashboardWorkflowResponseList(Long workspaceId) {

        List<DashboardWorkflowResponse> dashboardWorkflowResponseList = new ArrayList<>();
        List<WorkflowResponse> workflowResponseList = workflowService.getWorkflows(workspaceId);

        for (WorkflowResponse workflowResponse : workflowResponseList) {
            List <LaunchedWorkflow> launchedWorkflowList = launchedWorkflowService.getLaunchedWorkflowEntityListByWorkflowId(workflowResponse.getId());
            dashboardWorkflowResponseList.add(getDashboardWorkflowResponse(workflowResponse, launchedWorkflowList));
        }

        return dashboardWorkflowResponseList;

    }


    private DashboardWorkflowResponse getDashboardWorkflowResponse(WorkflowResponse workflowResponse, List<LaunchedWorkflow> launchedWorkflowList){
        DashboardWorkflowResponse dashboardWorkflowResponse = new DashboardWorkflowResponse(workflowResponse.getName(),workflowResponse.getId(),0L,0L,0L,0L);

        if (launchedWorkflowList.size() == 0){
            return dashboardWorkflowResponse;
        }

        for (LaunchedWorkflow launchedWorkflow : launchedWorkflowList){

            if(launchedWorkflow.getStatus().equals(LaunchedStatus.SKIPPED)) continue;

            if (launchedWorkflow.getStatus().equals(LaunchedStatus.TO_DO) ) {
                dashboardWorkflowResponse.setPendingCnt(dashboardWorkflowResponse.getPendingCnt() + 1);
            } else if (launchedWorkflow.getStatus().equals(LaunchedStatus.IN_PROGRESS) || launchedWorkflow.getStatus().equals(LaunchedStatus.OVERDUE)){
                dashboardWorkflowResponse.setInProgressCnt(dashboardWorkflowResponse.getInProgressCnt() + 1);
            } else if (launchedWorkflow.getStatus().equals(LaunchedStatus.COMPLETED) || launchedWorkflow.getStatus().equals(LaunchedStatus.MARKED_AS_COMPLETED)){
                dashboardWorkflowResponse.setCompletedCnt(dashboardWorkflowResponse.getCompletedCnt()+1);
            } else{
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.WORKFLOW_NOT_NORMAL);
            }
        }

        dashboardWorkflowResponse.setProgress(getProgress(launchedWorkflowList));
        return dashboardWorkflowResponse;
    }

    public Long getProgress (List<LaunchedWorkflow> launchedWorkflowList){
        Long completedCnt = 0L;
        Long totalCnt = 0L;

        for (LaunchedWorkflow launchedWorkflow : launchedWorkflowList){
            List<LaunchedSequence> launchedSequences = launchedWorkflow.getLaunchedSequences();
            for (LaunchedSequence launchedSequence : launchedSequences){
                if (launchedSequence.getStatus().equals(LaunchedStatus.COMPLETED) || launchedSequence.getStatus().equals(LaunchedStatus.MARKED_AS_COMPLETED)){
                    completedCnt ++;
                }
                totalCnt ++;
            }
        }

        if (totalCnt.equals(0L)) return 0L;
        return 100* completedCnt / totalCnt;

    }

    public DashboardMemberListResponse getDashboardMemberListResponse(Long workflowId) {
        List <DashboardMemberResponse> dashboardMemberResponseList = getDashboardMemberResponseList(workflowId);
        Workflow workflow = workflowService.getWorkflowEntity(workflowId);


        return DashboardMemberListResponse.of (workflow.getName(), dashboardMemberResponseList);

    }


    public List<DashboardMemberResponse> getDashboardMemberResponseList(Long workflowId){
        List <LaunchedWorkflowResponse> launchedWorkflowResponseList = launchedWorkflowService.getLaunchedWorkflowListByWorkflowId(workflowId);
        List <DashboardMemberResponse> dashboardMemberResponseList = new ArrayList<>();

        for (LaunchedWorkflowResponse launchedWorkflowResponse : launchedWorkflowResponseList){
            dashboardMemberResponseList.add(getDashboardMemberResponse(launchedWorkflowResponse));
        }
        return dashboardMemberResponseList;
    }

    public DashboardMemberResponse getDashboardMemberResponse (LaunchedWorkflowResponse launchedWorkflowResponse){
        DashboardMemberResponse dashboardMemberResponse = new DashboardMemberResponse();
        dashboardMemberResponse.setLaunchedWorkflowResponse(launchedWorkflowResponse);
      //  dashboardMemberResponse.setENPS(launchedWorkflow.getMember().getEnps);
        dashboardMemberResponse.setProgress(getProgress(launchedWorkflowResponse));
        return dashboardMemberResponse;
    }

    private Long getProgress(LaunchedWorkflowResponse launchedWorkflowResponse){
        Long completedCnt = 0L;

        for (LaunchedSequenceResponse launchedSequenceResponse :launchedWorkflowResponse.getLaunchedSequences()){
            if (launchedSequenceResponse.getStatus().equals(LaunchedStatus.COMPLETED)){
                completedCnt ++;
            }
        }
        return 100* completedCnt / launchedWorkflowResponse.getLaunchedSequences().size();
    }

    @Transactional
    public DashboardAlertResponse getDashboardAlertResponse(Long workspaceId) {
        List<MemberAlertResponse> employeeAlertList = getMemberAlerts(MemberType.employee, workspaceId);
        List<MemberAlertResponse> managerAlertList = getMemberAlerts(MemberType.manager, workspaceId);

        return DashboardAlertResponse.of(employeeAlertList, managerAlertList);
    }

    private List<DashboardEmployeeAlertResponse> getEmployeeAlertList(List<LaunchedSequence> launchedSequenceList){
        List<DashboardEmployeeAlertResponse> employeeAlertList = new ArrayList<>();
        for (LaunchedSequence launchedSequence : launchedSequenceList){
            if(launchedSequence.getMember().getType().equals(MemberType.employee)){
                DashboardEmployeeAlertResponse employeeAlert = new DashboardEmployeeAlertResponse();
                employeeAlert.setName(launchedSequence.getMember().getName());
                employeeAlert.setSequence(launchedSequence.getName());
                employeeAlert.setWorkflow(launchedSequence.getLauchedWorkflow().getName());
                employeeAlert.setOverdue(ChronoUnit.DAYS.between(LocalDate.now(),launchedSequence.getApplyDate().plusDays(7)));
                employeeAlertList.add(employeeAlert);
            }
        }

        return employeeAlertList;
    }

    private List<DashboardManagerAlertResponse> getManagerAlertList(List<LaunchedSequence> launchedSequenceList){

        List<DashboardManagerAlertResponse> managerAlertList = new ArrayList<>();
        for (LaunchedSequence launchedSequence : launchedSequenceList){
            if(launchedSequence.getMember().getType().equals(MemberType.manager)){
                DashboardManagerAlertResponse mangerAlert = new DashboardManagerAlertResponse();
                mangerAlert.setName(launchedSequence.getMember().getName());
                mangerAlert.setSequence(launchedSequence.getName());
                mangerAlert.setWorkflow(launchedSequence.getLauchedWorkflow().getName());
                mangerAlert.setOverdue(ChronoUnit.DAYS.between(LocalDate.now(),launchedSequence.getApplyDate().plusDays(7)));
                managerAlertList.add(mangerAlert);
            }
        }

        return managerAlertList;

    }

    @Transactional
    public List<MemberAlertResponse> getMemberAlerts(MemberType type, Long workspaceId){
        var overdueAlerts = overdueAlertRepository.findAllByMemberTypeAndWorkspaceId(type, workspaceId);
        var sequenceStatusMap = launchedSequenceRepository.findAllByWorkspaceId(workspaceId).stream()
                .collect(Collectors.toMap(LaunchedSequence::getId, LaunchedSequence::getStatus));
        var completedAlerts = overdueAlerts.stream()
                .filter(alert -> {
                    var alertStatus = sequenceStatusMap.get(alert.getLaunchedSequenceId());
                    return alertStatus.compareTo(LaunchedStatus.COMPLETED) >= 0;
                }).collect(Collectors.toList());
        overdueAlertRepository.deleteAll(completedAlerts);
        return overdueAlerts.stream()
                .filter(alert -> sequenceStatusMap.get(alert.getLaunchedSequenceId()) == LaunchedStatus.OVERDUE)
                .map(MemberAlertResponse::of)
                .collect(Collectors.toList());
    }

}
