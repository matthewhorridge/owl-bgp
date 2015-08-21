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

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPObjectOneOfHandler extends TripleHandler {

    public TPObjectOneOfHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        Set<Individual> individualSet = consumer.translateToIndividualSet(object);
        for (Individual ind : individualSet)
            if (consumer.isAnonymous(ind))
                throw new IllegalArgumentException("Error: The individuals in a oneOf expression cannot be anonymous, but here we have " + ind + " occurring in the list for the triple: " + subject + " " + predicate + " " + object);
        if (individualSet != null && individualSet.size() > 0)
            consumer.mapClassIdentifierToClassExpression(subject, ObjectOneOf.create(individualSet));
        else {
            // TODO: error handling
            System.err.println("error");
        }
    }
}
