package com.backend.curi.common.interceptor;

import com.backend.curi.exception.CuriException;
import com.backend.curi.exception.ErrorType;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class Extractor {
    public static Long extractLongFromUrl(HttpServletRequest request, String point) {
        String requestUrl = request.getRequestURI();
        String[] parts = requestUrl.split("/" +point+ "/");
        if (parts.length >= 2) {
            String id = parts[1].split("/")[0];
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
            }
        }

        throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
    }

    public static UUID extractUUIDFromUrl(HttpServletRequest request, String point) {
        String requestUrl = request.getRequestURI();
        String[] parts = requestUrl.split("/" +point+ "/");
        if (parts.length >= 2) {
            String id = parts[1].split("/")[0];
            try {
                return UUID.fromString(id);
            } catch (NumberFormatException e) {

                throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
            }
        }

        throw new CuriException(HttpStatus.BAD_REQUEST, ErrorType.INVALID_URL_ERROR);
    }
}
