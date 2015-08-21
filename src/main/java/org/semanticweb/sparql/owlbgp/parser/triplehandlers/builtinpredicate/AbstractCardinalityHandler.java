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
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public abstract class AbstractCardinalityHandler extends TripleHandler {

    protected final Identifier predicateIRI;

    public AbstractCardinalityHandler(TripleConsumer consumer, Identifier predicateIRI) {
        super(consumer);
        this.predicateIRI = predicateIRI;
    }

    protected PropertyExpression getPropertyExpression(Identifier subject) {
        Identifier propID = consumer.getObject(subject, Vocabulary.OWL_ON_PROPERTY, true);
        PropertyExpression pe = consumer.getOPE(propID);
        if (pe != null)
            return pe;
        else {
            pe = consumer.getDPE(propID);
            if (pe != null)
                return pe;
        }
        // TODO: error handling
        throw new RuntimeException("error");
    }

    protected ClassExpression getClassExpression(Identifier subject) {
        Identifier classID = consumer.getObject(subject, Vocabulary.OWL_ON_CLASS, true);
        if (classID == null) return null;
        consumer.translateClassExpression(classID);
        return consumer.getCE(classID);
    }

    protected DataRange getDataRange(Identifier subject) {
        Identifier dataRangeID = consumer.getObject(subject, Vocabulary.OWL_ON_DATA_RANGE, true);
        if (dataRangeID == null) return null;
        consumer.translateDataRange(dataRangeID);
        return consumer.getDR(dataRangeID);
    }

    protected int getCardinality(Identifier object) {
        if (!(object instanceof TypedLiteral)) {
            throw new IllegalArgumentException("Error: The term " + object + " is supposed to be a non-negative integer, i.e., a literal with datatype xsd:nonNegativeInteger, because it represents the number in a number restriction and occurs in a triple with predicate " + predicateIRI + ". ");
        }
        else {
            TypedLiteral lit = (TypedLiteral) object;
            if (lit.getDatatype() != Datatype.XSD_NON_NEGATIVE_INTEGER) {
                // TODO: error handling
                throw new IllegalArgumentException("Error: The term " + object + " is supposed to be a non-negative integer, i.e., a literal with datatype xsd:nonNegativeInteger, because it represents the number in a number restriction and occurs in a triple with predicate " + predicateIRI + ". Here the datatype is: " + lit.getDatatype());
            }
            else {
                try {
                    int n = Integer.parseInt(lit.getLexicalForm());
                    if (n < 0)
                        throw new IllegalArgumentException("Error: The literal object " + lit + " in a triple with predicate " + predicateIRI + " denotes an integer smaller than zero, which is not allowed for numbers in a number restriction. ");
                    else
                        return n;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Error: The literal object " + lit + " in a triple with predicate " + predicateIRI + " cannot be parsed into an integer, which is required since it denotes a number in a number restriction. ");
                }
            }
        }
    }
}
