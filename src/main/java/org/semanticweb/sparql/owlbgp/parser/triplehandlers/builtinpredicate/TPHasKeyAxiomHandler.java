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
import java.util.List;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.HasKey;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPHasKeyAxiomHandler extends TripleHandler {

    public TPHasKeyAxiomHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        //T(CE) owl:hasKey T(SEQ OPE1 ... OPEm DPE1 ... DPEn ) .
        ClassExpression cls = consumer.getCE(subject);
        if (cls == null)
            throw new RuntimeException("Could not find  class expression for the subject in the triple " + subject + " " + Vocabulary.OWL_HAS_KEY + " " + object + ". ");
        else {
            List<PropertyExpression> properties = consumer.translateToPropertyExpressionList(object);
            if (properties == null || properties.size() == 0)
                throw new RuntimeException("Error: A list representing the properties of a HasKey axiom could not be assembled. The main triple is: " + subject + " " + Vocabulary.OWL_HAS_KEY.toString() + " " + object + " and " + object + " is the main node for the list. ");
            else {
                for (PropertyExpression pe : properties)
                    if (!(pe instanceof ObjectPropertyExpression) && !(pe instanceof DataPropertyExpression))
                        throw new RuntimeException("Error: A list representing object and data properties of a HasKey axiom contain a property that is neither an object nor a data property expression. The main triple is: " + subject + " " + Vocabulary.OWL_HAS_KEY.toString() + " " + object + " and " + object + " is the main node for the list. ");
                consumer.addAxiom(HasKey.create(cls, new HashSet<PropertyExpression>(properties), annotations));
            }
        }
    }

}
