package io.robe.auth.token;

import io.dropwizard.auth.Auth;
import io.robe.auth.tokenbased.Token;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.Principal;

public class TokenFactoryProvider extends AbstractValueFactoryProvider {

    private final TokenFactory tokenFactory;

    @Inject
    public TokenFactoryProvider(
            final MultivaluedParameterExtractorProvider extractorProvider,
            ServiceLocator locator,
            TokenFactory tokenFactory) {

        super(extractorProvider, locator, Parameter.Source.UNKNOWN);
        this.tokenFactory = tokenFactory;
    }

    @Override
    protected Factory<?> createValueFactory(Parameter parameter) {
        Class<?> paramType = parameter.getRawType();
        Auth annotation = parameter.getAnnotation(Auth.class);
        if (annotation != null && paramType.isAssignableFrom(Token.class)) {
            return tokenFactory;
        }
        return null;
    }

    @Singleton
    private static class PrincipalClassProvider<T extends Principal> {

        private final Class<T> clazz;

        public PrincipalClassProvider(Class<T> clazz) {
            this.clazz = clazz;
        }
    }

    public static class Binder<T extends Principal> extends AbstractBinder {

        private final Class<T> principalClass;

        public Binder(Class<T> principalClass) {
            this.principalClass = principalClass;
        }

        @Override
        protected void configure() {
            bind(new PrincipalClassProvider<>(principalClass)).to(PrincipalClassProvider.class);
            bind(TokenFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(TokenParamInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Auth>>() {
            }).in(Singleton.class);
        }
    }
}
