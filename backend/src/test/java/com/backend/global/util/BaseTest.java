package com.backend.global.util;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

public abstract class BaseTest {

	public static final FixtureMonkey fixtureMonkeyBuilder  = FixtureMonkey.builder()
			.objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();
	public static final FixtureMonkey fixtureMonkeyRecord = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();
	public static final FixtureMonkey fixtureMonkeyValidation = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.plugin(new JakartaValidationPlugin())
			.build();
}
