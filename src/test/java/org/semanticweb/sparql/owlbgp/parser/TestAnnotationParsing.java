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
import java.util.Collections;

import junit.framework.TestCase;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;

public class TestAnnotationParsing extends TestCase {
    public static final String LB = System.getProperty("line.separator") ;
    
    public TestAnnotationParsing() {
        super();
    }
    public TestAnnotationParsing(String name) {
        super(name);
    }

    public void testAnnos() throws Exception {
        String s="<http://example.org/x1> rdfs:label \"annoVal1\"^^xsd:string ."
            + "<http://example.org/x2> <http://example.org/annoProp> \"annoVal2\"^^xsd:string ."
            + "<http://example.org/annoProp> a owl:AnnotationProperty . "
            + "_:w a owl:Annotation ."
            + "_:w owl:annotatedSource <http://example.org/x2> ."
            + "_:w owl:annotatedProperty <http://example.org/annoProp> ."
            + "_:w owl:annotatedTarget \"annoVal2\"^^xsd:string ."
            +"_:w <http://www.w3.org/2000/01/rdf-schema#comment> \"annoAnnoVal\"^^xsd:string .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Annotation anno1=Annotation.create(AnnotationProperty.create("http://www.w3.org/2000/01/rdf-schema#label"), TypedLiteral.create("annoVal1", "", Datatype.XSD_STRING));
        Annotation annoAnno=Annotation.create(AnnotationProperty.create("http://www.w3.org/2000/01/rdf-schema#comment"), TypedLiteral.create("annoAnnoVal", "", Datatype.XSD_STRING));
        Annotation anno2=Annotation.create(AnnotationProperty.create("http://example.org/annoProp"), TypedLiteral.create("annoVal2", "", Datatype.XSD_STRING), Collections.singleton(annoAnno));
        Identifier anno1IRI=IRI.create("http://example.org/x1");
        Identifier anno2IRI=IRI.create("http://example.org/x2");
        Identifier annoAnnoNode=parser.string2AnonymousIndividual.get("w");
        assertTrue(consumer.ANN.get(annoAnnoNode).size()==1);
        assertTrue(consumer.ANN.get(annoAnnoNode).iterator().next()==annoAnno);
        assertTrue(consumer.ANN.get(anno1IRI).size()==1);
        assertTrue(consumer.ANN.get(anno1IRI).iterator().next()==anno1);
        assertTrue(consumer.ANN.get(anno2IRI).size()==1);
        assertTrue(consumer.ANN.get(anno2IRI).iterator().next()==anno2);
        assertTrue(consumer.allTriplesConsumed());
    }
}