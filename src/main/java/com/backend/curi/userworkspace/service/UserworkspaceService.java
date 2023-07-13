package com.backend.curi.userworkspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.userworkspace.repository.UserworkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserworkspaceService {
    private final UserworkspaceRepository userworkspaceRepository;

    public Userworkspace create (String userId, int workspaceId){
        return userworkspaceRepository.save(Userworkspace.builder().userId(userId).workspaceId(workspaceId).build());
    }

    public List<Integer> getWorkspaceIdListByUserId(String userId) {
        var workspaceIdList = userworkspaceRepository
                .findAllByUserId(userId)
                .stream()
                .map(Userworkspace::getWorkspaceId)
                .collect(Collectors.toList());

        if (workspaceIdList.isEmpty())
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS);

        return workspaceIdList;
    }

    public List<Integer> getWorkspaceIdListByUserEmails(String userEmail){
        List<Userworkspace> userworkspaceList = userworkspaceRepository.findAllByUserEmail(userEmail);
        return userworkspaceList.stream().map(Userworkspace::getWorkspaceId).collect(Collectors.toList());
    }

    public List<String> getUserIdListByWorkspaceId(int workspaceId){
        var userworkspaceList = userworkspaceRepository.findAllByWorkspaceId(workspaceId);

        return userworkspaceList.stream().map(Userworkspace::getUserId).collect(Collectors.toList());
    }

    public boolean exist (String userId, int workspaceId){
        return !userworkspaceRepository.findAllByUserIdAndWorkspaceId(userId, workspaceId).isEmpty();
    }

    public void delete (String userId, int workspaceId){
        userworkspaceRepository.deleteAll(userworkspaceRepository.findAllByUserIdAndWorkspaceId(userId, workspaceId));
        return;
    }






}
