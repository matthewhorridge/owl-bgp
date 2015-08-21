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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointObjectProperties;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class AllDisjointPropertiesHandler extends TripleHandler {

    public AllDisjointPropertiesHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        super.handleStreaming(subject, predicate, object, false);
        consumer.addReifiedSubject(subject);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        Identifier listMainNode = consumer.getObject(subject, Vocabulary.OWL_MEMBERS, true);
        if (listMainNode == null)
            throw new RuntimeException("Error: There is not list with main node " + subject + ", which contains the classes for the triple " + subject + " " + predicate + " " + object);
        else {
            Set<PropertyExpression> peSet = consumer.translateToPropertyExpressionSet(listMainNode);
            if (peSet != null && peSet.size() > 0) {
                Set<ObjectPropertyExpression> opes = new HashSet<ObjectPropertyExpression>();
                Set<DataPropertyExpression> dpes = new HashSet<DataPropertyExpression>();
                for (PropertyExpression pe : peSet)
                    if (pe instanceof ObjectPropertyExpression)
                        opes.add((ObjectPropertyExpression) pe);
                    else if (pe instanceof DataPropertyExpression)
                        dpes.add((DataPropertyExpression) pe);
                    else
                        throw new RuntimeException("Error: A list representing the properties of a disjoint properties axiom contains a property that is neither an object property expression nor a data property expression. The main triple is: " + subject + " " + predicate + " " + object + " and " + object + " is the main node for the list. ");
                if (opes.size() > 1) {
                    if (dpes.size() > 1)
                        throw new RuntimeException("Error: A list representing the properties of a disjoint properties axiom contains both data and object property expressions. The main triple is: " + subject + " " + predicate + " " + object + " and " + object + " is the main node for the list. ");
                    else
                        consumer.addAxiom(DisjointObjectProperties.create(opes, annotations));
                }
                else if (dpes.size() > 1) {
                    consumer.addAxiom(DisjointDataProperties.create(dpes, annotations));
                }
                else
                    throw new RuntimeException("Error: A list representing the properties of a disjoint properties axiom has less than 2 items in it. The main triple is: " + subject + " " + Vocabulary.OWL_MEMBERS.toString() + " " + listMainNode + " and " + listMainNode + " is the main node for the list. ");
            }
            else
                throw new RuntimeException("Error: A list representing the properties for a DisjointObjectProperties or DisjointDataProperties axiom could not be assembled. The main triple is: " + subject + " " + Vocabulary.OWL_MEMBERS.toString() + " " + listMainNode + " and " + listMainNode + " is the main node for the list. ");
        }
    }
}
