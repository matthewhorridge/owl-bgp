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
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointClasses;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPDisjointWithHandler extends TripleHandler {

    public TPDisjointWithHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        ClassExpression class1 = consumer.getCE(subject);
        ClassExpression class2 = consumer.getCE(object);
        String errorMessage = "";
        if (class1 == null)
            errorMessage = "Could not find a class expression for the subject in the triple " + subject + " " + predicate + " " + object + ". ";
        if (class2 == null)
            errorMessage += "Could not find a class expression for the object in the triple " + subject + " " + predicate + " " + object + ". ";
        if (class1 != null && class2 != null) {
            Set<ClassExpression> classes = new HashSet<ClassExpression>();
            classes.add(class1);
            classes.add(class2);
            consumer.addAxiom(DisjointClasses.create(classes, annotations));
        }
        else {
            throw new RuntimeException(errorMessage);
        }
    }
}
