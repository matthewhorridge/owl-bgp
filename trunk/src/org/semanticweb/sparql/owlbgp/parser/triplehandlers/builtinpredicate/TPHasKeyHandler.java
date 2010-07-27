package org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.HasKey;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.translators.HasKeyListItemTranslator;
import org.semanticweb.sparql.owlbgp.parser.translators.OptimisedListTranslator;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TriplePredicateHandler;

public class TPHasKeyHandler extends TriplePredicateHandler {

    private OptimisedListTranslator<PropertyExpression> listTranslator;

    public TPHasKeyHandler(TripleConsumer consumer) {
        super(consumer, Vocabulary.OWL_HAS_KEY.getIRI());
        this.listTranslator=new OptimisedListTranslator<PropertyExpression>(consumer, new HasKeyListItemTranslator(consumer));
    }

    public boolean canHandleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        return false;
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        consumer.consumeTriple(subject, predicate, object);
        ClassExpression ce=consumer.translateClassExpression(subject);
        Set<PropertyExpression> props = listTranslator.translateToSet(object);
        consumer.addAxiom(HasKey.create(ce, props));
    }
}