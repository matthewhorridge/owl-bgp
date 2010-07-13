package org.semanticweb.sparql.owlbgpparser;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.DisjointClasses;

public class TypeAllDisjointClassesHandler extends BuiltInTypeHandler {

    public TypeAllDisjointClassesHandler(OWLRDFConsumer consumer) {
        super(consumer, Vocabulary.OWL_ALL_DISJOINT_CLASSES.getIRI());
    }
    public void handleTriple(String subject, String predicate, String object) {
        consumeTriple(subject, predicate, object);
        String listNode=consumer.getResourceObject(subject, Vocabulary.OWL_MEMBERS.getIRI(), true);
        if (listNode!=null) {
            Set<ClassExpression> desc=consumer.translateToClassExpressionSet(listNode);
            Set<Annotation> annotations=consumer.translateAnnotations(subject);
            addAxiom(DisjointClasses.create(desc,annotations));
        }
    }
    public boolean canHandleStreaming(String subject, String predicate, String object) {
        return false;
    }
}
