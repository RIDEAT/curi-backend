package com.backend.curi.userworkspace.service;

import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.userworkspace.repository.UserworkspaceRepository;
import lombok.RequiredArgsConstructor;
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
        List<Userworkspace> userworkspaceList = userworkspaceRepository.findAllByUserId(userId);
        return userworkspaceList.stream().map(Userworkspace::getWorkspaceId).collect(Collectors.toList());
    }

    public List<Integer> getWorkspaceIdListByUserEmails(String userEmail){
        List<Userworkspace> userworkspaceList = userworkspaceRepository.findAllByUserEmail(userEmail);
        return userworkspaceList.stream().map(Userworkspace::getWorkspaceId).collect(Collectors.toList());
    }

    public List<String> getUserIdListByWorkspaceId(int workspaceId){
        List<Userworkspace> userworkspaceList = userworkspaceRepository.findAllByWorkspaceId(workspaceId);
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
