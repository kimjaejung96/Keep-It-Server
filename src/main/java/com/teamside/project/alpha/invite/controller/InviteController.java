package com.teamside.project.alpha.invite.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.enumurate.InviteType;
import com.teamside.project.alpha.invite.service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invites")
public class InviteController {
    private final InviteService inviteService;

    /**
     * 멤버 초대
     * @param groupId
     * @param inviteType
     * @param mid
     * @return
     * @throws CustomRuntimeException
     */
    @PostMapping("/groups/{groupId}")
    public ResponseEntity<ResponseObject> inviteMember(@PathVariable String groupId,
                                                       @RequestParam InviteType inviteType,
                                                       @RequestParam(required = false) String mid) throws CustomRuntimeException {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(inviteService.inviteMember(groupId, inviteType, mid));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @GetMapping("/{inviteId}/groups/{groupId}")
    public ResponseEntity<ResponseObject> getInvite(@PathVariable String inviteId,
                                                    @PathVariable String groupId) {

        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(inviteService.getInvite(inviteId, groupId));
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

}
