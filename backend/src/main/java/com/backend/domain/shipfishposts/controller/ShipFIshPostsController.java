package com.backend.domain.shipfishposts.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.shipfishposts.constant.ShipFishPostsMessageConstant;
import com.backend.domain.shipfishposts.dto.request.ShipFishPostsRequest;
import com.backend.domain.shipfishposts.service.ShipFishPostsService;
import com.backend.global.exception.GlobalErrorCode;
import com.backend.global.exception.GlobalException;
import com.backend.global.response.GenericResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ship-post")
@RequiredArgsConstructor
public class ShipFishPostsController {

	private final ShipFishPostsService shipFishPostsService;

	@PostMapping
	public GenericResponse<?> createShipFishPost(
		@RequestBody @Valid ShipFishPostsRequest.Create requestDto,
		BindingResult bindingResult) {

		// Todo : UserDetails

		if (bindingResult.hasErrors()) {
			throw new GlobalException(GlobalErrorCode.NOT_VALID);
		}

		Long shipFishPostsId = shipFishPostsService.save(requestDto);

		return GenericResponse.ok(ShipFishPostsMessageConstant.CREATE_POSTS_SUCCESS);
	}

}
