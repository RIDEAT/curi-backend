package com.backend.curi.workflow.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import com.backend.curi.workflow.controller.dto.ContentResponse;
import com.backend.curi.workflow.repository.ContentRepository;
import com.backend.curi.workflow.repository.entity.Content;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;



    public ContentResponse getContents(ObjectId contentId){
        Content content = contentRepository.findById(contentId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
        return ContentResponse.of(content);
    }

    public Object getContent(ObjectId contentId){
        return getContents(contentId).getContents();
    }

    public Content createContents(Object substitutedMessage) {
        Content content = new Content();
        content.setContent(substitutedMessage);

        return contentRepository.save(content);
    }
}
