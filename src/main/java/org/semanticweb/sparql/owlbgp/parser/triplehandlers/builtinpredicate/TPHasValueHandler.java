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

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPHasValueHandler extends TripleHandler {

    public TPHasValueHandler(TripleConsumer consumer) {
        super(consumer);
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        Identifier propID = consumer.getObject(subject, Vocabulary.OWL_ON_PROPERTY, true);
        if (object instanceof Literal) {
            DataPropertyExpression dpe = consumer.getDPE(propID);
            if (dpe != null) {
                consumer.mapClassIdentifierToClassExpression(subject, DataHasValue.create(dpe, (Literal) object));
            }
            else {
                // TODO: error handling
                throw new RuntimeException("error");
            }
        }
        else {
            ObjectPropertyExpression ope = consumer.getOPE(propID);
            if (ope != null) {
                if (consumer.isAnonymous(object))
                    throw new IllegalArgumentException("Error: The individuals in a hasValue expression cannot be anonymous, but here we have the triple: " + subject + " " + predicate + " " + object);
                Individual ind = consumer.getIND(object);
                consumer.mapClassIdentifierToClassExpression(subject, ObjectHasValue.create(ope, ind));
            }
            else {
                // TODO: error handling
                throw new RuntimeException("error");
            }
        }
    }
}
