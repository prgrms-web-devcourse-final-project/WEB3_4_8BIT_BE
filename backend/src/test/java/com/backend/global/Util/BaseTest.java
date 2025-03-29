package com.backend.global.util;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

public abstract class BaseTest {

	protected final FixtureMonkey fixtureMonkeyBuilder;
	protected final FixtureMonkey fixtureMonkeyRecord;
	protected final FixtureMonkey fixtureMonkeyValidation;

	public BaseTest() {
		this.fixtureMonkeyBuilder = FixtureMonkey.builder()
			.objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();

		this.fixtureMonkeyRecord = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.build();

		this.fixtureMonkeyValidation = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.defaultNotNull(true)
			.plugin(new JakartaValidationPlugin())
			.build();
	}
}
