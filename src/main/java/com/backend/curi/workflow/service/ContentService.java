package com.backend.curi.workflow.service;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
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

    Object getMessage(ObjectId contentId){
        return contentRepository.findById(contentId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS)).getMessage();
    }

    Content getContent (ObjectId contentId){
        return contentRepository.findById(contentId).orElseThrow(()->new CuriException(HttpStatus.NOT_FOUND, ErrorType.CONTENT_NOT_EXISTS));

    }

    public Content createContent(Object substitutedMessage) {
        Content content = new Content();
        content.setMessage(substitutedMessage);

        return contentRepository.save(content);
    }
}