package org.semanticweb.sparql.owlbgpparser;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.DatatypeDefinition;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;

public class TPEquivalentClassHandler extends TriplePredicateHandler {

    public TPEquivalentClassHandler(OWLRDFConsumer consumer) {
        super(consumer, Vocabulary.OWL_EQUIVALENT_CLASS.getIRI());
    }

    public boolean canHandleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        // Can handle when streaming if the subject or object are named
        boolean named = (consumer.isClass(subject)) && !isSubjectOrObjectAnonymous(subject, object);
        return named || consumer.getClassExpressionIfTranslated(subject)!=null && consumer.getClassExpressionIfTranslated(object)!=null;
    }
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        // Can handle because the IRIs can easily be translated to classes
        if (consumer.isDataRange(object) || consumer.isDataRange(subject)) {
            Datatype datatype=Datatype.create((IRI)subject);
            DataRange dataRange=consumer.translateDataRange(object);
            addAxiom(DatatypeDefinition.create(datatype, dataRange));
        } else {
            Set<ClassExpression> operands=new HashSet<ClassExpression>();
            operands.add(translateClassExpression(subject));
            operands.add(translateClassExpression(object));
            addAxiom(EquivalentClasses.create(operands));
        }
        consumeTriple(subject, predicate, object);
    }
}
