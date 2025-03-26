package com.backend.domain.shipfishposts.service;

import com.backend.domain.shipfishposts.repository.ShipFishPostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipFishPostsServiceImpl implements ShipFishPostsService{

    private final ShipFishPostsRepository shipFishPostsRepository;

}

