package au.gov.ga.geodesy.support.mapper.orika.geodesyml;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Bidirectional conversion with composition.
 */
public abstract class ComposableBidirectionalConverter<A, B> extends BidirectionalConverter<A, B> {

    /**
     * Given a converter from B to C, return a converter from A to C.
     */
    public <C> ComposableBidirectionalConverter<A, C> compose(BidirectionalConverter<B, C> bToC) {
        return new ComposableBidirectionalConverter<A, C>() {

            @Override
            public C convertTo(A a, Type<C> targetType, MappingContext ctx) {
                @SuppressWarnings("unchecked")
                B b = ComposableBidirectionalConverter.this.convertTo(a, (Type<B>) ComposableBidirectionalConverter.this.destinationType, ctx);
                return bToC.convertTo(b, targetType, ctx);
            }

            @Override
            public A convertFrom(C c, Type<A> targetType, MappingContext ctx) {
                @SuppressWarnings("unchecked")
                B b = bToC.convertFrom(c, (Type<B>) ComposableBidirectionalConverter.this.destinationType, ctx);
                return ComposableBidirectionalConverter.this.convertFrom(b, targetType, ctx);
            }
        };
    }
}
