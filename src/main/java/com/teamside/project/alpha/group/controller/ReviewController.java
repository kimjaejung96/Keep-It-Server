package com.teamside.project.alpha.group.controller;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.model.dto.ResponseObject;
import com.teamside.project.alpha.group.model.dto.CommentDto;
import com.teamside.project.alpha.group.model.dto.ReviewDto;
import com.teamside.project.alpha.group.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/groups/{groupId}/reviews")
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createReview(@PathVariable Long groupId, @Valid @RequestBody ReviewDto review) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        reviewService.createReview(groupId, review);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }
    @PatchMapping("")
    public ResponseEntity<ResponseObject> updateReview(@PathVariable Long groupId, @Valid @RequestBody ReviewDto.UpdateReviewDto review) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.updateReview(groupId, review);
        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    /**
     * 리뷰 상세 조회
     * @param reviewId
     * @return
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseObject> selectReviewDetail(@PathVariable Long groupId, @PathVariable Long reviewId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        responseObject.setBody(reviewService.selectReviewDetail(groupId, reviewId));

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }


    /**
     * 리뷰에 댓글, 대댓글 작성 API
     * @param comment 댓글 dto
     *                parentId -> nullable(대댓글 작성용)
     * @param reviewId 댓글 작성할 리뷰 id
     * @return
     */
    @PostMapping("/{reviewId}/comment")
    public ResponseEntity<ResponseObject> createComment(@PathVariable Long groupId, @Valid @RequestBody CommentDto.CreateComment comment, @PathVariable Long reviewId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.CREATED);
        reviewService.createComment(groupId, comment, reviewId);

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

    @PostMapping("/{reviewId}/keep")
    public ResponseEntity<ResponseObject> keepReview(@PathVariable Long groupId, @PathVariable Long reviewId) {
        ResponseObject responseObject = new ResponseObject(ApiExceptionCode.OK);
        reviewService.keepReview(groupId, reviewId);

        return new ResponseEntity(responseObject, HttpStatus.OK);
    }

}
