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

package org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.DifferentIndividuals;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class AllDifferentHandler extends TripleHandler {

    public AllDifferentHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        super.handleStreaming(subject, predicate, object, false);
        consumer.addReifiedSubject(subject);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        Identifier pred = Vocabulary.OWL_MEMBERS;
        Identifier listMainNode = consumer.getObject(subject, pred, true);
        if (listMainNode == null) {
            pred = Vocabulary.OWL_DISTINCT_MEMBERS;
            listMainNode = consumer.getObject(subject, Vocabulary.OWL_DISTINCT_MEMBERS, true);
        }
        if (listMainNode == null)
            throw new RuntimeException("Error: There is not list with main node " + subject + ", which contains the individuals for the triple " + subject + " " + predicate + " " + object);
        else {
            Set<Individual> individualSet = consumer.translateToIndividualSet(listMainNode);
            if (individualSet != null && individualSet.size() > 0)
                if (individualSet.size() > 1)
                    consumer.addAxiom(DifferentIndividuals.create(individualSet, annotations));
                else
                    throw new RuntimeException("Error: A list representing the individuals of a DifferentIndividuals axiom has less than 2 items in it. The main triple is: " + subject + " " + predicate + " " + object + " . and " + listMainNode + " is the main node for the list. ");
            else
                throw new RuntimeException("Error: A list representing the classes of a DifferentIndividuals axiom could not be assembled. The main triple is: " + subject + " " + pred + " " + listMainNode + " and " + listMainNode + " is the main node for the list. ");
        }
    }
}
