package com.teamside.project.alpha.common.exception;

import com.teamside.project.common.exception.CustomException;
import com.teamside.project.common.model.dto.ResponseObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class CustomExceptionTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("익셉션 테스트")
    ResponseEntity<ResponseObject> tests() throws CustomException {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setBody("z");
        return new ResponseEntity<>(responseObject, HttpStatus.OK);

        //		throw new CustomException(ApiExceptionCode.OK);
    }
}
