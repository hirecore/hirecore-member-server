package io.hirecore.hirecorememberserver.support;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.jqwik.JavaTypeArbitraryGenerator;
import com.navercorp.fixturemonkey.api.jqwik.JqwikPlugin;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.arbitraries.StringArbitrary;

import java.util.List;

public final class FixtureMonkeyFactory {

    private static final FixtureMonkey MONKEY = FixtureMonkey.builder()
            .objectIntrospector(new FailoverIntrospector(List.of(
                    BuilderArbitraryIntrospector.INSTANCE,
                    ConstructorPropertiesArbitraryIntrospector.INSTANCE,
                    FieldReflectionArbitraryIntrospector.INSTANCE
            )))
            .defaultNotNull(true)
            .plugin(new JqwikPlugin()
                    .javaTypeArbitraryGenerator(new JavaTypeArbitraryGenerator() {
                        @Override
                        public StringArbitrary strings() {
                            return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
                        }
                    })
            )
            .build();

    private FixtureMonkeyFactory() {}

    public static FixtureMonkey monkey() {
        return MONKEY;
    }
}
