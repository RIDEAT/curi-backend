package com.backend.curi.userworkspace.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.userworkspace.repository.UserworkspaceRepository;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.hibernate.jdbc.Work;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserworkspaceService {
    private final UserworkspaceRepository userworkspaceRepository;
    private final UserRepository userRepository;
    public Userworkspace create (String userId, Workspace workspace){
        var user = userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        return userworkspaceRepository.save(Userworkspace.builder().user(user).workspace(workspace).build());
    }

    public List<Workspace> getWorkspaceListByUser(String userId) {
        var user = userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        var workspaceList = userworkspaceRepository
                .findAllByUser(user)
                .stream()
                .map(Userworkspace::getWorkspace)
                .collect(Collectors.toList());

        if (workspaceList.isEmpty())
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS);

        return workspaceList;
    }

    public List<Workspace> getWorkspaceListByUserEmails(String userEmail){
        List<Userworkspace> userworkspaceList = userworkspaceRepository.findAllByUserEmail(userEmail);
        return userworkspaceList.stream().map(Userworkspace::getWorkspace).collect(Collectors.toList());
    }

    public List<User_> getUserListByWorkspace(Workspace workspace){
        var userworkspaceList = userworkspaceRepository.findAllByWorkspace(workspace);

        return userworkspaceList.stream().map(Userworkspace::getUser).collect(Collectors.toList());
    }

    public boolean exist (String userId, Workspace workspace){
        var user = userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        return !userworkspaceRepository.findAllByUserAndWorkspace(user, workspace).isEmpty();
    }

    public void delete (String userId, Workspace workspace){
        var user = userRepository.findByUserId(userId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        userworkspaceRepository.deleteAll(userworkspaceRepository.findAllByUserAndWorkspace(user, workspace));
        return;
    }






}
