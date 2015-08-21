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
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.AbstractTest;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataComplementOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataOneOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction.OWL2_FACET;

public class TestDataRangeParsing extends AbstractTest {
    
    public void testDatatypeRestriction() throws Exception {
        String s="_:x a rdfs:Datatype ."
            + "_:x owl:onDatatype xsd:int . "
            + "_:x owl:withRestrictions (_:z1 _:z2) . "
            + "_:z1 xsd:minInclusive \"1\"^^xsd:int . "
            + "_:z2 xsd:maxInclusive \"5\"^^xsd:int . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Set<FacetRestriction> facets=new HashSet<FacetRestriction>();
        facets.add(FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("1", "", Datatype.XSD_INT)));
        facets.add(FacetRestriction.create(OWL2_FACET.MAX_INCLUSIVE, TL("5", "", Datatype.XSD_INT)));
        DataRange restriction=DatatypeRestriction.create(Datatype.XSD_INT,facets);
        Identifier restrictionIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getDR(restrictionIRI)==restriction);
        assertNoTriplesLeft(consumer);
    }
    public void testOneOf() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:oneOf ( \"1\"^^xsd:int \"abc\" \"abc@\"^^rdf:PlainLiteral \"abc\"@ \"abc\"@en \"abc@en\"^^rdf:PlainLiteral ) .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataRange oneOf=DataOneOf.create(TL("1", "", Datatype.XSD_INT), TL("abc", "", Datatype.RDF_PLAIN_LITERAL), TL("abc", "en", Datatype.RDF_PLAIN_LITERAL));
        Identifier oneOfIRI=parser.string2AnonymousIndividual.get("x");        
        assertTrue(consumer.getDR(oneOfIRI)==oneOf);
        assertNoTriplesLeft(consumer);
    }
    public void testComplicatedComplement() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:x rdf:type rdfs:Datatype ."
            + "_:x owl:datatypeComplementOf _:d ."
            + "_:d rdf:type rdfs:Datatype ."
            + "_:d owl:unionOf _:d1 ."
            + "_:d1 rdf:first _:c . "
            + "_:c rdf:type rdfs:Datatype ."
            + "_:c owl:intersectionOf _:c1 ."
            + "_:c1 rdf:first xsd:int . "
            + "_:c1 rdf:rest _:c2 . "
            + "_:c2 rdf:first xsd:short . "
            + "_:c2 rdf:rest rdf:nil . "
            + "_:d1 rdf:rest _:d2 . "
            + "_:d2 rdf:first ?short . "
            + "_:d2 rdf:rest _:d3 ."
            + "_:d3 rdf:first xsd:byte . "
            + "_:d3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        IRI shortIRI=IRI("xsd:short");
        IRI byteIRI=IRI("xsd:byte");
        UntypedVariable shortVar=V("?short");
        Identifier complementIRI=parser.string2AnonymousIndividual.get("x");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("d");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("c");
        DataRange intDT=DT(intIRI);
        DataRange byteDT=DT(byteIRI);
        DataRange shortDT=DT(shortIRI);
        DataRange shortVarDT=DTV(shortVar);
        DataRange intersection=DataIntersectionOf.create(intDT, shortDT);
        DataRange union=DataUnionOf.create(intersection, shortVarDT, byteDT);
        DataRange complement=DataComplementOf.create(union);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(shortIRI)==shortDT);
        assertTrue(consumer.getDR(shortVar)==shortVarDT);
        assertTrue(consumer.getDR(intersectionIRI)==intersection);
        assertTrue(consumer.getDR(unionIRI)==union);
        assertTrue(consumer.getDR(complementIRI)==complement);
        assertNoTriplesLeft(consumer);
    }
    public void testDoubleComplement() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:datatypeComplementOf _:y ."
            + "_:y rdf:type rdfs:Datatype ."
            + "_:y owl:datatypeComplementOf xsd:int .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        Identifier complement1IRI=parser.string2AnonymousIndividual.get("x");
        Identifier complement2IRI=parser.string2AnonymousIndividual.get("y");
        DataRange intDT=DT(intIRI);
        DataRange complement1=DataComplementOf.create(intDT);
        DataRange complement2=DataComplementOf.create(complement1);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(complement2IRI)==complement1);
        assertTrue(consumer.getDR(complement1IRI)==complement2);
        assertNoTriplesLeft(consumer);
    }
    public void testComplement() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:datatypeComplementOf xsd:int .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        Identifier complementIRI=parser.string2AnonymousIndividual.get("x");
        DataRange intDT=DT(intIRI);
        DataRange complement=DataComplementOf.create(intDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(complementIRI)==complement);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionIntersection() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:d rdf:type rdfs:Datatype ."
            + "_:d owl:unionOf _:d1 ."
            + "_:d1 rdf:first _:c . "
            + "_:c rdf:type rdfs:Datatype ."
            + "_:c owl:intersectionOf _:c1 ."
            + "_:c1 rdf:first xsd:int . "
            + "_:c1 rdf:rest _:c2 . "
            + "_:c2 rdf:first xsd:short . "
            + "_:c2 rdf:rest rdf:nil . "
            + "_:d1 rdf:rest _:d2 . "
            + "_:d2 rdf:first ?short . "
            + "_:d2 rdf:rest _:d3 ."
            + "_:d3 rdf:first xsd:byte . "
            + "_:d3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        IRI shortIRI=IRI("xsd:short");
        IRI byteIRI=IRI("xsd:byte");
        UntypedVariable shortVar=V("?short");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("d");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("c");
        
        DataRange intDT=DT(intIRI);
        DataRange byteDT=Datatype.create(byteIRI);
        DataRange shortDT=Datatype.create(shortIRI);
        DataRange shortVarDT=DTV(shortVar);
        DataRange intersection=DataIntersectionOf.create(intDT, shortDT);
        DataRange union=DataUnionOf.create(intersection, shortVarDT, byteDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(shortIRI)==shortDT);
        assertTrue(consumer.getDR(shortVar)==shortVarDT);
        assertTrue(consumer.getDR(intersectionIRI)==intersection);
        assertTrue(consumer.getDR(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionOtherSyntax() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:x rdf:type rdfs:Datatype ."
            + "_:x owl:unionOf (xsd:int ?short xsd:byte) .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        UntypedVariable shortVar=V("?short");
        IRI byteIRI=IRI("xsd:byte");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        DataRange intDT=Datatype.create(intIRI);
        DataRange shortDT=DTV(shortVar);
        DataRange byteDT=DT(byteIRI);
        DataRange union=DataUnionOf.create(intDT, shortDT, byteDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(shortVar)==shortDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionOneVarConjunct() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:x rdf:type rdfs:Datatype ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first ?short . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        UntypedVariable shortVar=V("?short");
        DataRange shortDT=DTV(shortVar);
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getDR(shortVar)==shortDT);
        assertTrue(consumer.getDR(unionIRI)==shortDT);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionOneConjunct() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first xsd:int . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        DataRange intDT=DT(intIRI);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(unionIRI)==intDT);
        assertNoTriplesLeft(consumer);
    }
    public void testUnion2() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:x rdf:type rdfs:Datatype ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first xsd:int . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first ?short . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first xsd:byte . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        UntypedVariable shortVar=V("?short");
        IRI byteIRI=IRI("xsd:byte");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        DataRange intDT=DT(intIRI);
        DataRange shortDT=DTV(shortVar);
        DataRange byteDT=DT(byteIRI);
        DataRange union=DataUnionOf.create(intDT, shortDT, byteDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(shortVar)==shortDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testUnion() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first xsd:int . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first xsd:short . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first xsd:byte . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        IRI shortIRI=IRI("xsd:short");
        IRI byteIRI=IRI("xsd:byte");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        Datatype intDT=DT(intIRI);
        Datatype shortDT=DT(shortIRI);
        Datatype byteDT=DT(byteIRI);
        DataRange union=DataUnionOf.create(intDT, shortDT, byteDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(shortIRI)==shortDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersectionOneVarConjunct() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:x rdf:type rdfs:Datatype ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first ?short . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        UntypedVariable shortVar=V("?short");
        DataRange shortDT=DTV(shortVar);
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getDR(shortVar)==shortDT);
        assertTrue(consumer.getDR(intersectionIRI)==shortDT);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersectionOneConjunct() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first xsd:int . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        DataRange intDT=DT(intIRI);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(intersectionIRI)==intDT);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersection2() throws Exception {
        String s="?short rdf:type rdfs:Datatype ."
            + "_:x rdf:type rdfs:Datatype ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first xsd:int . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first ?short . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first xsd:byte . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        UntypedVariable shortVar=V("?short");
        IRI byteIRI=IRI("xsd:byte");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        DataRange intDT=DT(intIRI);
        DataRange shortDT=DTV(shortVar);
        DataRange byteDT=DT(byteIRI);
        DataRange intersection=DataIntersectionOf.create(intDT, shortDT, byteDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(shortVar)==shortDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(intersectionIRI)==intersection);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersection() throws Exception {
        String s="_:x rdf:type rdfs:Datatype ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first xsd:int . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first xsd:short . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first xsd:byte . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI intIRI=IRI("xsd:int");
        IRI shortIRI=IRI("xsd:short");
        IRI byteIRI=IRI("xsd:byte");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        Datatype intDT=DT(intIRI);
        Datatype shortDT=DT(shortIRI);
        Datatype byteDT=DT(byteIRI);
        DataRange intersection=DataIntersectionOf.create(intDT, shortDT, byteDT);
        assertTrue(consumer.getDR(intIRI)==intDT);
        assertTrue(consumer.getDR(shortIRI)==shortDT);
        assertTrue(consumer.getDR(byteIRI)==byteDT);
        assertTrue(consumer.getDR(intersectionIRI)==intersection);
        assertNoTriplesLeft(consumer);
    }
    public void testCustomDatatype() throws Exception {
        String s="<http://example.org/datatype> a rdfs:Datatype .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI datatypeIRI=IRI("http://example.org/datatype");
        Datatype datatype=DT(datatypeIRI);
        assertTrue(consumer.getDR(datatypeIRI)==datatype);
    }
    public void testDatatypeVariable() throws Exception {
        String s="?dt a rdfs:Datatype .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Identifier datatypeVar=V("?dt");
        DataRange dataRange=DTV("?dt");
        assertTrue(consumer.getDR(datatypeVar)==dataRange);
    }
}