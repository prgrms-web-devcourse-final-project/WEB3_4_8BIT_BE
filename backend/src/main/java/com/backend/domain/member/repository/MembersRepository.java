package com.backend.domain.member.repository;

import java.util.Optional;

import com.backend.domain.member.domain.Provider;
import com.backend.domain.member.entity.Members;

public interface MembersRepository {

	Members save(Members members);

	Optional<Members> findByProviderAndProviderId(Provider provider, String providerId);


}
