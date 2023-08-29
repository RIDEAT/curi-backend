package com.backend.curi.userworkspace.service;

import com.backend.curi.CuriApplication;
import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.smtp.AwsS3Service;
import com.backend.curi.user.repository.UserRepository;
import com.backend.curi.user.repository.entity.User_;
import com.backend.curi.userworkspace.repository.entity.Userworkspace;
import com.backend.curi.userworkspace.repository.UserworkspaceRepository;
import com.backend.curi.workspace.repository.WorkspaceRepository;
import com.backend.curi.workspace.repository.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserworkspaceService {
    private static Logger log = LoggerFactory.getLogger(UserworkspaceService.class);

    private final UserworkspaceRepository userworkspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final AwsS3Service amazonS3Service;
    public Userworkspace create (CurrentUser currentUser, Workspace workspace){
        var user = userRepository.findByUserId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        return userworkspaceRepository.save(Userworkspace.builder().user(user).workspace(workspace).build());
    }

    public List<Workspace> getWorkspaceListByUser(CurrentUser currentUser) {
        log.info("current UserId: "+currentUser.getUserId());
        var user = userRepository.findByUserId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));

        var workspaceList = userworkspaceRepository
                .findAllByUser(user)
                .stream()
                .map(Userworkspace::getWorkspace)
                .collect(Collectors.toList());
        workspaceList.forEach(workspace -> workspace.setLogoUrl(amazonS3Service.getSignedUrl(workspace.getLogoUrl())));
        /*
        if (workspaceList.isEmpty())
            throw new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS);
*/
        return workspaceList;
    }


    public List<User_> getUserListByWorkspaceId(Long workspaceId){
        var userworkspaceList = userworkspaceRepository.findAllByWorkspaceId(workspaceId);

        return userworkspaceList.stream().map(Userworkspace::getUser).collect(Collectors.toList());
    }

    public void belongstoWorkspace(Long workspaceId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        String userId = currentUser.getUserId();
        User_ user = userRepository.findByUserId(currentUser.getUserId()).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.USER_NOT_EXISTS));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.WORKSPACE_NOT_EXISTS));
        if(userworkspaceRepository.findAllByUserAndWorkspace(user, workspace).isEmpty()){
            throw new CuriException(HttpStatus.FORBIDDEN, ErrorType.UNAUTHORIZED_WORKSPACE);
        }
    }

    public void delete (User_ user, Workspace workspace){
        userworkspaceRepository.deleteAll(userworkspaceRepository.findAllByUserAndWorkspace(user, workspace));
        return;
    }






}
