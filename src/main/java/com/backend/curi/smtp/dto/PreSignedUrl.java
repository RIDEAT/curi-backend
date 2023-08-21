package com.backend.curi.smtp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreSignedUrl {
    String fileName;
    String preSignedUrl;
}
