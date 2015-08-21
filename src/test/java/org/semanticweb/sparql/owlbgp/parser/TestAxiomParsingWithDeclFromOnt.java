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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.sparql.owlbgp.AbstractTest;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.AnnotationSubject;
import org.semanticweb.sparql.owlbgp.model.AnnotationValue;
import org.semanticweb.sparql.owlbgp.model.Ontology;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.AsymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.DatatypeDefinition;
import org.semanticweb.sparql.owlbgp.model.axioms.Declaration;
import org.semanticweb.sparql.owlbgp.model.axioms.DifferentIndividuals;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointUnion;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalDataProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.HasKey;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseFunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.IrreflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.ReflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.axioms.SubAnnotationPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubDataPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubObjectPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectComplementOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataComplementOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataOneOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction.OWL2_FACET;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;

public class TestAxiomParsingWithDeclFromOnt extends AbstractTest {
    public static final String LB = System.getProperty("line.separator") ;

    protected OWLOntology queriedOntology;
    
    @Override
    protected void setUp() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory=manager.getOWLDataFactory();
        queriedOntology=manager.createOntology();
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDatatype(org.semanticweb.owlapi.model.IRI.create("http://example.org/dt1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDatatype(org.semanticweb.owlapi.model.IRI.create("http://example.org/dt2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDatatype(org.semanticweb.owlapi.model.IRI.create("http://example.org/dt3"))));
        
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/a1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/a2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/a3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/b1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/b2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/b3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/c1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/c2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLNamedIndividual(org.semanticweb.owlapi.model.IRI.create("http://example.org/c3"))));
        
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/r"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/r1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/r2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/r3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/s"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/s1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/s2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/s3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/t"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/t1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/t2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/t3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op4"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op5"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLObjectProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/op6"))));
        
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/dp"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/dp1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/dp2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/dp3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLDataProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/dp4"))));
        
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/ap1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/ap2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLAnnotationProperty(org.semanticweb.owlapi.model.IRI.create("http://example.org/ap3"))));
        
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/C"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/D"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/E"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/F"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/G"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/H"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/C1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/D1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/E1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/F1"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/C2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/D2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/E2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/F2"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/C3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/D3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/E3"))));
        manager.addAxiom(queriedOntology, factory.getOWLDeclarationAxiom(factory.getOWLClass(org.semanticweb.owlapi.model.IRI.create("http://example.org/F3"))));
    }
    
    public void testSubClassOfExistsTypeGuessing() throws Exception {
        Declaration ax1=Declaration.create(CV("o"));
        ClassExpression superCls=ObjectSomeValuesFrom.create(OP("http://example.org/r"), CV("o"));
        Axiom ax3=SubClassOf.create(CV("x"), superCls);
        String s=ax1.toTurtleString()+ax3.toTurtleString();
        Ontology qo=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(qo.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testSubClassOfExists() throws Exception {
        Declaration ax1=Declaration.create(CV("o"));
        Declaration ax2=Declaration.create(CV("x"));
        ClassExpression superCls=ObjectSomeValuesFrom.create(OP("http://example.org/r"), CV("o"));
        Axiom ax3=SubClassOf.create(CV("x"), superCls);
        String s=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        Ontology qo=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(qo.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testDatatypeDefinition() throws Exception {
        Axiom ax1=DatatypeDefinition.create(DT("<http://example.org/dt1>"),DataIntersectionOf.create(DT("xsd:int"), DataComplementOf.create(DT("xsd:string"))));
        Axiom ax2=DatatypeDefinition.create(
                DT("<http://example.org/dt2>"),
                DataComplementOf.create(
                        DataUnionOf.create(DT("xsd:int"), DataComplementOf.create(DT("xsd:string")))),
                        ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom ax3=DatatypeDefinition.create(
                DT("<http://example.org/dt2>"),
                DataComplementOf.create(
                        DataIntersectionOf.create(DT("xsd:int"), DataOneOf.create(TL("lit1"), TL("lit2")))),
                        ANN(AP("rdfs:label"), TL("EFanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testTransitiveObjProp() throws Exception {
        Axiom ax1=TransitiveObjectProperty.create(OP("op1"));
        Axiom ax2=TransitiveObjectProperty.create(IOP("op2"));
        Axiom ax3=TransitiveObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=TransitiveObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=TransitiveObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=TransitiveObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.remove(Declaration.create(OP("op1"))));
        assertTrue(axioms.remove(Declaration.create(OP("op3"))));
        assertTrue(axioms.remove(Declaration.create(OP("op6"))));
        assertTrue(axioms.isEmpty());
    }
    public void testAsymmetricObjProp() throws Exception {
        Axiom ax1=AsymmetricObjectProperty.create(OP("op1"));
        Axiom ax2=AsymmetricObjectProperty.create(IOP("op2"));
        Axiom ax3=AsymmetricObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=AsymmetricObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=AsymmetricObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=AsymmetricObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.isEmpty());
    }
    public void testSymmetricObjProp() throws Exception {
        Axiom ax1=SymmetricObjectProperty.create(OP("op1"));
        Axiom ax2=SymmetricObjectProperty.create(IOP("op2"));
        Axiom ax3=SymmetricObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=SymmetricObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=SymmetricObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=SymmetricObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.remove(Declaration.create(OP("op1"))));
        assertTrue(axioms.remove(Declaration.create(OP("op3"))));
        assertTrue(axioms.remove(Declaration.create(OP("op6"))));
        assertTrue(axioms.isEmpty());
    }
    public void testIrreflexiveObjProp() throws Exception {
        Axiom ax1=IrreflexiveObjectProperty.create(OP("op1"));
        Axiom ax2=IrreflexiveObjectProperty.create(IOP("op2"));
        Axiom ax3=IrreflexiveObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=IrreflexiveObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=IrreflexiveObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=IrreflexiveObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.isEmpty());
    }
    public void testReflexiveObjProp() throws Exception {
        Axiom ax1=ReflexiveObjectProperty.create(OP("op1"));
        Axiom ax2=ReflexiveObjectProperty.create(IOP("op2"));
        Axiom ax3=ReflexiveObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=ReflexiveObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=ReflexiveObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=ReflexiveObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.isEmpty());
    }
    public void testInverseFunctionalObjProp() throws Exception {
        Axiom ax1=InverseFunctionalObjectProperty.create(OP("op1"));
        Axiom ax2=InverseFunctionalObjectProperty.create(IOP("op2"));
        Axiom ax3=InverseFunctionalObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=InverseFunctionalObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=InverseFunctionalObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=InverseFunctionalObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.remove(Declaration.create(OP("op1"))));
        assertTrue(axioms.remove(Declaration.create(OP("op3"))));
        assertTrue(axioms.remove(Declaration.create(OP("op6"))));
        assertTrue(axioms.isEmpty());
    }
    public void testFunctionalDataProp() throws Exception {
        Axiom ax1=FunctionalDataProperty.create(DP("dp1"));
        Axiom ax2=FunctionalDataProperty.create(DP("dp2"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax3=FunctionalDataProperty.create(DP("dp3"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax4=FunctionalDataProperty.create(DP("dp4"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()+ax4.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.isEmpty());
    }
    public void testFunctionalObjProp() throws Exception {
        Axiom ax1=FunctionalObjectProperty.create(OP("op1"));
        Axiom ax2=FunctionalObjectProperty.create(IOP("op2"));
        Axiom ax3=FunctionalObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=FunctionalObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=FunctionalObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=FunctionalObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.remove(ax6));
        assertTrue(axioms.isEmpty());
    }
    public void testInvObjPropOf() throws Exception {
        Axiom sop1=InverseObjectProperties.create(OP("op1"), OP("op2"));
        Axiom sop2=InverseObjectProperties.create(IOP("op2"), OP("op3"));
        Axiom sop2a=InverseObjectProperties.create(OP("op2"), IOP("op3"));
        Axiom sop3=InverseObjectProperties.create(OP("op1"), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        
        Axiom sop4=InverseObjectProperties.create(IOP("op1"), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop4a=InverseObjectProperties.create(OP("op1"), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        
        Axiom sop5=InverseObjectProperties.create(IOP("op1"), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        
        Axiom sop6=InverseObjectProperties.create(OP("op2"), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6a=InverseObjectProperties.create(IOP("op2"), OP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        
        String str=sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()
            +sop6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sop1));
        assertTrue(axioms.remove(sop2) || axioms.remove(sop2a));
        assertTrue(axioms.remove(sop3));
        assertTrue(axioms.remove(sop4) || axioms.remove(sop4a));
        assertTrue(axioms.remove(sop5));
        assertTrue(axioms.remove(sop6) || axioms.remove(sop6a));
        assertTrue(axioms.isEmpty());
    }
    public void testEquivObjProp() throws Exception {
        Axiom sop1=EquivalentObjectProperties.create(OP("op1"), OP("op2"));
        Axiom sop2=EquivalentObjectProperties.create(IOP("op2"), OP("op3"));
        Axiom sop3=EquivalentObjectProperties.create(OP("op1"), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sop4=EquivalentObjectProperties.create(IOP("op1"), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop5=EquivalentObjectProperties.create(IOP("op1"), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6=EquivalentObjectProperties.create(OP("op2"), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()+sop6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sop1));
        assertTrue(axioms.remove(sop2));
        assertTrue(axioms.remove(sop3));
        assertTrue(axioms.remove(sop4));
        assertTrue(axioms.remove(sop5));
        assertTrue(axioms.remove(sop6));
        assertTrue(axioms.isEmpty());
    }
    public void testSubChainObjPropOf() throws Exception {
        Axiom sop1=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op1"), OP("op1")), OP("op2"));
        Axiom sop2=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op3"), OP("op3"), OP("op1"), IOP("op2"), IOP("op3")), OP("op3"));
        Axiom sop3=SubObjectPropertyOf.create(ObjectPropertyChain.create(IOP("op1"), OP("op1")), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sop4=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op1"), IOP("op1"), OP("op3")), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop5=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op3"), OP("op3"), IOP("op3")), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op1"), OP("op2"), OP("op2")), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()+sop6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sop1));
        assertTrue(axioms.remove(sop2));
        assertTrue(axioms.remove(sop3));
        assertTrue(axioms.remove(sop4));
        assertTrue(axioms.remove(sop5));
        assertTrue(axioms.remove(sop6));
        assertTrue(axioms.isEmpty());
    }
    public void testSubObjPropOf() throws Exception {
        Axiom sop1=SubObjectPropertyOf.create(OP("op1"), OP("op2"));
        Axiom sop2=SubObjectPropertyOf.create(IOP("op2"), OP("op3"));
        Axiom sop3=SubObjectPropertyOf.create(OP("op1"), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sop4=SubObjectPropertyOf.create(IOP("op1"), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop5=SubObjectPropertyOf.create(IOP("op1"), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6=SubObjectPropertyOf.create(OP("op2"), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()+sop6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sop1));
        assertTrue(axioms.remove(sop2));
        assertTrue(axioms.remove(sop3));
        assertTrue(axioms.remove(sop4));
        assertTrue(axioms.remove(sop5));
        assertTrue(axioms.remove(sop6));
        assertTrue(axioms.isEmpty());
    }
    public void testSubDataPropOf() throws Exception {
        Axiom ax1=SubDataPropertyOf.create(DP("dp1"), DP("dp2"));
        Axiom ax2=SubDataPropertyOf.create(DP("dp1"), DP("dp3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax3=SubDataPropertyOf.create(DP("dp2"), DP("dp1"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax4=SubDataPropertyOf.create(DP("dp2"), DP("dp2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax5=SubDataPropertyOf.create(DP("dp3"), DP("dp2"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ax1.toTurtleString()+ax2.toTurtleString()
            +ax3.toTurtleString()+ax4.toTurtleString()+ax5.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.remove(ax4));
        assertTrue(axioms.remove(ax5));
        assertTrue(axioms.isEmpty());
    }
    public void testDisjointUnionOfClasses() throws Exception {
        Axiom ax1a=DisjointUnion.create(C("C1"), ObjectSomeValuesFrom.create(OP("op1"), ObjectIntersectionOf.create(C("D1"), C("E1"))), C("F1"));
        Axiom ax1b=DisjointUnion.create(C("C1"), C("E1"), ObjectSomeValuesFrom.create(OP("op1"), ObjectIntersectionOf.create(C("D1"), C("E1"))), C("F1"));
        Annotation ann=ANN(AP("rdfs:label"), TL("anno"));
        Annotation annAnn=ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno")));
        Set<ClassExpression> classes=new HashSet<ClassExpression>();
        classes.add(ObjectSomeValuesFrom.create(OP("op1"), ObjectIntersectionOf.create(C("D1"), C("E1"))));
        classes.add(C("F1"));
        Axiom ax2a=DisjointUnion.create(C("C1"), classes, Collections.singleton(ann));
        Axiom ax2b=DisjointUnion.create(C("E1"), classes, Collections.singleton(ann));
        Axiom ax3a=DisjointUnion.create(C("C1"), classes, Collections.singleton(annAnn));
        Axiom ax3b=DisjointUnion.create(C("E1"), classes, Collections.singleton(annAnn));
        String s=ax1a.toTurtleString()+ax1b.toTurtleString()+ax2a.toTurtleString()+ax2b.toTurtleString()+ax3a.toTurtleString()+ax3b.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1a));
        assertTrue(axioms.remove(ax1b));
        assertTrue(axioms.remove(ax2a));
        assertTrue(axioms.remove(ax2b));
        assertTrue(axioms.remove(ax3a));
        assertTrue(axioms.remove(ax3b));
    }
    public void testHasKey2() throws Exception {        ClassExpression cAndD=ObjectIntersectionOf.create(C("C"), C("D"));
        Axiom hasKey=HasKey.create(cAndD, OP("op"), DP("dp"));
        Set<PropertyExpression> props=new HashSet<PropertyExpression>();
        props.add(DP("dp"));
        Axiom hasKeyAnn=HasKey.create((ClassExpression)ObjectUnionOf.create(C("C"), C("D")),props,Collections.singleton(ANN(AP("rdfs:label"), TL("C or D anno"))));
        props=new HashSet<PropertyExpression>();
        props.add(DP("dp"));
        props.add(OP("op"));
        Axiom hasKeyAnnAnn=HasKey.create((ClassExpression)ObjectComplementOf.create(C("C")),props,
                Collections.singleton(ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno")))));
        String s=hasKey.toTurtleString()+hasKeyAnn.toTurtleString()+hasKeyAnnAnn.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(hasKey));
        assertTrue(axioms.remove(hasKeyAnn));
        assertTrue(axioms.remove(hasKeyAnnAnn));
        assertTrue(axioms.isEmpty());
    }
    public void testHasKey() throws Exception {
        Axiom hasKey=HasKey.create(C("C"), OP("op"));
        String s=hasKey.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(hasKey));
        assertTrue(axioms.isEmpty());
    }
    public void testDataPropertyRangeComplex() throws Exception {
        Axiom sco=DataPropertyRange.create(DP("dp1"), DataIntersectionOf.create(DT("dt1"),DT("dt2")));
        Axiom scoa=DataPropertyRange.create(DP("dp2"), 
                DataUnionOf.create(DT("dt1"), DataIntersectionOf.create(DT("dt2"),DT("dt3"))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=DataPropertyRange.create(DP("dp3"),
                DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testDataPropertyRange() throws Exception {
        Axiom sco=DataPropertyRange.create(DP("dp1"),DT("dt1"));
        Axiom scoa=DataPropertyRange.create(DP("dp2"),DT("dt2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=DataPropertyRange.create(DP("dp3"),DT("dt3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testObjectPropertyRangeComplex() throws Exception {
        Axiom sco=ObjectPropertyRange.create(IOP("r"), ObjectSomeValuesFrom.create(IOP("r"), ObjectComplementOf.create(C("C"))));
        Axiom scoa=ObjectPropertyRange.create(OP("s"), 
                ObjectAllValuesFrom.create(IOP("s"), ObjectIntersectionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=ObjectPropertyRange.create(OP("r"),
                DataExactCardinality.create(5, DP("dp1"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testObjectPropertyRange() throws Exception {
        Axiom sco=ObjectPropertyRange.create(OP("op1"),C("C1"));
        Axiom scoa=ObjectPropertyRange.create(IOP("op1"),C("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=ObjectPropertyRange.create(OP("op3"),C("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    
    public void testDataPropertyDomainComplex() throws Exception {
        Axiom sco=DataPropertyDomain.create(DP("dp1"), ObjectSomeValuesFrom.create(IOP("op"), ObjectIntersectionOf.create(C("C"),C("D"))));
        Axiom scoa=DataPropertyDomain.create(DP("dp2"), 
                ObjectAllValuesFrom.create(OP("op"), ObjectUnionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=DataPropertyDomain.create(DP("dp3"),
                DataMaxCardinality.create(5, DP("dp1"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testDataPropertyDomain() throws Exception {
        Axiom sco=DataPropertyDomain.create(DP("dp1"),C("C1"));
        Axiom scoa=DataPropertyDomain.create(DP("dp2"),C("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=DataPropertyDomain.create(DP("dp3"),C("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testObjectPropertyDomainComplex() throws Exception {
        Axiom sco=ObjectPropertyDomain.create(IOP("r"), ObjectSomeValuesFrom.create(IOP("r"), ObjectIntersectionOf.create(C("C"),C("D"))));
        Axiom scoa=ObjectPropertyDomain.create(OP("s"), 
                ObjectAllValuesFrom.create(IOP("s"), ObjectUnionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=ObjectPropertyDomain.create(OP("r"),
                DataMaxCardinality.create(5, DP("dp1"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testObjectPropertyDomain() throws Exception {
        Axiom sco=ObjectPropertyDomain.create(OP("op1"),C("C1"));
        Axiom scoa=ObjectPropertyDomain.create(IOP("op1"),C("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=ObjectPropertyDomain.create(OP("op3"),C("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testDisjointWithObjectProperty() throws Exception {
        Axiom spo=DisjointObjectProperties.create(IOP("op1"), OP("op2"));
        Axiom spoa=DisjointObjectProperties.create(OP("op1"),OP("op3"),ANN(AP("rdfs:label"), TL("opanno")));
        Axiom spoaa=DisjointObjectProperties.create(OP("op2"),IOP("op3"),ANN(AP("rdfs:comment"), TL("opAanno"), ANN(AP("rdfs:label"), TL("opannoAnno"))));
        String str=spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(spo));
        assertTrue(axioms.remove(spoa));
        assertTrue(axioms.remove(spoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testDisjointWithDataProperty() throws Exception {
        Axiom spo=DisjointDataProperties.create(DP("dp1"), DP("dp2"));
        Axiom spoa=DisjointDataProperties.create(DP("dp1"),DP("dp3"),ANN(AP("rdfs:label"), TL("dpanno")));
        Axiom spoaa=DisjointDataProperties.create(DP("dp2"),DP("dp3"),ANN(AP("rdfs:comment"), TL("dpAanno"), ANN(AP("rdfs:label"), TL("dpannoAnno"))));
        String str=spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(spo));
        assertTrue(axioms.remove(spoa));
        assertTrue(axioms.remove(spoaa));
    }
    public void testEquivalentDataProperties() throws Exception {
        Axiom spo=EquivalentDataProperties.create(DP("dp1"), DP("dp2"));
        Axiom spoa=EquivalentDataProperties.create(DP("dp1"),DP("dp3"),ANN(AP("rdfs:label"), TL("dpanno")));
        Axiom spoaa=EquivalentDataProperties.create(DP("dp2"),DP("dp3"),ANN(AP("rdfs:comment"), TL("dpAanno"), ANN(AP("rdfs:label"), TL("dpannoAnno"))));
        String str=spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(spo));
        assertTrue(axioms.remove(spoa));
        assertTrue(axioms.remove(spoaa));
    }
    public void testEquivalentObjectProperties() throws Exception {
        Axiom spo=EquivalentObjectProperties.create(IOP("op1"), OP("op2"));
        Axiom spoa=EquivalentObjectProperties.create(OP("op1"),OP("op3"),ANN(AP("rdfs:label"), TL("opanno")));
        Axiom spoaa=EquivalentObjectProperties.create(OP("op2"),IOP("op3"),ANN(AP("rdfs:comment"), TL("opAanno"), ANN(AP("rdfs:label"), TL("opannoAnno"))));
        String str=spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(spo));
        assertTrue(axioms.remove(spoa));
        assertTrue(axioms.remove(spoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testEquivalentClassComplex() throws Exception {
        Axiom sco=EquivalentClasses.create(ObjectSomeValuesFrom.create(IOP("r"), C("C")),ObjectIntersectionOf.create(C("C"),C("D")));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(ObjectSomeValuesFrom.create(OP("s"), ObjectComplementOf.create(ObjectIntersectionOf.create(C("E"),C("F")))));
        ces.add(ObjectMinCardinality.create(5, IOP("r")));
        Axiom scoa=EquivalentClasses.create(ces,ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=EquivalentClasses.create(
                SET(DataAllValuesFrom.create(DP("dp1"), DataIntersectionOf.create(DT("xsd:string"), DataComplementOf.create(DT("rdf:PlainLiteral")))),
                    DataExactCardinality.create(1000, DP("dp2"), DatatypeRestriction.create(DT("xsd:decimal"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_INTEGER))))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testEquivalentClasses() throws Exception {
        Axiom sco=EquivalentClasses.create(C("C"),C("D"));
        Axiom scoa=EquivalentClasses.create(SET(C("E"),C("F")),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=EquivalentClasses.create(SET(C("G"), C("H")),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testSubClassOfComplex() throws Exception {
        Axiom sco=SubClassOf.create(ObjectSomeValuesFrom.create(IOP("r"), C("C")),ObjectIntersectionOf.create(C("C"),C("D")));
        Axiom scoa=SubClassOf.create(ObjectAllValuesFrom.create(OP("s"), ObjectUnionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))), ObjectMinCardinality.create(5, OP("r"), ObjectIntersectionOf.create(C("E"),C("F"))),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=SubClassOf.create(
                DataSomeValuesFrom.create(DP("dp1"), DataUnionOf.create(DT("xsd:string"), DT("rdf:PlainLiteral"))), 
                DataMaxCardinality.create(5, DP("dp2"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testSubClassOf() throws Exception {
        Axiom sco=SubClassOf.create(C("C"),C("D"));
        Axiom scoa=SubClassOf.create(C("E"),C("F"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=SubClassOf.create(C("G"),C("H"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testNegativeDataPropertyAssertion() throws Exception {
        Axiom negDP=NegativeDataPropertyAssertion.create(DP("dp1"),NI("a1"),TL("b1"));
        Axiom negDPa=NegativeDataPropertyAssertion.create(DP("dp2"),NI("a2"),TL("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Axiom negDPaa=NegativeDataPropertyAssertion.create(DP("dp3"),NI("a3"),TL("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=negDP.toTurtleString()+negDPa.toTurtleString()+negDPaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(negDP));
        assertTrue(axioms.remove(negDPa));
        assertTrue(axioms.remove(negDPaa));
        assertTrue(axioms.isEmpty());
    }
    public void testNegativeObjectPropertyAssertion() throws Exception {
        Axiom negOP=NegativeObjectPropertyAssertion.create(OP("r1"),NI("a1"),NI("b1"));
        Axiom negOPa=NegativeObjectPropertyAssertion.create(OP("r2"),NI("a2"),NI("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Axiom negOPaa=NegativeObjectPropertyAssertion.create(OP("r3"),NI("a3"),NI("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=negOP.toTurtleString()+negOPa.toTurtleString()+negOPaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(negOP));
        assertTrue(axioms.remove(negOPa));
        assertTrue(axioms.remove(negOPaa));
        assertTrue(axioms.isEmpty());
    }
    public void testClassAssertion() throws Exception {
        Axiom ax1=ClassAssertion.create(C("C1"),NI("b1"));
        Axiom ax2=ClassAssertion.create(C("C2"),NI("b2"), ANN(AP("rdfs:label"), TL("rst2anno")));
        Axiom ax3=ClassAssertion.create(C("C3"),NI("b3"), ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testObjectPropertyAssertion() throws Exception {
        Axiom ax1=ObjectPropertyAssertion.create(OP("op1"),NI("a1"),NI("b1"));
        Axiom ax2=ObjectPropertyAssertion.create(OP("op2"),NI("a2"),NI("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Axiom ax3=ObjectPropertyAssertion.create(OP("op3"),NI("a3"),NI("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testDataPropertyAssertion() throws Exception {
        Axiom ax1=DataPropertyAssertion.create(DP("dp1"),NI("a1"),TL("b1"));
        Axiom ax2=DataPropertyAssertion.create(DP("dp2"),NI("a2"),TL("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Axiom ax3=DataPropertyAssertion.create(DP("dp3"),NI("a3"),TL("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testSameAs() throws Exception {
        Axiom ax1=SameIndividual.create(NI("a1"),NI("b1"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        Axiom ax2=SameIndividual.create(inds, ANN(AP("rdfs:label"), TL("rst2anno")));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        Axiom ax3=SameIndividual.create(inds2, ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(ax1));
        assertTrue(axioms.remove(ax2));
        assertTrue(axioms.remove(ax3));
        assertTrue(axioms.isEmpty());
    }
    public void testDifferentFrom() throws Exception {
        Axiom diffInds=DifferentIndividuals.create(NI("a1"),NI("b1"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        Axiom diffIndsa=DifferentIndividuals.create(inds, ANN(AP("rdfs:label"), TL("rst2anno")));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        Axiom diffIndsaa=DifferentIndividuals.create(inds2, ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=diffInds.toTurtleString()+diffIndsa.toTurtleString()+diffIndsaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(diffInds));
        assertTrue(axioms.remove(diffIndsa));
        assertTrue(axioms.remove(diffIndsaa));
        assertTrue(axioms.isEmpty());
    }
    public void testAllDifferent() throws Exception {
        Axiom diffInds=DifferentIndividuals.create(NI("a1"),NI("b1"),NI("c1"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        inds.add(NI("c2"));
        Axiom diffIndsa=DifferentIndividuals.create(inds, ANN(AP("rdfs:label"), TL("rst2anno")));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        inds2.add(NI("c3"));
        Axiom diffIndsaa=DifferentIndividuals.create(inds2, ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=diffInds.toTurtleString()+diffIndsa.toTurtleString()+diffIndsaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(diffInds));
        assertTrue(axioms.remove(diffIndsa));
        assertTrue(axioms.remove(diffIndsaa));
        assertTrue(axioms.isEmpty());
    }
    public void testAllDisjointDataProperties() throws Exception {
        Axiom djop=DisjointDataProperties.create(SET(DP("dp1"),DP("dp2"),DP("dp3")));
        Axiom djopa=DisjointDataProperties.create(SET(DP("dp1"), DP("dp2"), DP("dp3")), SET(ANN(AP("rdfs:label"), TL("rst2anno"))));
        Axiom djopaa=DisjointDataProperties.create(SET(DP("dp1"), DP("dp2"), DP("dp3")), SET(ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno")))));
        String s=djop.toTurtleString()+djopa.toTurtleString()+djopaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(djop));
        assertTrue(axioms.remove(djopa));
        assertTrue(axioms.remove(djopaa));
        assertTrue(axioms.isEmpty());
    }
    public void testAllDisjointObjectProperties() throws Exception {
        Axiom djop=DisjointObjectProperties.create(SET(OP("r1"),OP("s1"),OP("t1")));
        Axiom djopa=DisjointObjectProperties.create(SET(OP("r2"), OP("s2"), OP("t2")), SET(ANN(AP("rdfs:label"), TL("rst2anno"))));
        Axiom djopaa=DisjointObjectProperties.create(SET(OP("r3"), OP("s3"), OP("t3")), SET(ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno")))));
        String s=djop.toTurtleString()+djopa.toTurtleString()+djopaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(djop));
        assertTrue(axioms.remove(djopa));
        assertTrue(axioms.remove(djopaa));
        assertTrue(axioms.isEmpty());
    }
    public void testDisjointWithClasses() throws Exception {
        Axiom dc=DisjointClasses.create(C("C"),C("D"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(C("E"));
        ces.add(C("F"));
        Axiom dca=DisjointClasses.create(ces, ANN(AP("rdfs:label"), TL("EFanno")));
        ces=new HashSet<ClassExpression>();
        ces.add(C("G"));
        ces.add(C("H"));
        Axiom dcaa=DisjointClasses.create(ces, ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=dc.toTurtleString()+dca.toTurtleString()+dcaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(dc));
        assertTrue(axioms.remove(dca));
        assertTrue(axioms.remove(dcaa));
        assertTrue(axioms.isEmpty());
    }
    public void testAllDisjointClasses() throws Exception {
        Axiom dc=DisjointClasses.create(C("C1"),C("D1"),C("E1"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(C("C2"));
        ces.add(C("D2"));
        ces.add(C("E2"));
        Axiom dca=DisjointClasses.create(ces, ANN(AP("rdfs:label"), TL("CDE2anno")));
        Set<ClassExpression> ces2=new HashSet<ClassExpression>();
        ces2.add(C("C3"));
        ces2.add(C("D3"));
        ces2.add(C("E3"));
        Axiom dcaa=DisjointClasses.create(ces2, ANN(AP("rdfs:comment"), TL("CDE3Aanno"), ANN(AP("rdfs:label"), TL("CDE3annoAnno"))));
        String s=dc.toTurtleString()+dca.toTurtleString()+dcaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(dc));
        assertTrue(axioms.remove(dca));
        assertTrue(axioms.remove(dcaa));
        assertTrue(axioms.isEmpty());
    }
    public void testDeclarationsWithAnnotationAnnotations() throws Exception {
        Annotation caa=ANN(AP("ap"), TL("caa"));
        Annotation ca=ANN(AP("ap"), TL("ca"),caa);
        Annotation opaa=ANN(AP("ap"), TL("opaa"));
        Annotation opa=ANN(AP("ap"), TL("opa"),opaa);
        Annotation dpaa=ANN(AP("ap"), TL("dpaa"));
        Annotation dpa=ANN(AP("ap"), TL("dpa"),dpaa);
        Annotation apaa=ANN(AP("ap"), TL("apaa"));
        Annotation apa=ANN(AP("ap"), TL("apa"),apaa);
        Annotation niaa=ANN(AP("ap"), TL("niaa"));
        Annotation nia=ANN(AP("ap"), TL("nia"),niaa);
        Annotation dtaa=ANN(AP("ap"), TL("dtaa"));
        Annotation dta=ANN(AP("ap"), TL("dta"),dtaa);
        Declaration c=Declaration.create(C("C"), ca);
        Declaration op=Declaration.create(OP("op"), opa);
        Declaration dp=Declaration.create(DP("dp"), dpa);
        Declaration ap=Declaration.create(AP("ap"), apa);
        Declaration ni=Declaration.create(NI("ni"), nia);
        Declaration dt=Declaration.create(DT("dt"), dta);
        String s=c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(c));
        assertTrue(axioms.remove(op));
        assertTrue(axioms.remove(dp));
        assertTrue(axioms.remove(ap));
        assertTrue(axioms.remove(ni));
        assertTrue(axioms.remove(dt));
        assertTrue(axioms.isEmpty());
    }
    public void testDeclarationsWithAnnotations() throws Exception {
        Annotation ca=ANN(AP("ap"), TL("ca"));
        Annotation opa=ANN(AP("ap"), TL("opa"));
        Annotation dpa=ANN(AP("ap"), TL("dpa"));
        Annotation apa=ANN(AP("ap"), TL("apa"));
        Annotation nia=ANN(AP("ap"), TL("nia"));
        Annotation dta=ANN(AP("ap"), TL("dta"));
        Declaration c=Declaration.create(C("C"), ca);
        Declaration op=Declaration.create(OP("op"), opa);
        Declaration dp=Declaration.create(DP("dp"), dpa);
        Declaration ap=Declaration.create(AP("ap"), apa);
        Declaration ni=Declaration.create(NI("ni"), nia);
        Declaration dt=Declaration.create(DT("dt"), dta);
        String s=c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(c));
        assertTrue(axioms.remove(op));
        assertTrue(axioms.remove(dp));
        assertTrue(axioms.remove(ap));
        assertTrue(axioms.remove(ni));
        assertTrue(axioms.remove(dt));
        assertTrue(axioms.isEmpty());
    }
    public void testDeclarations() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration op=Declaration.create(OP("op"));
        Declaration dp=Declaration.create(DP("dp"));
        Declaration ap=Declaration.create(AP("ap"));
        Declaration ni=Declaration.create(NI("ni"));
        Declaration dt=Declaration.create(DT("dt"));
        String s=c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(c));
        assertTrue(axioms.remove(op));
        assertTrue(axioms.remove(dp));
        assertTrue(axioms.remove(ap));
        assertTrue(axioms.remove(ni));
        assertTrue(axioms.remove(dt));
        assertTrue(axioms.isEmpty());
    }
    public void testAnnotationAssertion() throws Exception {
        String s=IRI("CDepr").toString()+" rdf:type owl:DeprecatedClass . "+IRI("PropDepr").toString()+" rdf:type owl:DeprecatedProperty . ";
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        AnnotationAssertion assertion=AnnotationAssertion.create((AnnotationPropertyExpression)AP("owl:deprecated"), (AnnotationSubject)IRI("CDepr"), (AnnotationValue)TL("true", null, IRI("xsd:boolean")));
        AnnotationAssertion assertion2=AnnotationAssertion.create((AnnotationPropertyExpression)AP("owl:deprecated"), (AnnotationSubject)IRI("PropDepr"), (AnnotationValue)TL("true", null, IRI("xsd:boolean")));
        assertTrue(axioms.remove(assertion));
        assertTrue(axioms.remove(assertion2));
        assertTrue(axioms.isEmpty());
    }
    public void testSubAnnotationPropertyOf() throws Exception {
        Axiom sap1=SubAnnotationPropertyOf.create(AP("ap1"), AP("ap2"));
        Axiom sap3=SubAnnotationPropertyOf.create(AP("ap1"), AP("ap3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sap4=SubAnnotationPropertyOf.create(AP("ap2"), AP("ap2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sap5=SubAnnotationPropertyOf.create(AP("ap3"), AP("ap2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sap6=SubAnnotationPropertyOf.create(AP("ap2"), AP("ap3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=sap1.toTurtleString()+sap3.toTurtleString()
            +sap4.toTurtleString()+sap5.toTurtleString()+sap6.toTurtleString();
        Ontology o=ParserManager.parseBGP(str, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sap1));
        assertTrue(axioms.remove(sap3));
        assertTrue(axioms.remove(sap4));
        assertTrue(axioms.remove(sap5));
        assertTrue(axioms.remove(sap6));
        assertTrue(axioms.isEmpty());
    }
    public void testAnnotationPropertyDomain() throws Exception {
        Axiom sco=AnnotationPropertyDomain.create(AP("ap1"),IRI("iri1"));
        Axiom scoa=AnnotationPropertyDomain.create(AP("ap1"),IRI("iri2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=AnnotationPropertyDomain.create(AP("ap3"),IRI("iri3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
    public void testAnnotationPropertyRange() throws Exception {
        Axiom sco=AnnotationPropertyRange.create(AP("ap1"),IRI("iri1"));
        Axiom scoa=AnnotationPropertyRange.create(AP("ap1"),IRI("iri2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Axiom scoaa=AnnotationPropertyRange.create(AP("ap3"),IRI("iri3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        Ontology o=ParserManager.parseBGP(s, queriedOntology);
        Set<Axiom> axioms=new HashSet<Axiom>(o.getAxioms());
        assertTrue(axioms.remove(sco));
        assertTrue(axioms.remove(scoa));
        assertTrue(axioms.remove(scoaa));
        assertTrue(axioms.isEmpty());
    }
}