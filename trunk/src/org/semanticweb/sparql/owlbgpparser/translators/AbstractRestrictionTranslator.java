package org.semanticweb.sparql.owlbgpparser.translators;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgpparser.TripleConsumer;

public abstract class AbstractRestrictionTranslator implements ClassExpressionTranslator {
    protected final TripleConsumer consumer;
    
    public AbstractRestrictionTranslator(TripleConsumer consumer) {
        this.consumer=consumer;
    }
    public ClassExpression translate(Identifier mainNode) {
        return translateRestriction(mainNode);
    }
    protected abstract ClassExpression translateRestriction(Identifier mainNode);
}
