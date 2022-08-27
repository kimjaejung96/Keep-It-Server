package com.teamside.project.alpha.group.controller;


import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomException;
import com.teamside.project.alpha.common.model.constant.KeepitConstant;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.GroupDto;
import com.teamside.project.alpha.group.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.awt.print.Pageable;

@RestController
@Validated
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createGroup(@RequestBody @Valid GroupDto group) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        groupService.createGroup(group);

        return new ResponseEntity<>(responseObject, HttpStatus.CREATED);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ResponseObject> deleteGroup(@PathVariable Long groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.deleteGroup(groupId);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @GetMapping("/{groupId}")
    public ResponseEntity<ResponseObject> selectGroup(@PathVariable Long groupId) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.selectGroup(groupId));

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @PatchMapping
    public ResponseEntity<ResponseObject> updateGroup(@RequestBody @Valid GroupDto group) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.updateGroup(group);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
    @GetMapping("/{groupName}/exists")
    public ResponseEntity<ResponseObject> isExistGroupName(@Pattern (regexp = KeepitConstant.REGEXP_GROUP_NAME)@PathVariable String groupName) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        groupService.isExistGroupName(groupName);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> groups(@RequestParam(required = false) Long groupId, @RequestParam Long pageSize) throws CustomException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.groups(groupId, pageSize));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/random")
    public ResponseEntity<ResponseObject> random(){
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(groupService.random());
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}
