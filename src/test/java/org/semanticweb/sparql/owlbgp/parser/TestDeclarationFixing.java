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
import org.semanticweb.sparql.owlbgp.model.axioms.Declaration;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseFunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.SymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;

public class TestDeclarationFixing extends AbstractTest {

    public void testDeclarations() throws Exception {
        String s="<http://example.org/ontProp> a owl:OntologyProperty ."
            + "<http://example.org/invFuncProp> a owl:InverseFunctionalProperty ."
            + "<http://example.org/transProp> rdf:type owl:TransitiveProperty ."
            + "<http://example.org/symProp> a owl:SymmetricProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().contains(Declaration.create(AnnotationProperty.create(IRI("ontProp")))));
        assertTrue(!consumer.containsTriple(IRI("ontProp"), Vocabulary.RDF_TYPE, Vocabulary.OWL_ONTOLOGY_PROPERTY));
        assertTrue(consumer.getAxioms().contains(Declaration.create(OP("invFuncProp"))));
        assertTrue(consumer.getAxioms().contains(InverseFunctionalObjectProperty.create(OP("invFuncProp"))));
        assertTrue(consumer.getAxioms().contains(Declaration.create(OP("transProp"))));
        assertTrue(consumer.getAxioms().contains(TransitiveObjectProperty.create(OP("transProp"))));
        assertTrue(consumer.getAxioms().contains(Declaration.create(OP("symProp"))));
        assertTrue(consumer.getAxioms().contains(SymmetricObjectProperty.create(OP("symProp"))));
        assertNoTriplesLeft(consumer);
    }
}