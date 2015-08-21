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
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectComplementOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasSelf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataUnionOf;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class TestClassExpressionParsing extends AbstractTest {
    public static final String LB = System.getProperty("line.separator") ;
    
    public void testClassAssertion() throws Exception {
        String s="<http://example.org/hasChild> rdf:type owl:ObjectProperty . "+LB+
                "?parent rdf:type [ "+LB+
                " rdf:type owl:Restriction ; "+LB+
                " owl:onProperty  <http://example.org/hasChild> ; "+LB+
                " owl:minCardinality \"1\"^^xsd:nonNegativeInteger ] ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertNoTriplesLeft(consumer);
    }
    
    public void testOWL1Classes() throws Exception {
        String s="_:v rdf:type owl:Class . "
            + "_:v owl:unionOf _:u1 . "
            + "_:u1 rdf:first <http://example.org/C> . "
            + "_:u1 rdf:rest _:u2 . "
            + "_:u2 rdf:first <http://example.org/D> . "
            + "_:u2 rdf:rest rdf:nil . "
            + "<http://example.org/C> a owl:Class . "
            + "<http://example.org/D> a owl:Class . "
            + "_:x rdf:type owl:Class . "
            + "_:x owl:unionOf <http://example.org/C> . "
            + "_:y rdf:type owl:Class . "
            + "_:y owl:unionOf rdf:nil . "
            + "_:z rdf:type owl:Class . "
            + "_:z owl:intersectionOf <http://example.org/C> . "
            + "_:w rdf:type owl:Class . "
            + "_:w owl:intersectionOf rdf:nil . "
            + "_:u rdf:type owl:Class . "
            + "_:u owl:intersectionOf _:i1 . "
            + "_:i1 rdf:first <http://example.org/C> . "
            + "_:i1 rdf:rest _:i2 . "
            + "_:i2 rdf:first <http://example.org/D> . "
            + "_:i2 rdf:rest rdf:nil . "
            + "_:o rdf:type owl:Class . "
            + "_:o owl:oneOf rdf:nil . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        Identifier unionNilIRI=parser.string2AnonymousIndividual.get("y");
        Identifier intersecionIRI=parser.string2AnonymousIndividual.get("z");
        Identifier intersecionNilIRI=parser.string2AnonymousIndividual.get("w");
        Identifier uIRI=parser.string2AnonymousIndividual.get("u");
        Identifier vIRI=parser.string2AnonymousIndividual.get("v");
        Identifier oneOfNilIRI=parser.string2AnonymousIndividual.get("o");
        Clazz cClass=C(cIRI);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(unionIRI)==cClass);
        assertTrue(consumer.getCE(unionNilIRI)==Clazz.NOTHING);
        assertTrue(consumer.getCE(intersecionIRI)==cClass);
        assertTrue(consumer.getCE(intersecionNilIRI)==Clazz.THING);
        assertTrue(consumer.getCE(uIRI)==ObjectIntersectionOf.create(C("C"), C("D")));
        assertTrue(consumer.getCE(vIRI)==ObjectUnionOf.create(C("C"), C("D")));
        assertTrue(consumer.getCE(oneOfNilIRI)==Clazz.NOTHING);
        assertNoTriplesLeft(consumer);
    }
    public void testDataMaxQualifiedCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:maxQualifiedCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "_:x owl:onDataRange xsd:integer ."
            + "<http://example.org/r> a owl:DatatypeProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty r=DP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=DataMaxCardinality.create(3,r,DT("xsd:integer"));
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.DPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testDataMinQualifiedCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:minQualifiedCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "_:x owl:onDataRange xsd:integer ."
            + "<http://example.org/r> a owl:DatatypeProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty r=DP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=DataMinCardinality.create(3,r,DT("xsd:integer"));
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.DPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testDataExactQualifiedCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:qualifiedCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:DatatypeProperty ."
            + "_:x owl:onDataRange _:dr ."
            + "_:dr rdf:type rdfs:Datatype ."
            + "_:dr owl:unionOf (xsd:int xsd:byte) .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty r=DP("r");
        IRI rIRI=IRI("r");
        DataRange dr=DataUnionOf.create(DT("xsd:int"), DT("xsd:byte"));
        ClassExpression cardi=DataExactCardinality.create(3,r,dr);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        Identifier drIRI=parser.string2AnonymousIndividual.get("dr");
        assertTrue(consumer.DPE.get(rIRI)==r);
        assertTrue(consumer.DR.get(drIRI)==dr);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectMaxQualifiedCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:maxQualifiedCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "_:x owl:onClass <http://example.org/C> ."
            + "<http://example.org/r> a owl:ObjectProperty ."
            + "<http://example.org/C> a owl:Class .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        Clazz c=C("C");
        IRI cIRI=IRI("C");
        ClassExpression cardi=ObjectMaxCardinality.create(3,r,c);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cIRI)==c);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectMinQualifiedCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onClass _:c ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:minQualifiedCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:ObjectProperty ."
            + "<http://example.org/C> a owl:Class ."
            + "<http://example.org/D> a owl:Class ."
            + "_:c a owl:Class ."
            + "_:c owl:intersectionOf (<http://example.org/C> <http://example.org/D>) .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        Clazz c=C("C");
        IRI cIRI=IRI("C");
        Clazz d=C("D");
        IRI dIRI=IRI("D");
        ClassExpression conj=ObjectIntersectionOf.create(c, d);
        ClassExpression cardi=ObjectMinCardinality.create(3,r,conj);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        Identifier conjIRI=parser.string2AnonymousIndividual.get("c");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cIRI)==c);
        assertTrue(consumer.getCE(dIRI)==d);
        assertTrue(consumer.getCE(conjIRI)==conj);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectExactQualifiedCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:qualifiedCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:ObjectProperty ."
            + "_:x owl:onClass <http://example.org/C> ."
            + "<http://example.org/C> a owl:Class .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        Clazz c=C("C");
        IRI cIRI=IRI("C");
        ClassExpression cardi=ObjectExactCardinality.create(3,r,c);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cIRI)==c);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testDataMaxCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:maxCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:DatatypeProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty r=DP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=DataMaxCardinality.create(3,r);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.DPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testDataMinCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:minCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:DatatypeProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty r=DP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=DataMinCardinality.create(3,r);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.DPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testDataExactCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:cardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:DatatypeProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty r=DP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=DataExactCardinality.create(3,r);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.DPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectMaxCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:maxCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:ObjectProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=ObjectMaxCardinality.create(3,r);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectMinCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:minCardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:ObjectProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=ObjectMinCardinality.create(3,r);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectExactCardinality() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:cardinality \"3\"^^xsd:nonNegativeInteger ."
            + "<http://example.org/r> a owl:ObjectProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        ClassExpression cardi=ObjectExactCardinality.create(3,r);
        Identifier cardiIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(cardiIRI)==cardi);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectHasSelf() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:hasSelf \"true\"^^xsd:boolean ."
            + "<http://example.org/r> a owl:ObjectProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectProperty r=OP("r");
        IRI rIRI=IRI("r");
        ClassExpression hasSelf=ObjectHasSelf.create(r);
        Identifier hasSelfIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.OPE.get(rIRI)==r);
        assertTrue(consumer.getCE(hasSelfIRI)==hasSelf);
        assertNoTriplesLeft(consumer);
    }
    public void testDataHasValue() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/dp> ."
            + "_:x owl:hasValue \"abc\"^^xsd:string ."
            + "<http://example.org/dp> a owl:DatatypeProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        DataProperty dp=DP("dp");
        IRI dpIri=IRI("dp");
        TypedLiteral lit=TL("abc","","xsd:string");
        ClassExpression hasValue=DataHasValue.create(dp, lit);
        Identifier hasValueIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.DPE.get(dpIri)==dp);
        assertTrue(consumer.getCE(hasValueIRI)==hasValue);
        assertNoTriplesLeft(consumer);
    }
    public void testObjectHasValue() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty _:inv ."
            + "_:x owl:hasValue <http://example.org/ind> ."
            + "<http://example.org/r> a owl:ObjectProperty ."
            + "_:inv owl:inverseOf <http://example.org/r> .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ObjectPropertyExpression invr=IOP("r");
        NamedIndividual ind=NI("ind");
        ClassExpression hasValue=ObjectHasValue.create(invr, ind);
        Identifier hasValueIRI=parser.string2AnonymousIndividual.get("x");
        Identifier invIRI=parser.string2AnonymousIndividual.get("inv");
        assertTrue(consumer.OPE.get(invIRI)==invr);
        assertTrue(consumer.getCE(hasValueIRI)==hasValue);
        assertNoTriplesLeft(consumer);
    }
    public void testAllValuesFrom() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty _:inv ."
            + "_:x owl:allValuesFrom _:neg ."
            + "<http://example.org/r> a owl:ObjectProperty ."
            + "<http://example.org/C> a owl:Class ."
            + "_:inv owl:inverseOf <http://example.org/r> ."
            + "_:neg rdf:type owl:Class ."
            + "_:neg owl:complementOf <http://example.org/C> .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Clazz c=C("C");
        ClassExpression notC=ObjectComplementOf.create(c);
        ObjectPropertyExpression invr=IOP("r");
        ClassExpression some=ObjectAllValuesFrom.create(invr, notC);
        Identifier someIRI=parser.string2AnonymousIndividual.get("x");
        Identifier invIRI=parser.string2AnonymousIndividual.get("inv");
        Identifier negIRI=parser.string2AnonymousIndividual.get("neg");
        assertTrue(consumer.OPE.get(invIRI)==invr);
        assertTrue(consumer.getCE(negIRI)==notC);
        assertTrue(consumer.getCE(someIRI)==some);
        assertNoTriplesLeft(consumer);
    }
    public void testSomeValuesFrom() throws Exception {
        String s="_:x rdf:type owl:Restriction ."
            + "_:x owl:onProperty <http://example.org/r> ."
            + "_:x owl:someValuesFrom <http://example.org/C> ."
            + "<http://example.org/r> a owl:ObjectProperty ."
            + "<http://example.org/C> a owl:Class .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ClassExpression some=ObjectSomeValuesFrom.create(OP("r"), C("C"));
        Identifier someIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getCE(someIRI)==some);
        assertNoTriplesLeft(consumer);
    }
    public void testOneOf() throws Exception {
        String s="_:x rdf:type owl:Class ."
            + "_:x owl:oneOf ( <http://example.org/a> <http://example.org/b> ?c ) .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        ClassExpression oneOf=ObjectOneOf.create(NamedIndividual.create("http://example.org/a"), NamedIndividual.create("http://example.org/b"), IndividualVariable.create("c"));
        Identifier oneOfIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getCE(oneOfIRI)==oneOf);
        assertNoTriplesLeft(consumer);
    }
    public void testComplicatedComplement() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "<http://example.org/D> a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "?class a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:complementOf _:d ."
            + "_:d rdf:type owl:Class ."
            + "_:d owl:unionOf _:d1 ."
            + "_:d1 rdf:first _:c . "
            + "_:c rdf:type owl:Class ."
            + "_:c owl:intersectionOf _:c1 ."
            + "_:c1 rdf:first <http://example.org/C> . "
            + "_:c1 rdf:rest _:c2 . "
            + "_:c2 rdf:first <http://example.org/D> . "
            + "_:c2 rdf:rest rdf:nil . "
            + "_:d1 rdf:rest _:d2 . "
            + "_:d2 rdf:first ?class . "
            + "_:d2 rdf:rest _:d3 ."
            + "_:d3 rdf:first <http://example.org/E> . "
            + "_:d3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        IRI dIRI=IRI("D");
        IRI eIRI=IRI("E");
        Identifier classVar=V("?class");
        Identifier complementIRI=parser.string2AnonymousIndividual.get("x");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("d");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("c");
        ClassExpression cClass=C(cIRI);
        ClassExpression eClass=C(eIRI);
        ClassExpression dClass=C(dIRI);
        ClassExpression classVarCE=CV(classVar.toString());
        ClassExpression intersection=ObjectIntersectionOf.create(cClass, dClass);
        ClassExpression union=ObjectUnionOf.create(intersection, classVarCE, eClass);
        ClassExpression complement=ObjectComplementOf.create(union);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(dIRI)==dClass);
        assertTrue(consumer.getCE(classVar)==classVarCE);
        assertTrue(consumer.getCE(intersectionIRI)==intersection);
        assertTrue(consumer.getCE(unionIRI)==union);
        assertTrue(consumer.getCE(complementIRI)==complement);
        assertNoTriplesLeft(consumer);
    }
    public void testDoubleComplement() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:complementOf _:y ."
            + "_:y rdf:type owl:Class ."
            + "_:y owl:complementOf <http://example.org/C> .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier complement1IRI=parser.string2AnonymousIndividual.get("x");
        Identifier complement2IRI=parser.string2AnonymousIndividual.get("y");
        ClassExpression cClass=C(cIRI);
        ClassExpression complement1=ObjectComplementOf.create(cClass);
        ClassExpression complement2=ObjectComplementOf.create(complement1);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(complement2IRI)==complement1);
        assertTrue(consumer.getCE(complement1IRI)==complement2);
        assertNoTriplesLeft(consumer);
    }
    public void testComplement() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:complementOf <http://example.org/C> .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier complementIRI=parser.string2AnonymousIndividual.get("x");
        ClassExpression cClass=C(cIRI);
        ClassExpression complement=ObjectComplementOf.create(cClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(complementIRI)==complement);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionIntersection() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "<http://example.org/D> a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "?class a owl:Class . "
            + "_:d rdf:type owl:Class ."
            + "_:d owl:unionOf _:d1 ."
            + "_:d1 rdf:first _:c . "
            + "_:c rdf:type owl:Class ."
            + "_:c owl:intersectionOf _:c1 ."
            + "_:c1 rdf:first <http://example.org/C> . "
            + "_:c1 rdf:rest _:c2 . "
            + "_:c2 rdf:first <http://example.org/D> . "
            + "_:c2 rdf:rest rdf:nil . "
            + "_:d1 rdf:rest _:d2 . "
            + "_:d2 rdf:first ?class . "
            + "_:d2 rdf:rest _:d3 ."
            + "_:d3 rdf:first <http://example.org/E> . "
            + "_:d3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        IRI dIRI=IRI("D");
        IRI eIRI=IRI("E");
        Identifier classVar=V("?class");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("d");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("c");
        
        ClassExpression cClass=C(cIRI);
        ClassExpression eClass=C(eIRI);
        ClassExpression dClass=C(dIRI);
        ClassExpression indVarCE=CV(classVar.toString());
        ClassExpression intersection=ObjectIntersectionOf.create(cClass, dClass);
        ClassExpression union=ObjectUnionOf.create(intersection, indVarCE, eClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(dIRI)==dClass);
        assertTrue(consumer.getCE(classVar)==indVarCE);
        assertTrue(consumer.getCE(intersectionIRI)==intersection);
        assertTrue(consumer.getCE(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionOtherSyntax() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "?class a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:unionOf (<http://example.org/C> ?class <http://example.org/E>) .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier classVar=V("?class");
        IRI eIRI=IRI("E");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        ClassExpression cClass=C(cIRI);
        ClassExpression dClass=CV(classVar.toString());
        ClassExpression eClass=C(eIRI);
        ClassExpression union=ObjectUnionOf.create(cClass, dClass, eClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(classVar)==dClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionOneVarDisjunct() throws Exception {
        String s="?class a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first ?class . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Identifier classVar=V("?class");
        ClassExpression dClass=CV(classVar.toString());
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getCE(classVar)==dClass);
        assertTrue(consumer.getCE(unionIRI)==dClass);
        assertNoTriplesLeft(consumer);
    }
    public void testUnionOneConjunct() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first <http://example.org/C> . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        ClassExpression cClass=C(cIRI);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(unionIRI)==cClass);
        assertNoTriplesLeft(consumer);
    }
    public void testUnion2() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "?class a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first <http://example.org/C> . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first ?class . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first <http://example.org/E> . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier indVar=V("?class");
        IRI eIRI=IRI("E");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        ClassExpression cClass=C(cIRI);
        ClassExpression dClass=CV(indVar.toString());
        ClassExpression eClass=C(eIRI);
        ClassExpression union=ObjectUnionOf.create(cClass, dClass, eClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(indVar)==dClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testUnion() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "<http://example.org/D> a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:unionOf _:l1 ."
            + "_:l1 rdf:first <http://example.org/C> . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first <http://example.org/D> . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first <http://example.org/E> . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        IRI dIRI=IRI("D");
        IRI eIRI=IRI("E");
        Identifier unionIRI=parser.string2AnonymousIndividual.get("x");
        Clazz cClass=C(cIRI);
        Clazz dClass=C(dIRI);
        Clazz eClass=C(eIRI);
        ClassExpression union=ObjectUnionOf.create(cClass, dClass, eClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(dIRI)==dClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(unionIRI)==union);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersectionOneVarConjunct() throws Exception {
        String s="?class a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first ?class . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Identifier classVar=V("?class");
        ClassExpression dClass=CV(classVar.toString());
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        assertTrue(consumer.getCE(classVar)==dClass);
        assertTrue(consumer.getCE(intersectionIRI)==dClass);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersectionOneConjunct() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first <http://example.org/C> . "
            + "_:l1 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        ClassExpression cClass=C(cIRI);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(intersectionIRI)==cClass);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersection2() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "?class a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first <http://example.org/C> . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first ?class . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first <http://example.org/E> . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        Identifier classVar=V("?class");
        IRI eIRI=IRI("E");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        ClassExpression cClass=C(cIRI);
        ClassExpression dClass=CV(classVar.toString());
        ClassExpression eClass=C(eIRI);
        ClassExpression intersection=ObjectIntersectionOf.create(cClass, dClass, eClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(classVar)==dClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(intersectionIRI)==intersection);
        assertNoTriplesLeft(consumer);
    }
    public void testIntersection() throws Exception {
        String s="<http://example.org/C> a owl:Class . "
            + "<http://example.org/D> a owl:Class . "
            + "<http://example.org/E> a owl:Class . "
            + "_:x rdf:type owl:Class ."
            + "_:x owl:intersectionOf _:l1 ."
            + "_:l1 rdf:first <http://example.org/C> . "
            + "_:l1 rdf:rest _:l2 . "
            + "_:l2 rdf:first <http://example.org/D> . "
            + "_:l2 rdf:rest _:l3 ."
            + "_:l3 rdf:first <http://example.org/E> . "
            + "_:l3 rdf:rest rdf:nil .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI cIRI=IRI("C");
        IRI dIRI=IRI("D");
        IRI eIRI=IRI("E");
        Identifier intersectionIRI=parser.string2AnonymousIndividual.get("x");
        Clazz cClass=C(cIRI);
        Clazz dClass=C(dIRI);
        Clazz eClass=C(eIRI);
        ClassExpression intersection=ObjectIntersectionOf.create(cClass, dClass, eClass);
        assertTrue(consumer.getCE(cIRI)==cClass);
        assertTrue(consumer.getCE(dIRI)==dClass);
        assertTrue(consumer.getCE(eIRI)==eClass);
        assertTrue(consumer.getCE(intersectionIRI)==intersection);
        assertNoTriplesLeft(consumer);
    }
    public void testCustomClazz() throws Exception {
        String s="<http://example.org/class> a owl:Class .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        IRI classIRI=IRI("class");
        Clazz clazz=C(classIRI);
        assertTrue(consumer.getCE(classIRI)==clazz);
    }
    public void testClassVariable() throws Exception {
        String s="?c a owl:Class .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        Identifier classVar=V("?c");
        ClassExpression classExpression=CV("?c");
        assertTrue(consumer.getCE(classVar)==classExpression);
    }
}