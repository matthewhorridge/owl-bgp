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
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPSubClassOfHandler extends TripleHandler {

    public TPSubClassOfHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        ClassExpression subClass = consumer.getCE(subject);
        ClassExpression superClass = consumer.getCE(object);
        String errorMessage = "";
        if (subClass == null)
            errorMessage = "Could not find a subclass expression for the subject in the triple " + subject + " rdfs:subClassOf " + object + ". ";
        if (superClass == null)
            errorMessage += "Could not find a superclass expression for the object in the triple " + subject + " rdfs:subClassOf " + object + ". ";
        if (subClass != null && superClass != null)
            consumer.addAxiom(SubClassOf.create(subClass, superClass, annotations));
        else
            throw new RuntimeException(errorMessage);
    }
}
