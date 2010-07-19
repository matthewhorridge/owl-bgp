package org.semanticweb.sparql.owlbgpparser;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class ObjectMaxCardinalityTranslator extends AbstractObjectCardinalityTranslator {

    public ObjectMaxCardinalityTranslator(OWLRDFConsumer consumer) {
        super(consumer);
    }
    protected ClassExpression createRestriction(ObjectPropertyExpression prop, int cardi) {
        return ObjectMaxCardinality.create(cardi, prop, Clazz.THING);
    }
    protected ClassExpression createRestriction(ObjectPropertyExpression prop,int cardi,ClassExpression filler) {
        return ObjectMaxCardinality.create(cardi, prop, filler);
    }
    protected Identifier getCardinalityTriplePredicate() {
        return Vocabulary.OWL_MAX_CARDINALITY.getIRI();
    }
    protected Identifier getQualifiedCardinalityTriplePredicate() {
        return Vocabulary.OWL_MAX_QUALIFIED_CARDINALITY.getIRI();
    }
}
