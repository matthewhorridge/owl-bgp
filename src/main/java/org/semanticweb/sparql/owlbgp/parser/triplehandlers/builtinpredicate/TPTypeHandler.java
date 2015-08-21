/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of OWL-BGP.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP. If not, see <http://www.gnu.org/licenses/>.
 */

package org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPTypeHandler extends TripleHandler {

    public TPTypeHandler(TripleConsumer consumer) {
        super(consumer);
    }

    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        if (!Vocabulary.BUILT_IN_VOCABULARY_IRIS.contains(object) && consumer.isAnonymous(object)) {
            // the object is a class or class expression and the subject an individual (the instance of the class)
            super.handleStreaming(object, predicate, Vocabulary.OWL_CLASS, false);
            if (consumer.isVariable(subject))
                consumer.mapIndividualIdentifierToindividual(subject, IndividualVariable.create(subject.toString()));
            else if (consumer.isAnonymous(subject))
                consumer.mapIndividualIdentifierToindividual(subject, AnonymousIndividual.create(subject.toString()));
            else
                consumer.mapIndividualIdentifierToindividual(subject, NamedIndividual.create(subject.toString()));
        }
        super.handleStreaming(subject, predicate, object, false);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        ClassExpression classexpression = consumer.getCE(object);
        Individual individual = consumer.getIND(subject);
        if (individual instanceof AnonymousIndividual) {
            IndividualVariable variableForAnonymousIndividual = IndividualVariable.create(individual.getIdentifierString());
            consumer.addVariableForAnonymousIndividual(variableForAnonymousIndividual);
            individual = variableForAnonymousIndividual;
        }
        String errorMessage = "";
        if (individual == null)
            errorMessage = "Could not find an individual for the subject in the triple " + subject + " " + predicate + " " + object + ". ";
        if (classexpression == null)
            errorMessage += "Could not find a class expression for the object in the triple " + subject + " " + predicate + " " + object + ". ";
        if (individual != null && classexpression != null)
            consumer.addAxiom(ClassAssertion.create(classexpression, individual, annotations));
        else
            throw new RuntimeException(errorMessage);
    }
}
