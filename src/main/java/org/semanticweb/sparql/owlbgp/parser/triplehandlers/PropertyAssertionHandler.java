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

package org.semanticweb.sparql.owlbgp.parser.triplehandlers;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;

public class PropertyAssertionHandler extends TripleHandler {

    public PropertyAssertionHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        Individual individual = consumer.getIND(subject);
        if (individual instanceof AnonymousIndividual) {
            IndividualVariable variableForAnonymousIndividual = IndividualVariable.create(individual.getIdentifierString());
            consumer.addVariableForAnonymousIndividual(variableForAnonymousIndividual);
            individual = variableForAnonymousIndividual;
        }
        ObjectPropertyExpression ope = consumer.getOPE(predicate);
        if (ope != null) {
            Individual individual2 = consumer.getIND(object);
            if (individual2 != null) {
                if (individual2 instanceof AnonymousIndividual) {
                    IndividualVariable variableForAnonymousIndividual = IndividualVariable.create(individual2.getIdentifierString());
                    consumer.addVariableForAnonymousIndividual(variableForAnonymousIndividual);
                    individual2 = variableForAnonymousIndividual;
                }
                consumer.addAxiom(ObjectPropertyAssertion.create(ope, individual, individual2, annotations));
            }
            else
                throw new RuntimeException("Could not find an individual for the object in the triple " + subject + " " + predicate + " " + object + ", but " + predicate + " is an object property. ");
        }
        else {
            DataPropertyExpression dpe = consumer.getDPE(predicate);
            if (dpe != null) {
                Literal literal = null;
                if (object instanceof UntypedVariable)
                    literal = LiteralVariable.create((UntypedVariable) object);
                else if (object instanceof Literal)
                    literal = (Literal) object;
                if (literal == null)
                    throw new RuntimeException("It seems " + object + " is not a literal although the triple " + subject + " " + predicate + " " + object + " seems to be a data property assertion since " + predicate + " is a data property. ");
                consumer.addAxiom(DataPropertyAssertion.create(dpe, individual, literal, annotations));
            }
            else {
                throw new RuntimeException("Could find neither a data nor an object property for the predicate in the triple " + subject + " " + predicate + " " + object + ". ");
            }
        }
    }
}
