package org.semanticweb.sparql.evaluation.queryobjects;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;

public class QO_TransitiveObjectProperty extends QO_ObjectPropertyAxiom<TransitiveObjectProperty> {

    public QO_TransitiveObjectProperty(TransitiveObjectProperty axiomTemplate) {
	    super(axiomTemplate);
	}
    protected OWLAxiom getEntailmentAxiom(OWLDataFactory dataFactory, OWLObjectPropertyExpression ope) {
        return dataFactory.getOWLTransitiveObjectPropertyAxiom(ope);
    }
}