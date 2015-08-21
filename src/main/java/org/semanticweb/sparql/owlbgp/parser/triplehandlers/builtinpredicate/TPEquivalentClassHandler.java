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
import org.semanticweb.sparql.owlbgp.model.axioms.DatatypeDefinition;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPEquivalentClassHandler extends TripleHandler {

    public TPEquivalentClassHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        ClassExpression class1 = consumer.getCE(subject);
        DataRange dr1 = null;
        if (class1 == null)
            dr1 = consumer.getDR(subject);
        if (class1 == null && dr1 == null)
            throw new RuntimeException("Could find neither a data range nor a class expression for the subject in the triple " + subject + " " + predicate + " " + object + ". ");

        ClassExpression class2 = consumer.getCE(object);
        DataRange dr2 = null;
        if (class2 == null)
            dr2 = consumer.getDR(object);
        if (class2 == null && dr2 == null)
            throw new RuntimeException("Could find neither a data range nor a class expression for the object in the triple " + subject + " " + predicate + " " + object + ". ");

        if (class1 != null && class2 != null) {
            Set<ClassExpression> classes = new HashSet<ClassExpression>();
            classes.add(class1);
            classes.add(class2);
            consumer.addAxiom(EquivalentClasses.create(classes, annotations));
        }
        else if (dr1 != null && dr2 != null) {
            if (dr1 instanceof Datatype)
                consumer.addAxiom(DatatypeDefinition.create((Datatype) dr1, dr2, annotations));
            else if (dr1 instanceof DatatypeVariable)
                consumer.addAxiom(DatatypeDefinition.create((DatatypeVariable) dr1, dr2, annotations));
            else {
                throw new RuntimeException("The data range for the subject in the triple " + subject + " " + predicate + " " + object + " is not a datatype or a datatype variable as required, but a complex data range: " + dr1);
            }
        }
        else if (class1 != null)
            throw new RuntimeException("Error: The subject of the triple " + subject + " " + predicate + " " + object + "  was tranlated into a class expression, but the object into a data range, which is not allowed in OWL DL. ");
        else
            throw new RuntimeException("Error: The object of the triple " + subject + " " + predicate + " " + object + "  was tranlated into a class expression, but the subject into a data range, which is not allowed in OWL DL. ");

    }
}
