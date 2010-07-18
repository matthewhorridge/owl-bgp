package org.semanticweb.sparql.owlbgpparser;

import org.semanticweb.sparql.owlbgp.model.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.ILiteral;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.Individual;
import org.semanticweb.sparql.owlbgp.model.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.ObjectPropertyExpression;

public class TypeNegativePropertyAssertionHandler extends BuiltInTypeHandler {

    public TypeNegativePropertyAssertionHandler(OWLRDFConsumer consumer) {
        super(consumer, Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION.getIRI());
    }

    public boolean canHandleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        return false;
    }
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        Identifier source=consumer.getResourceObject(subject, Vocabulary.OWL_SOURCE_INDIVIDUAL.getIRI(), true);
        Individual sourceInd=consumer.translateIndividual(source);
        Identifier property=consumer.getResourceObject(subject, Vocabulary.OWL_ASSERTION_PROPERTY.getIRI(), true);
        Identifier target=consumer.getResourceObject(subject, Vocabulary.OWL_TARGET_INDIVIDUAL.getIRI(), true);
        consumer.translateAnnotations(subject);
        if (target!=null) { 
            ObjectPropertyExpression prop=consumer.translateObjectPropertyExpression(property);
            Individual targetInd=consumer.translateIndividual(target);
            addAxiom(NegativeObjectPropertyAssertion.create(prop,sourceInd,targetInd,consumer.getPendingAnnotations()));
        } else {
            DataPropertyExpression prop=consumer.translateDataPropertyExpression(property);
            ILiteral targetLit=consumer.getLiteralObject(subject, Vocabulary.OWL_TARGET_VALUE.getIRI(), true);
            addAxiom(NegativeDataPropertyAssertion.create(prop,sourceInd,targetLit,consumer.getPendingAnnotations()));
        }
        consumeTriple(subject, predicate, object);
    }
}