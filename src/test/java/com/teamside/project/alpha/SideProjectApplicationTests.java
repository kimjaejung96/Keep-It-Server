package com.teamside.project.alpha;

import com.teamside.project.common.exception.CustomException;
import com.teamside.project.common.model.dto.ResponseObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class SideProjectApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	ResponseEntity<ResponseObject> 익셉션테스트() throws CustomException {
		ResponseObject responseObject = new ResponseObject();
		responseObject.setBody("z");
		return new ResponseEntity<>(responseObject, HttpStatus.OK);

		//		throw new CustomException(ApiExceptionCode.OK);
	}


}
