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

package org.semanticweb.sparql.owlbgp.parser.translators;

import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;

public class IndividualListItemTranslator implements ListItemTranslator<Individual> {

    protected final TripleConsumer consumer;

    public IndividualListItemTranslator(TripleConsumer consumer) {
        this.consumer = consumer;
    }

    public Individual translate(Identifier firstObject) {
        if (firstObject instanceof IRI)
            return NamedIndividual.create((IRI) firstObject);
        else if (consumer.isAnonymous(firstObject))
            return AnonymousIndividual.create(firstObject.toString());
        else if (consumer.isVariable(firstObject))
            return IndividualVariable.create(firstObject.toString());
        else
            throw new IllegalArgumentException("Cannot translate list item to an individual because the object of the rdf:first triple is not an individual. ");
    }
}
