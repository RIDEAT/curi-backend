package com.backend.curi.workflow.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.member.repository.entity.Member;
import com.backend.curi.security.dto.CurrentUser;
import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.controller.dto.SequenceResponse;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.entity.Content;
import com.backend.curi.workflow.repository.entity.ModuleType;
import com.backend.curi.workflow.repository.entity.contents.ContentsContent;
import com.backend.curi.workspace.controller.dto.RoleResponse;
import com.backend.curi.workspace.repository.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final List<String> syntaxList = List.of("이름", "이메일");
    public Content getContent(ObjectId contentId){
        return contentRepository.findById(contentId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
    }

    public Content copyContents(Content contentToCopy, Map<Role, Member> memberMap){
        Content content = Content.of(contentToCopy);
        parseContent(content, memberMap);
        return contentRepository.save(content);
    }

    private void parseContent(Content content, Map<Role, Member> memberMap){
        if(content.getType() == ModuleType.contents) {
            var data = (ContentsContent)content.getContent();
            if(data.getContent() == null)
                return;
            var jsonContent = new JSONObject(data.getContent());
            var stringData = jsonContent.toString();
            for(var entry : memberMap.entrySet()){
                var role = entry.getKey();
                var member = entry.getValue();
                for(var syntax : syntaxList) {
                    stringData = stringData.replace(
                            getRolesSyntax(role.getName(),syntax),
                            parseFrom(syntax, member));
                }
            }

            data.setContent(new JSONObject(stringData));
            content.setContent(data);
        }
    }

    private String getRolesSyntax(String roleName, String syntax){
        return String.format("{%s.%s}", roleName, syntax);
    }

    private String parseFrom(String syntax, Member member){
        return switch (syntax) {
            case "이름" -> member.getName();
            case "이메일" -> member.getEmail();
            default -> "";
        };
    }

}
