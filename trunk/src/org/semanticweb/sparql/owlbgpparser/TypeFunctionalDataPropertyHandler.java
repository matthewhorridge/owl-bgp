package org.semanticweb.sparql.owlbgpparser;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalDataProperty;

public class TypeFunctionalDataPropertyHandler extends BuiltInTypeHandler {

    public TypeFunctionalDataPropertyHandler(OWLRDFConsumer consumer) {
        super(consumer, Vocabulary.OWL_FUNCTIONAL_DATA_PROPERTY.getIRI());
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        addAxiom(FunctionalDataProperty.create(translateDataProperty(subject)));
        consumeTriple(subject, predicate, object);
    }
}