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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPSameAsHandler extends TripleHandler {

    public TPSameAsHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        Individual ind1 = consumer.getIND(subject);
        Individual ind2 = consumer.getIND(object);
        String errorMessage = "";
        if (ind1 == null)
            errorMessage = "Could not find an individual for the subject in the triple " + subject + " " + predicate + " " + object + ". ";
        if (ind2 == null)
            errorMessage += "Could not find an individual for the object in the triple " + subject + " " + predicate + " " + object + ". ";
        if (ind1 != null && ind2 != null) {
            Set<Individual> inds = new HashSet<Individual>();
            inds.add(ind1);
            inds.add(ind2);
            consumer.addAxiom(SameIndividual.create(inds, annotations));
        }
        else {
            throw new RuntimeException(errorMessage);
        }
    }
}
