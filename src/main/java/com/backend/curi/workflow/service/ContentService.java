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



    public ContentResponse getContent (ObjectId contentId){
        Content content = contentRepository.findById(contentId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));
        return ContentResponse.of(content);
    }

    public Object getMessage(ObjectId contentId){
        return getContent(contentId).getMessage();
    }

    public Content createContent(Object substitutedMessage) {
        Content content = new Content();
        content.setMessage(substitutedMessage);

        return contentRepository.save(content);
    }
}
