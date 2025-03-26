package com.backend.domain.shipfishposts.controller;

import com.backend.domain.shipfishposts.service.ShipFishPostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ship-post")
@RequiredArgsConstructor
public class ShipFIshPostsController {

    private final ShipFishPostsService shipFishPostsService;

}
