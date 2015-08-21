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

package org.semanticweb.sparql.owlbgp.parser;

import java.io.StringReader;

import org.semanticweb.sparql.owlbgp.AbstractTest;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class TestPropertyParsing extends AbstractTest {

    public void testInverse() throws Exception {
        String s="<http://example.org/r> a owl:ObjectProperty ."
            + "_:x owl:inverseOf <http://example.org/r> . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI riri=IRI("r");
        ObjectProperty r=OP("r");
        ObjectPropertyExpression invr=IOP("r");
        Identifier invRnode=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getOPE(invRnode)==invr);
        assertTrue(consumer.getOPE(riri)==r);
        assertNoTriplesLeft(consumer);
    }
    public void testInverseWithVariable() throws Exception {
        String s="?r a owl:ObjectProperty ."
            + "_:x owl:inverseOf ?r . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Variable runtyped=V("?r");
        ObjectPropertyVariable rvar=OPV("?r");
        ObjectPropertyExpression invr=ObjectInverseOf.create(rvar);
        Identifier invRnode=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getOPE(invRnode)==invr);
        assertTrue(consumer.getOPE(runtyped)==rvar);
        assertNoTriplesLeft(consumer);
    }
    public void testInverseOfInverse() throws Exception {
        String s="<http://example.org/r> a owl:ObjectProperty ."
            + "_:x owl:inverseOf <http://example.org/r> . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI riri=IRI("r");
        ObjectProperty r=OP("r");
        ObjectPropertyExpression invr=IOP("r");
        Identifier xnode=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getOPE(riri)==r);
        assertTrue(consumer.getOPE(xnode)==invr);
        assertNoTriplesLeft(consumer);
    }
}