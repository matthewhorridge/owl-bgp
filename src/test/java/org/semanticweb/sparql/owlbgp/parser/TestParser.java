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
import org.semanticweb.sparql.owlbgp.model.Import;
import org.semanticweb.sparql.owlbgp.model.Ontology;
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.Declaration;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubObjectPropertyOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class TestParser extends TestCase {
    public static final String LB = System.getProperty("line.separator") ;
    
    public TestParser() {
        super();
    }
    public TestParser(String name) {
        super(name);
    }
    public void testParseOntology() throws Exception {
        String s="<http://example.org/myOnt> rdf:type owl:Ontology ."+LB
            + "<http://example.org/myOnt> owl:versionIRI <http://example.org/myOnt/1.0> ."+LB
            + "<http://example.org/myOnt> owl:imports <http://example.org/myOtherOnt> ."+LB
            + "<http://example.org/myOnt> owl:imports <http://example.org/anotherOnt> ."+LB
            + "<http://example.org/myOnt/C1> rdf:type owl:Class . "+LB
            + "<http://example.org/myOnt/C2> rdf:type owl:Class . "+LB
            + "<http://example.org/myOnt/C1> rdfs:subClassOf <http://example.org/myOnt/C2> . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        assertTrue(ontology.containsImport(Import.create(IRI.create("http://example.org/anotherOnt"))));
        assertTrue(ontology.containsImport(Import.create(IRI.create("http://example.org/myOtherOnt"))));
        assertTrue(ontology.containsAxiom(Declaration.create(Clazz.create("http://example.org/myOnt/C1"))));
        assertTrue(ontology.containsAxiom(Declaration.create(Clazz.create("http://example.org/myOnt/C2"))));
        assertTrue(ontology.containsAxiom(SubClassOf.create(Clazz.create("http://example.org/myOnt/C1"), Clazz.create("http://example.org/myOnt/C2"))));
        assertTrue(ontology.hasVersionIRI(IRI.create("http://example.org/myOnt/1.0")));
        assertTrue(ontology.getOntologyIRI()==IRI.create("http://example.org/myOnt"));
        assertTrue(ontology.getAxioms().size()==3);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testParseOntologyWithVars() throws Exception {
        String s="?ontIRI rdf:type owl:Ontology ."+LB
            + "?ontIRI owl:versionIRI ?versionIRI ."+LB
            + "<http://example.org/myOnt> owl:imports ?imports ."+LB
            + "<http://example.org/myOnt> owl:imports <http://example.org/anotherOnt> ."+LB
            + "?class rdf:type owl:Class . "+LB
            + "<http://example.org/myOnt/C2> rdf:type owl:Class . "+LB
            + "?class rdfs:subClassOf <http://example.org/myOnt/C2> . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        assertTrue(ontology.containsImport(Import.create(IRI.create("http://example.org/anotherOnt"))));
        assertTrue(ontology.containsImport(Import.create(UntypedVariable.create("?imports"))));
        assertTrue(ontology.containsAxiom(Declaration.create(ClassVariable.create("?class"))));
        assertTrue(ontology.containsAxiom(Declaration.create(Clazz.create("http://example.org/myOnt/C2"))));
        assertTrue(ontology.containsAxiom(SubClassOf.create(ClassVariable.create("?class"), Clazz.create("http://example.org/myOnt/C2"))));
        assertTrue(ontology.hasVersionIRI(UntypedVariable.create("?versionIRI")));
        assertTrue(ontology.getOntologyIRI()==UntypedVariable.create("?ontIRI"));
        assertTrue(ontology.getAxioms().size()==3);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testObjectInverseOf() throws Exception {
        String s="_:x owl:inverseOf <http://example.org/myOnt/op1> ."+LB
            + "_:x rdfs:subPropertyOf <http://example.org/myOnt/op2> . "+LB
            + "<http://example.org/myOnt/op2> rdf:type owl:ObjectProperty . "+LB
            + "<http://example.org/myOnt/op1> rdf:type owl:ObjectProperty . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        ObjectProperty op2=ObjectProperty.create("http://example.org/myOnt/op2");
        ObjectProperty op1=ObjectProperty.create("http://example.org/myOnt/op1");
        assertTrue(ontology.containsAxiom(Declaration.create(op1)));
        assertTrue(ontology.containsAxiom(Declaration.create(op2)));
        assertTrue(ontology.containsAxiom(SubObjectPropertyOf.create(ObjectInverseOf.create(op1), op2)));
        assertTrue(ontology.getAxioms().size()==3);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testObjectInverseOfWithVars() throws Exception {
        String s="_:x owl:inverseOf ?op1 ."+LB
            + "_:x rdfs:subPropertyOf <http://example.org/myOnt/op2> . "+LB
            + "<http://example.org/myOnt/op2> rdf:type owl:ObjectProperty . "+LB
            + "$op1 rdf:type owl:ObjectProperty . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        ObjectProperty op2=ObjectProperty.create("http://example.org/myOnt/op2");
        ObjectPropertyVariable op1=ObjectPropertyVariable.create("$op1");
        assertTrue(ontology.containsAxiom(Declaration.create(op1)));
        assertTrue(ontology.containsAxiom(Declaration.create(op2)));
        assertTrue(ontology.containsAxiom(SubObjectPropertyOf.create(ObjectInverseOf.create(op1), op2)));
        assertTrue(ontology.getAxioms().size()==3);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testAnnotationProperty() throws Exception {
        String s="<http://example.org/myOnt/ap1> a owl:AnnotationProperty ."+LB
            + "<http://example.org/myOnt/ap1> rdfs:range <http://example.org/myOnt/range> . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        AnnotationProperty ap1=AnnotationProperty.create("http://example.org/myOnt/ap1");
        IRI range=IRI.create("http://example.org/myOnt/range");
        assertTrue(ontology.containsAxiom(Declaration.create(ap1)));
        assertTrue(ontology.containsAxiom(AnnotationPropertyRange.create(ap1, range)));
        assertTrue(ontology.getAxioms().size()==2);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testAnnotationPropertyWithVars() throws Exception {
        String s="?ap a owl:AnnotationProperty ."+LB
            + "?ap rdfs:range <http://example.org/myOnt/range> . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        AnnotationPropertyVariable ap1=AnnotationPropertyVariable.create("?ap");
        IRI range=IRI.create("<http://example.org/myOnt/range>");
        assertTrue(ontology.containsAxiom(Declaration.create(ap1)));
        assertTrue(ontology.containsAxiom(AnnotationPropertyRange.create(ap1, range)));
        assertTrue(ontology.getAxioms().size()==2);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testDataIntersection() throws Exception {
        String s="<http://example.org/myOnt/class> rdfs:subClassOf _:class . "+LB
            + "_:class rdf:type owl:Restriction ."+LB
            + "_:class owl:onProperty <http://example.org/myOnt/dp> ."+LB
            + "_:class owl:allValuesFrom _:dr . "+LB
            + "_:dr rdf:type rdfs:Datatype . "+LB
            + "_:dr owl:intersectionOf ( xsd:int xsd:decimal <http://www.w3.org/2001/XMLSchema#byte>) . "
            + "<http://example.org/myOnt/dp> a owl:DatatypeProperty . "
            + "<http://example.org/myOnt/class> a owl:Class . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        Clazz sub=Clazz.create("http://example.org/myOnt/class");
        DataProperty dp=DataProperty.create("http://example.org/myOnt/dp");
        Datatype intDT=Datatype.create("http://www.w3.org/2001/XMLSchema#int");
        Datatype decimalDT=Datatype.create("<http://www.w3.org/2001/XMLSchema#decimal>");
        Datatype byteDT=Datatype.XSD_BYTE;
        DataIntersectionOf and=DataIntersectionOf.create(decimalDT, byteDT, intDT);
        assertTrue(ontology.containsAxiom(Declaration.create(dp)));
        assertTrue(ontology.containsAxiom(Declaration.create(sub)));
        assertTrue(ontology.containsAxiom(SubClassOf.create(sub, DataAllValuesFrom.create(dp, and))));
        assertTrue(ontology.getAxioms().size()==3);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testDataIntersectionWithVars() throws Exception {
        String s="_:ont rdf:type owl:Ontology . "+LB
            + "$class rdfs:subClassOf _:class . "+LB
            + "_:class rdf:type owl:Restriction ."+LB
            + "_:class owl:onProperty ?dp ."+LB
            + "_:class owl:allValuesFrom _:dr . "+LB
            + "_:dr rdf:type rdfs:Datatype . "+LB
            + "_:dr owl:intersectionOf ( ?dtint xsd:decimal ?dtbyte) . "
            + "?dp a owl:DatatypeProperty . "
            + "?class rdf:type owl:Class . "
            + "?dtint a rdfs:Datatype . "
            + "?dtbyte a rdfs:Datatype . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        ClassVariable sub=ClassVariable.create("?class");
        DataPropertyVariable dp=DataPropertyVariable.create("$dp");
        DatatypeVariable intDT=DatatypeVariable.create("?dtint");
        Datatype decimalDT=Datatype.create("<http://www.w3.org/2001/XMLSchema#decimal>");
        DatatypeVariable byteDT=DatatypeVariable.create("$dtbyte");
        DataIntersectionOf and=DataIntersectionOf.create(decimalDT, byteDT, intDT);
        assertTrue(ontology.containsAxiom(Declaration.create(dp)));
        assertTrue(ontology.containsAxiom(Declaration.create(sub)));
        assertTrue(ontology.containsAxiom(Declaration.create(intDT)));
        assertTrue(ontology.containsAxiom(Declaration.create(byteDT)));
        assertTrue(ontology.containsAxiom(SubClassOf.create(sub, DataAllValuesFrom.create(dp, and))));
        assertTrue(ontology.getAxioms().size()==5);
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testMultiTripleAxiomWithAnnotation() throws Exception {
        String s="_:x rdf:type owl:NegativePropertyAssertion ."
            + "_:x owl:sourceIndividual <http://example.org/myOnt/Chris> ."
            + "_:x owl:assertionProperty <http://example.org/myOnt/brotherOf> ."
            + "_:x owl:targetIndividual <http://example.org/myOnt/Stewie> ."
            + "_:x <http://example.org/myOnt/author> <http://example.org/myOnt/Seth_MacFarlane> ."
            + "<http://example.org/myOnt/Chris> a owl:NamedIndividual ."
            + "<http://example.org/myOnt/brotherOf> a owl:ObjectProperty. "
            + "<http://example.org/myOnt/Stewie> a owl:NamedIndividual ."
            + "<http://example.org/myOnt/author> a owl:AnnotationProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        NamedIndividual chris=NamedIndividual.create("http://example.org/myOnt/Chris");
        NamedIndividual stewie=NamedIndividual.create("http://example.org/myOnt/Stewie");
        ObjectProperty brotherOf=ObjectProperty.create("http://example.org/myOnt/brotherOf");
        AnnotationProperty author=AnnotationProperty.create("http://example.org/myOnt/author");
        Annotation anno=Annotation.create(author, IRI.create("http://example.org/myOnt/Seth_MacFarlane"));
        NegativeObjectPropertyAssertion assertion=NegativeObjectPropertyAssertion.create(brotherOf, chris, stewie, Collections.singleton(anno)); 
        assertTrue(ontology.containsAxiom(assertion));
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testChainAxiomWithAnnotation() throws Exception {
        String s="_:x rdf:type owl:Axiom ."
            + "_:x owl:annotatedSource <http://example.org/myOnt/hasAunt> ."
            + "_:x owl:annotatedProperty owl:propertyChainAxiom ."
            + "_:x owl:annotatedTarget _:y1 ."
            + "_:x rdfs:comment \"An aunt is a mother's sister.\" ."
            + "<http://example.org/myOnt/hasAunt> owl:propertyChainAxiom _:y1 ."
            + "_:y1 rdf:first <http://example.org/myOnt/hasMother> ."
            + "_:y1 rdf:rest _:y2 ."
            + "_:y2 rdf:first <http://example.org/myOnt/hasSister> ."
            + "_:y2 rdf:rest rdf:nil . "
            + "<http://example.org/myOnt/hasAunt> a owl:ObjectProperty ."
            + "<http://example.org/myOnt/hasMother> a owl:ObjectProperty. "
            + "<http://example.org/myOnt/hasSister> a owl:ObjectProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        ObjectProperty hasAunt=ObjectProperty.create("http://example.org/myOnt/hasAunt");
        ObjectProperty hasMother=ObjectProperty.create("http://example.org/myOnt/hasMother");
        ObjectProperty hasSister=ObjectProperty.create("http://example.org/myOnt/hasSister");
        AnnotationProperty comment=AnnotationProperty.create("http://www.w3.org/2000/01/rdf-schema#comment");
        Annotation anno=Annotation.create(comment, TypedLiteral.create("An aunt is a mother's sister."));
        SubObjectPropertyOf sub=SubObjectPropertyOf.create(ObjectPropertyChain.create(hasMother, hasSister), hasAunt, Collections.singleton(anno)); 
        assertTrue(ontology.containsAxiom(sub));
        assertTrue(parser.handler.allTriplesConsumed());
    }
    public void testChainAxiomWithAnnotatedAnnotations() throws Exception {
        String s="_:x rdf:type owl:Axiom ."
            + "_:x owl:annotatedSource <http://example.org/myOnt/hasAunt> ."
            + "_:x owl:annotatedProperty owl:propertyChainAxiom ."
            + "_:x owl:annotatedTarget _:y1 ."
            + "_:x rdfs:comment \"An aunt is a mother's sister.\" ."
            + "_:y rdf:type owl:Annotation ."
            + "_:y owl:annotatedSource _:x ."
            + "_:y owl:annotatedProperty rdfs:comment ."
            + "_:y owl:annotatedTarget \"An aunt is a mother's sister.\" ."
            + "_:y rdfs:label \"Annotation annotation.\" ."
            + "<http://example.org/myOnt/hasAunt> owl:propertyChainAxiom _:y1 ."
            + "_:y1 rdf:first <http://example.org/myOnt/hasMother> ."
            + "_:y1 rdf:rest _:y2 ."
            + "_:y2 rdf:first <http://example.org/myOnt/hasSister> ."
            + "_:y2 rdf:rest rdf:nil . "
            + "<http://example.org/myOnt/hasAunt> a owl:ObjectProperty ."
            + "<http://example.org/myOnt/hasMother> a owl:ObjectProperty. "
            + "<http://example.org/myOnt/hasSister> a owl:ObjectProperty .";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        parser.parse();
        Ontology ontology=parser.getParsedOntology();
        ObjectProperty hasAunt=ObjectProperty.create("http://example.org/myOnt/hasAunt");
        ObjectProperty hasMother=ObjectProperty.create("http://example.org/myOnt/hasMother");
        ObjectProperty hasSister=ObjectProperty.create("http://example.org/myOnt/hasSister");
        AnnotationProperty comment=AnnotationProperty.create("http://www.w3.org/2000/01/rdf-schema#comment");
        AnnotationProperty label=AnnotationProperty.create("http://www.w3.org/2000/01/rdf-schema#label");
        Annotation annoAnno=Annotation.create(label, TypedLiteral.create("Annotation annotation."));
        Annotation anno=Annotation.create(comment, TypedLiteral.create("An aunt is a mother's sister."), Collections.singleton(annoAnno));
        SubObjectPropertyOf sub=SubObjectPropertyOf.create(ObjectPropertyChain.create(hasMother, hasSister), hasAunt, Collections.singleton(anno)); 
        assertTrue(ontology.containsAxiom(sub));
        assertTrue(parser.handler.allTriplesConsumed());
    }
}