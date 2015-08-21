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
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class NegativePropertyAssertionHandler extends TripleHandler {

    public NegativePropertyAssertionHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        super.handleStreaming(subject, predicate, object, false);
        consumer.addReifiedSubject(subject);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        Identifier[] triple = consumer.getReifiedTriple(subject, object);
        if (triple == null)
            throw new RuntimeException("Error: The triple for a negative property assertion could not be reified. The subject of the reification triples is " + subject + ", which should lead to the reified triple for the triple " + subject + " " + predicate + " " + object);
        else {
            Individual ind = consumer.getIND(triple[0]);
            if (ind == null)
                throw new RuntimeException("There is no individual for the identifier " + triple[0] + ", which should be the individual of a negative property assertion represented by the reified triple " + triple[0] + " " + triple[1] + " " + triple[2] + " . ");
            if (triple[2] instanceof UntypedVariable || triple[2] instanceof Literal) {
                Literal literal = null;
                if (triple[2] instanceof UntypedVariable)
                    literal = LiteralVariable.create((UntypedVariable) triple[2]);
                else
                    literal = (Literal) triple[2];
                DataPropertyExpression dpe = consumer.getDPE(triple[1]);
                if (dpe == null)
                    throw new RuntimeException("There is no data property for the identifier " + triple[1] + ", which should be the data property of a negative data property assertion represented by the reified triple " + triple[0] + " " + triple[1] + " " + triple[2] + " . ");
                consumer.addAxiom(NegativeDataPropertyAssertion.create(dpe, ind, literal, annotations));
            }
            else {
                ObjectPropertyExpression ope = consumer.getOPE(triple[1]);
                if (ope == null)
                    throw new RuntimeException("There is no object property for the identifier " + triple[1] + ", which should be the object property of a negative object property assertion represented by the reified triple " + triple[0] + " " + triple[1] + " " + triple[2] + " . ");
                Individual ind2 = consumer.getIND(triple[2]);
                if (ind2 == null)
                    throw new RuntimeException("There is no individual for the identifier " + triple[1] + ", which should be the second individual of a negative object property assertion represented by the reified triple " + triple[0] + " " + triple[1] + " " + triple[2] + " . ");
                consumer.addAxiom(NegativeObjectPropertyAssertion.create(ope, ind, ind2, annotations));
            }
        }
    }
}
