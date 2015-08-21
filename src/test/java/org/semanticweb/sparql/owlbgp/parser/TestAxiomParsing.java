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
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.AbstractTest;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.AnnotationSubject;
import org.semanticweb.sparql.owlbgp.model.AnnotationValue;
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

public class TestAxiomParsing extends AbstractTest {
    public static final String LB = System.getProperty("line.separator") ;

    public void testDatatypeDefinition() throws Exception {
        Declaration dt1=Declaration.create(DT("<http://example.org/dt1>"));
        Declaration dt2=Declaration.create(DT("<http://example.org/dt2>"));
        Declaration dt3=Declaration.create(DT("<http://example.org/dt3>"));
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
        String s=dt1.toTurtleString()+dt2.toTurtleString()+dt3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(dt1));
        assertTrue(consumer.getAxioms().remove(dt2));
        assertTrue(consumer.getAxioms().remove(dt3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testTransitiveObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=TransitiveObjectProperty.create(OP("op1"));
        Axiom ax2=TransitiveObjectProperty.create(IOP("op2"));
        Axiom ax3=TransitiveObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=TransitiveObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=TransitiveObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=TransitiveObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAsymmetricObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=AsymmetricObjectProperty.create(OP("op1"));
        Axiom ax2=AsymmetricObjectProperty.create(IOP("op2"));
        Axiom ax3=AsymmetricObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=AsymmetricObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=AsymmetricObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=AsymmetricObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSymmetricObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=SymmetricObjectProperty.create(OP("op1"));
        Axiom ax2=SymmetricObjectProperty.create(IOP("op2"));
        Axiom ax3=SymmetricObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=SymmetricObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=SymmetricObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=SymmetricObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testIrreflexiveObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=IrreflexiveObjectProperty.create(OP("op1"));
        Axiom ax2=IrreflexiveObjectProperty.create(IOP("op2"));
        Axiom ax3=IrreflexiveObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=IrreflexiveObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=IrreflexiveObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=IrreflexiveObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testReflexiveObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=ReflexiveObjectProperty.create(OP("op1"));
        Axiom ax2=ReflexiveObjectProperty.create(IOP("op2"));
        Axiom ax3=ReflexiveObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=ReflexiveObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=ReflexiveObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=ReflexiveObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testInverseFunctionalObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=InverseFunctionalObjectProperty.create(OP("op1"));
        Axiom ax2=InverseFunctionalObjectProperty.create(IOP("op2"));
        Axiom ax3=InverseFunctionalObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=InverseFunctionalObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=InverseFunctionalObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=InverseFunctionalObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testFunctionalDataProp() throws Exception {
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Declaration dp3=Declaration.create(DP("dp3"));
        Declaration dp4=Declaration.create(DP("dp4"));
        Axiom ax1=FunctionalDataProperty.create(DP("dp1"));
        Axiom ax2=FunctionalDataProperty.create(DP("dp2"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax3=FunctionalDataProperty.create(DP("dp3"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax4=FunctionalDataProperty.create(DP("dp4"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()+dp4.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()+ax4.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().remove(dp4));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testFunctionalObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration op4=Declaration.create(OP("op4"));
        Declaration op5=Declaration.create(OP("op5"));
        Declaration op6=Declaration.create(OP("op6"));
        Axiom ax1=FunctionalObjectProperty.create(OP("op1"));
        Axiom ax2=FunctionalObjectProperty.create(IOP("op2"));
        Axiom ax3=FunctionalObjectProperty.create(OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax4=FunctionalObjectProperty.create(IOP("op4"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax5=FunctionalObjectProperty.create(IOP("op5"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax6=FunctionalObjectProperty.create(OP("op6"),  
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString()
            +ax4.toTurtleString()+ax5.toTurtleString()+ax6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(ax6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(op4));
        assertTrue(consumer.getAxioms().remove(op5));
        assertTrue(consumer.getAxioms().remove(op6));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testInvObjPropOf() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
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
        
//        System.out.println(sop1.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop2.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop2a.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop3.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop4.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop4a.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop5.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop6.toTurtleString());
//        System.out.println("---------------");
//        System.out.println(sop6a.toTurtleString());
//        System.out.println("---------------");
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()
            +sop6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
//        consumer.debug=true;
        parser.parse();
//        for (Axiom ax : consumer.getAxioms())
//            System.out.println(ax);
//        System.out.println("---------------");
        assertTrue(consumer.getAxioms().remove(sop1));
        assertTrue(consumer.getAxioms().remove(sop2) || consumer.getAxioms().remove(sop2a));
        assertTrue(consumer.getAxioms().remove(sop3));
        assertTrue(consumer.getAxioms().remove(sop4) || consumer.getAxioms().remove(sop4a));
        assertTrue(consumer.getAxioms().remove(sop5));
        assertTrue(consumer.getAxioms().remove(sop6) || consumer.getAxioms().remove(sop6a));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivObjProp() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Axiom sop1=EquivalentObjectProperties.create(OP("op1"), OP("op2"));
        Axiom sop2=EquivalentObjectProperties.create(IOP("op2"), OP("op3"));
        Axiom sop3=EquivalentObjectProperties.create(OP("op1"), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sop4=EquivalentObjectProperties.create(IOP("op1"), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop5=EquivalentObjectProperties.create(IOP("op1"), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6=EquivalentObjectProperties.create(OP("op2"), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()+sop6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sop1));
        assertTrue(consumer.getAxioms().remove(sop2));
        assertTrue(consumer.getAxioms().remove(sop3));
        assertTrue(consumer.getAxioms().remove(sop4));
        assertTrue(consumer.getAxioms().remove(sop5));
        assertTrue(consumer.getAxioms().remove(sop6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubChainObjPropOf() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Axiom sop1=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op1"), OP("op1")), OP("op2"));
        Axiom sop2=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op3"), OP("op3"), OP("op1"), IOP("op2"), IOP("op3")), OP("op3"));
        Axiom sop3=SubObjectPropertyOf.create(ObjectPropertyChain.create(IOP("op1"), OP("op1")), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sop4=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op1"), IOP("op1"), OP("op3")), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop5=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op3"), OP("op3"), IOP("op3")), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6=SubObjectPropertyOf.create(ObjectPropertyChain.create(OP("op1"), OP("op2"), OP("op2")), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()+sop6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sop1));
        assertTrue(consumer.getAxioms().remove(sop2));
        assertTrue(consumer.getAxioms().remove(sop3));
        assertTrue(consumer.getAxioms().remove(sop4));
        assertTrue(consumer.getAxioms().remove(sop5));
        assertTrue(consumer.getAxioms().remove(sop6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubObjPropOf() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Axiom sop1=SubObjectPropertyOf.create(OP("op1"), OP("op2"));
        Axiom sop2=SubObjectPropertyOf.create(IOP("op2"), OP("op3"));
        Axiom sop3=SubObjectPropertyOf.create(OP("op1"), OP("op3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sop4=SubObjectPropertyOf.create(IOP("op1"), OP("op2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sop5=SubObjectPropertyOf.create(IOP("op1"), IOP("op2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sop6=SubObjectPropertyOf.create(OP("op2"), IOP("op3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()+sop6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sop1));
        assertTrue(consumer.getAxioms().remove(sop2));
        assertTrue(consumer.getAxioms().remove(sop3));
        assertTrue(consumer.getAxioms().remove(sop4));
        assertTrue(consumer.getAxioms().remove(sop5));
        assertTrue(consumer.getAxioms().remove(sop6));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubDataPropOf() throws Exception {
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Declaration dp3=Declaration.create(DP("dp3"));
        Axiom ax1=SubDataPropertyOf.create(DP("dp1"), DP("dp2"));
        Axiom ax2=SubDataPropertyOf.create(DP("dp1"), DP("dp3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom ax3=SubDataPropertyOf.create(DP("dp2"), DP("dp1"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom ax4=SubDataPropertyOf.create(DP("dp2"), DP("dp2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom ax5=SubDataPropertyOf.create(DP("dp3"), DP("dp2"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()
            +ax3.toTurtleString()+ax4.toTurtleString()+ax5.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(ax4));
        assertTrue(consumer.getAxioms().remove(ax5));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointUnionOfClasses() throws Exception {
        Declaration c1=Declaration.create(C("C1"));
        Declaration d1=Declaration.create(C("D1"));
        Declaration e1=Declaration.create(C("E1"));
        Declaration f1=Declaration.create(C("F1"));
        Declaration op1=Declaration.create(OP("op1"));
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
        String s=c1.toTurtleString()+d1.toTurtleString()+e1.toTurtleString()+f1.toTurtleString()+op1.toTurtleString()
            +ax1a.toTurtleString()+ax1b.toTurtleString()+ax2a.toTurtleString()+ax2b.toTurtleString()+ax3a.toTurtleString()+ax3b.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1a));
        assertTrue(consumer.getAxioms().remove(ax1b));
        assertTrue(consumer.getAxioms().remove(ax2a));
        assertTrue(consumer.getAxioms().remove(ax2b));
        assertTrue(consumer.getAxioms().remove(ax3a));
        assertTrue(consumer.getAxioms().remove(ax3b));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(d1));
        assertTrue(consumer.getAxioms().remove(e1));
        assertTrue(consumer.getAxioms().remove(f1));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testHasKey2() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        ClassExpression cAndD=ObjectIntersectionOf.create(C("C"), C("D"));
        Declaration op=Declaration.create(OP("op"));
        Declaration dp=Declaration.create(DP("dp"));
        Axiom hasKey=HasKey.create(cAndD, OP("op"), DP("dp"));
        Set<PropertyExpression> props=new HashSet<PropertyExpression>();
        props.add(DP("dp"));
        Axiom hasKeyAnn=HasKey.create((ClassExpression)ObjectUnionOf.create(C("C"), C("D")),props,Collections.singleton(ANN(AP("rdfs:label"), TL("C or D anno"))));
        props=new HashSet<PropertyExpression>();
        props.add(DP("dp"));
        props.add(OP("op"));
        Axiom hasKeyAnnAnn=HasKey.create((ClassExpression)ObjectComplementOf.create(C("C")),props,
                Collections.singleton(ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno")))));
        String s=c.toTurtleString()+d.toTurtleString()+op.toTurtleString()+dp.toTurtleString()
            +hasKey.toTurtleString()+hasKeyAnn.toTurtleString()+hasKeyAnnAnn.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(hasKey));
        assertTrue(consumer.getAxioms().remove(hasKeyAnn));
        assertTrue(consumer.getAxioms().remove(hasKeyAnnAnn));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testHasKey() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration op=Declaration.create(OP("op"));
        Axiom hasKey=HasKey.create(C("C"), OP("op"));
        String s=c.toTurtleString()+op.toTurtleString()
            +hasKey.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(hasKey));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyRangeComplex() throws Exception {
        Declaration c=Declaration.create(DT("C"));
        Declaration d=Declaration.create(DT("D"));
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration op=Declaration.create(OP("op"));
        Axiom sco=DataPropertyRange.create(DP("dp1"), DataIntersectionOf.create(DT("C"),DT("D")));
        Declaration e=Declaration.create(DT("E"));
        Declaration f=Declaration.create(DT("F"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Axiom scoa=DataPropertyRange.create(DP("dp2"), 
                DataUnionOf.create(DT("C"), DataIntersectionOf.create(DT("E"),DT("F"))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration dp3=Declaration.create(DP("dp3"));
        Axiom scoaa=DataPropertyRange.create(DP("dp3"),
                DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
            +dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()+op.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyRange() throws Exception {
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration c1=Declaration.create(DT("C1"));
        Axiom sco=DataPropertyRange.create(DP("dp1"),DT("C1"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Declaration c2=Declaration.create(DT("C2"));
        Axiom scoa=DataPropertyRange.create(DP("dp2"),DT("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration dp3=Declaration.create(DP("dp3"));
        Declaration c3=Declaration.create(DT("C3"));
        Axiom scoaa=DataPropertyRange.create(DP("dp3"),DT("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=dp1.toTurtleString()+c1.toTurtleString()+dp2.toTurtleString()+c2.toTurtleString()+dp3.toTurtleString()+c3.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyRangeComplex() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Declaration r=Declaration.create(OP("r"));
        Axiom sco=ObjectPropertyRange.create(IOP("r"), ObjectSomeValuesFrom.create(IOP("r"), ObjectComplementOf.create(C("C"))));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Declaration s=Declaration.create(OP("s"));
        Axiom scoa=ObjectPropertyRange.create(OP("s"), 
                ObjectAllValuesFrom.create(IOP("s"), ObjectIntersectionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration t=Declaration.create(DP("t"));
        Axiom scoaa=ObjectPropertyRange.create(OP("r"),
                DataExactCardinality.create(5, DP("t"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
            +r.toTurtleString()+s.toTurtleString()+t.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(r));
        assertTrue(consumer.getAxioms().remove(s));
        assertTrue(consumer.getAxioms().remove(t));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyRange() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration c1=Declaration.create(C("C1"));
        Axiom sco=ObjectPropertyRange.create(OP("op1"),C("C1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration c2=Declaration.create(C("C2"));
        Axiom scoa=ObjectPropertyRange.create(IOP("op1"),C("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration c3=Declaration.create(C("C3"));
        Axiom scoaa=ObjectPropertyRange.create(OP("op3"),C("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=op1.toTurtleString()+c1.toTurtleString()+op2.toTurtleString()+c2.toTurtleString()+op3.toTurtleString()+c3.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    
    public void testDataPropertyDomainComplex() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration op=Declaration.create(OP("op"));
        Axiom sco=DataPropertyDomain.create(DP("dp1"), ObjectSomeValuesFrom.create(IOP("op"), ObjectIntersectionOf.create(C("C"),C("D"))));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Axiom scoa=DataPropertyDomain.create(DP("dp2"), 
                ObjectAllValuesFrom.create(OP("op"), ObjectUnionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration dp3=Declaration.create(DP("dp3"));
        Axiom scoaa=DataPropertyDomain.create(DP("dp3"),
                DataMaxCardinality.create(5, DP("dp1"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
            +dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()+op.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyDomain() throws Exception {
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration c1=Declaration.create(C("C1"));
        Axiom sco=DataPropertyDomain.create(DP("dp1"),C("C1"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Declaration c2=Declaration.create(C("C2"));
        Axiom scoa=DataPropertyDomain.create(DP("dp2"),C("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration dp3=Declaration.create(DP("dp3"));
        Declaration c3=Declaration.create(C("C3"));
        Axiom scoaa=DataPropertyDomain.create(DP("dp3"),C("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=dp1.toTurtleString()+c1.toTurtleString()+dp2.toTurtleString()+c2.toTurtleString()+dp3.toTurtleString()+c3.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyDomainComplex() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Declaration r=Declaration.create(OP("r"));
        Axiom sco=ObjectPropertyDomain.create(IOP("r"), ObjectSomeValuesFrom.create(IOP("r"), ObjectIntersectionOf.create(C("C"),C("D"))));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Declaration s=Declaration.create(OP("s"));
        Axiom scoa=ObjectPropertyDomain.create(OP("s"), 
                ObjectAllValuesFrom.create(IOP("s"), ObjectUnionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))),
                ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration t=Declaration.create(DP("t"));
        Axiom scoaa=ObjectPropertyDomain.create(OP("r"),
                DataMaxCardinality.create(5, DP("t"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
            +r.toTurtleString()+s.toTurtleString()+t.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(r));
        assertTrue(consumer.getAxioms().remove(s));
        assertTrue(consumer.getAxioms().remove(t));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyDomain() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration c1=Declaration.create(C("C1"));
        Axiom sco=ObjectPropertyDomain.create(OP("op1"),C("C1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration c2=Declaration.create(C("C2"));
        Axiom scoa=ObjectPropertyDomain.create(IOP("op1"),C("C2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration c3=Declaration.create(C("C3"));
        Axiom scoaa=ObjectPropertyDomain.create(OP("op3"),C("C3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=op1.toTurtleString()+c1.toTurtleString()+op2.toTurtleString()+c2.toTurtleString()+op3.toTurtleString()+c3.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointWithObjectProperty() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Axiom spo=DisjointObjectProperties.create(IOP("op1"), OP("op2"));
        Axiom spoa=DisjointObjectProperties.create(OP("op1"),OP("op3"),ANN(AP("rdfs:label"), TL("opanno")));
        Axiom spoaa=DisjointObjectProperties.create(OP("op2"),IOP("op3"),ANN(AP("rdfs:comment"), TL("opAanno"), ANN(AP("rdfs:label"), TL("opannoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(spo));
        assertTrue(consumer.getAxioms().remove(spoa));
        assertTrue(consumer.getAxioms().remove(spoaa));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointWithDataProperty() throws Exception {
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Declaration dp3=Declaration.create(DP("dp3"));
        Axiom spo=DisjointDataProperties.create(DP("dp1"), DP("dp2"));
        Axiom spoa=DisjointDataProperties.create(DP("dp1"),DP("dp3"),ANN(AP("rdfs:label"), TL("dpanno")));
        Axiom spoaa=DisjointDataProperties.create(DP("dp2"),DP("dp3"),ANN(AP("rdfs:comment"), TL("dpAanno"), ANN(AP("rdfs:label"), TL("dpannoAnno"))));
        String str=dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()
            +spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(spo));
        assertTrue(consumer.getAxioms().remove(spoa));
        assertTrue(consumer.getAxioms().remove(spoaa));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentDataProperties() throws Exception {
        Declaration dp1=Declaration.create(DP("dp1"));
        Declaration dp2=Declaration.create(DP("dp2"));
        Declaration dp3=Declaration.create(DP("dp3"));
        Axiom spo=EquivalentDataProperties.create(DP("dp1"), DP("dp2"));
        Axiom spoa=EquivalentDataProperties.create(DP("dp1"),DP("dp3"),ANN(AP("rdfs:label"), TL("dpanno")));
        Axiom spoaa=EquivalentDataProperties.create(DP("dp2"),DP("dp3"),ANN(AP("rdfs:comment"), TL("dpAanno"), ANN(AP("rdfs:label"), TL("dpannoAnno"))));
        String str=dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()
            +spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(spo));
        assertTrue(consumer.getAxioms().remove(spoa));
        assertTrue(consumer.getAxioms().remove(spoaa));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentObjectProperties() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration op3=Declaration.create(OP("op3"));
        Axiom spo=EquivalentObjectProperties.create(IOP("op1"), OP("op2"));
        Axiom spoa=EquivalentObjectProperties.create(OP("op1"),OP("op3"),ANN(AP("rdfs:label"), TL("opanno")));
        Axiom spoaa=EquivalentObjectProperties.create(OP("op2"),IOP("op3"),ANN(AP("rdfs:comment"), TL("opAanno"), ANN(AP("rdfs:label"), TL("opannoAnno"))));
        String str=op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +spo.toTurtleString()+spoa.toTurtleString()+spoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(spo));
        assertTrue(consumer.getAxioms().remove(spoa));
        assertTrue(consumer.getAxioms().remove(spoaa));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentClassComplex() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Declaration r=Declaration.create(OP("r"));
        Axiom sco=EquivalentClasses.create(ObjectSomeValuesFrom.create(IOP("r"), C("C")),ObjectIntersectionOf.create(C("C"),C("D")));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Declaration s=Declaration.create(OP("s"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(ObjectSomeValuesFrom.create(OP("s"), ObjectComplementOf.create(ObjectIntersectionOf.create(C("E"),C("F")))));
        ces.add(ObjectMinCardinality.create(5, IOP("r")));
        Axiom scoa=EquivalentClasses.create(ces,ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration t=Declaration.create(DP("t"));
        Axiom scoaa=EquivalentClasses.create(
                SET(DataAllValuesFrom.create(DP("t"), DataIntersectionOf.create(DT("xsd:string"), DataComplementOf.create(DT("rdf:PlainLiteral")))),
                    DataExactCardinality.create(1000, DP("t"), DatatypeRestriction.create(DT("xsd:decimal"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_INTEGER))))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
            +r.toTurtleString()+s.toTurtleString()+t.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(r));
        assertTrue(consumer.getAxioms().remove(s));
        assertTrue(consumer.getAxioms().remove(t));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentClasses() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Axiom sco=EquivalentClasses.create(C("C"),C("D"));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Axiom scoa=EquivalentClasses.create(SET(C("E"),C("F")),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration g=Declaration.create(C("G"));
        Declaration h=Declaration.create(C("H"));
        Axiom scoaa=EquivalentClasses.create(SET(C("G"), C("H")),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()+g.toTurtleString()+h.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(g));
        assertTrue(consumer.getAxioms().remove(h));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubClassOfComplex() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Declaration r=Declaration.create(OP("r"));
        Axiom sco=SubClassOf.create(ObjectSomeValuesFrom.create(IOP("r"), C("C")),ObjectIntersectionOf.create(C("C"),C("D")));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Declaration s=Declaration.create(OP("s"));
        Axiom scoa=SubClassOf.create(ObjectAllValuesFrom.create(OP("s"), ObjectUnionOf.create(C("C"), ObjectIntersectionOf.create(C("E"),C("F")))), ObjectMinCardinality.create(5, OP("r"), ObjectIntersectionOf.create(C("E"),C("F"))),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration t=Declaration.create(DP("t"));
        Axiom scoaa=SubClassOf.create(
                DataSomeValuesFrom.create(DP("t"), DataUnionOf.create(DT("xsd:string"), DT("rdf:PlainLiteral"))), 
                DataMaxCardinality.create(5, DP("t"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String str=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
            +r.toTurtleString()+s.toTurtleString()+t.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(r));
        assertTrue(consumer.getAxioms().remove(s));
        assertTrue(consumer.getAxioms().remove(t));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubClassOf() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Axiom sco=SubClassOf.create(C("C"),C("D"));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Axiom scoa=SubClassOf.create(C("E"),C("F"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration g=Declaration.create(C("G"));
        Declaration h=Declaration.create(C("H"));
        Axiom scoaa=SubClassOf.create(C("G"),C("H"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()+g.toTurtleString()+h.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(g));
        assertTrue(consumer.getAxioms().remove(h));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testNegativeDataPropertyAssertion() throws Exception {
        Declaration a1=Declaration.create(NI("a1"));
        Declaration r1=Declaration.create(DP("r1"));
        Axiom negDP=NegativeDataPropertyAssertion.create(DP("r1"),NI("a1"),TL("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration r2=Declaration.create(DP("r2"));
        Axiom negDPa=NegativeDataPropertyAssertion.create(DP("r2"),NI("a2"),TL("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration r3=Declaration.create(DP("r3"));
        Axiom negDPaa=NegativeDataPropertyAssertion.create(DP("r3"),NI("a3"),TL("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=a1.toTurtleString()+r1.toTurtleString()
            +a2.toTurtleString()+r2.toTurtleString()
            +a3.toTurtleString()+r3.toTurtleString()
            +negDP.toTurtleString()+negDPa.toTurtleString()+negDPaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(negDP));
        assertTrue(consumer.getAxioms().remove(negDPa));
        assertTrue(consumer.getAxioms().remove(negDPaa));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(r1));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(r2));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().remove(r3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testNegativeObjectPropertyAssertion() throws Exception {
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Declaration r1=Declaration.create(OP("r1"));
        Axiom negOP=NegativeObjectPropertyAssertion.create(OP("r1"),NI("a1"),NI("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Declaration r2=Declaration.create(OP("r2"));
        Axiom negOPa=NegativeObjectPropertyAssertion.create(OP("r2"),NI("a2"),NI("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Declaration r3=Declaration.create(OP("r3"));
        Axiom negOPaa=NegativeObjectPropertyAssertion.create(OP("r3"),NI("a3"),NI("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=a1.toTurtleString()+b1.toTurtleString()+r1.toTurtleString()
            +a2.toTurtleString()+b2.toTurtleString()+r2.toTurtleString()
            +a3.toTurtleString()+b3.toTurtleString()+r3.toTurtleString()
            +negOP.toTurtleString()+negOPa.toTurtleString()+negOPaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(negOP));
        assertTrue(consumer.getAxioms().remove(negOPa));
        assertTrue(consumer.getAxioms().remove(negOPaa));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(b1));
        assertTrue(consumer.getAxioms().remove(r1));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(b2));
        assertTrue(consumer.getAxioms().remove(r2));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().remove(b3));
        assertTrue(consumer.getAxioms().remove(r3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testClassAssertion() throws Exception {
        Declaration c1=Declaration.create(C("C1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom ax1=ClassAssertion.create(C("C1"),NI("b1"));
        Declaration c2=Declaration.create(C("C2"));
        Declaration b2=Declaration.create(NI("b2"));
        Axiom ax2=ClassAssertion.create(C("C2"),NI("b2"), ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration c3=Declaration.create(C("C3"));
        Declaration b3=Declaration.create(NI("b3"));
        Axiom ax3=ClassAssertion.create(C("C3"),NI("b3"), ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=c1.toTurtleString()+b1.toTurtleString()
            +c2.toTurtleString()+b2.toTurtleString()
            +c3.toTurtleString()+b3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(b1));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(b2));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().remove(b3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyAssertion() throws Exception {
        Declaration op1=Declaration.create(OP("op1"));
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom ax1=ObjectPropertyAssertion.create(OP("op1"),NI("a1"),NI("b1"));
        Declaration op2=Declaration.create(OP("op2"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Axiom ax2=ObjectPropertyAssertion.create(OP("op2"),NI("a2"),NI("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration op3=Declaration.create(OP("op3"));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Axiom ax3=ObjectPropertyAssertion.create(OP("op3"),NI("a3"),NI("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=op1.toTurtleString()+a1.toTurtleString()+b1.toTurtleString()
            +op2.toTurtleString()+a2.toTurtleString()+b2.toTurtleString()
            +op3.toTurtleString()+a3.toTurtleString()+b3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(b1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(b2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().remove(b3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyAssertion() throws Exception {
        Declaration dp1=Declaration.create(DP("op1"));
        Declaration a1=Declaration.create(NI("a1"));
        Axiom ax1=DataPropertyAssertion.create(DP("op1"),NI("a1"),TL("b1"));
        Declaration dp2=Declaration.create(DP("op2"));
        Declaration a2=Declaration.create(NI("a2"));
        Axiom ax2=DataPropertyAssertion.create(DP("op2"),NI("a2"),TL("b2"),ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration dp3=Declaration.create(DP("op3"));
        Declaration a3=Declaration.create(NI("a3"));
        Axiom ax3=DataPropertyAssertion.create(DP("op3"),NI("a3"),TL("b3"),ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=dp1.toTurtleString()+a1.toTurtleString()
            +dp2.toTurtleString()+a2.toTurtleString()
            +dp3.toTurtleString()+a3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(dp1));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(dp2));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(dp3));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSameAs() throws Exception {
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom ax1=SameIndividual.create(NI("a1"),NI("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        Axiom ax2=SameIndividual.create(inds, ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        Axiom ax3=SameIndividual.create(inds2, ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=a1.toTurtleString()+b1.toTurtleString()
            +a2.toTurtleString()+b2.toTurtleString()
            +a3.toTurtleString()+b3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(b1));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(b2));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().remove(b3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDifferentFrom() throws Exception {
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom diffInds=DifferentIndividuals.create(NI("a1"),NI("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        Axiom diffIndsa=DifferentIndividuals.create(inds, ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        Axiom diffIndsaa=DifferentIndividuals.create(inds2, ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=a1.toTurtleString()+b1.toTurtleString()
            +a2.toTurtleString()+b2.toTurtleString()
            +a3.toTurtleString()+b3.toTurtleString()
            +diffInds.toTurtleString()+diffIndsa.toTurtleString()+diffIndsaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(diffInds));
        assertTrue(consumer.getAxioms().remove(diffIndsa));
        assertTrue(consumer.getAxioms().remove(diffIndsaa));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(b1));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(b2));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().remove(b3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDifferent() throws Exception {
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Declaration c1=Declaration.create(NI("c1"));
        Axiom diffInds=DifferentIndividuals.create(NI("a1"),NI("b1"),NI("c1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Declaration c2=Declaration.create(NI("c2"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        inds.add(NI("c2"));
        Axiom diffIndsa=DifferentIndividuals.create(inds, ANN(AP("rdfs:label"), TL("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Declaration c3=Declaration.create(NI("c3"));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        inds2.add(NI("c3"));
        Axiom diffIndsaa=DifferentIndividuals.create(inds2, ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno"))));
        String s=a1.toTurtleString()+b1.toTurtleString()+c1.toTurtleString()
            +a2.toTurtleString()+b2.toTurtleString()+c2.toTurtleString()
            +a3.toTurtleString()+b3.toTurtleString()+c3.toTurtleString()
            +diffInds.toTurtleString()+diffIndsa.toTurtleString()+diffIndsaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(diffInds));
        assertTrue(consumer.getAxioms().remove(diffIndsa));
        assertTrue(consumer.getAxioms().remove(diffIndsaa));
        assertTrue(consumer.getAxioms().remove(a1));
        assertTrue(consumer.getAxioms().remove(b1));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(a2));
        assertTrue(consumer.getAxioms().remove(b2));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(a3));
        assertTrue(consumer.getAxioms().remove(b3));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDisjointDataProperties() throws Exception {
        Declaration r1=Declaration.create(DP("r1"));
        Declaration s1=Declaration.create(DP("s1"));
        Declaration t1=Declaration.create(DP("t1"));
        Axiom djop=DisjointDataProperties.create(SET(DP("r1"),DP("s1"),DP("t1")));
        Declaration r2=Declaration.create(DP("r2"));
        Declaration s2=Declaration.create(DP("s2"));
        Declaration t2=Declaration.create(DP("t2"));
        Axiom djopa=DisjointDataProperties.create(SET(DP("r2"), DP("s2"), DP("t2")), SET(ANN(AP("rdfs:label"), TL("rst2anno"))));
        Declaration r3=Declaration.create(DP("r3"));
        Declaration s3=Declaration.create(DP("s3"));
        Declaration t3=Declaration.create(DP("t3"));
        Axiom djopaa=DisjointDataProperties.create(SET(DP("r3"), DP("s3"), DP("t3")), SET(ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno")))));
        String s=r1.toTurtleString()+s1.toTurtleString()+t1.toTurtleString()
            +r2.toTurtleString()+s2.toTurtleString()+t2.toTurtleString()
            +r3.toTurtleString()+s3.toTurtleString()+t3.toTurtleString()
            +djop.toTurtleString()+djopa.toTurtleString()+djopaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(djop));
        assertTrue(consumer.getAxioms().remove(djopa));
        assertTrue(consumer.getAxioms().remove(djopaa));
        assertTrue(consumer.getAxioms().remove(r1));
        assertTrue(consumer.getAxioms().remove(s1));
        assertTrue(consumer.getAxioms().remove(t1));
        assertTrue(consumer.getAxioms().remove(r2));
        assertTrue(consumer.getAxioms().remove(s2));
        assertTrue(consumer.getAxioms().remove(t2));
        assertTrue(consumer.getAxioms().remove(r3));
        assertTrue(consumer.getAxioms().remove(s3));
        assertTrue(consumer.getAxioms().remove(t3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDisjointObjectProperties() throws Exception {
        Declaration r1=Declaration.create(OP("r1"));
        Declaration s1=Declaration.create(OP("s1"));
        Declaration t1=Declaration.create(OP("t1"));
        Axiom djop=DisjointObjectProperties.create(SET(OP("r1"),OP("s1"),OP("t1")));
        Declaration r2=Declaration.create(OP("r2"));
        Declaration s2=Declaration.create(OP("s2"));
        Declaration t2=Declaration.create(OP("t2"));
        Axiom djopa=DisjointObjectProperties.create(SET(OP("r2"), OP("s2"), OP("t2")), SET(ANN(AP("rdfs:label"), TL("rst2anno"))));
        Declaration r3=Declaration.create(OP("r3"));
        Declaration s3=Declaration.create(OP("s3"));
        Declaration t3=Declaration.create(OP("t3"));
        Axiom djopaa=DisjointObjectProperties.create(SET(OP("r3"), OP("s3"), OP("t3")), SET(ANN(AP("rdfs:comment"), TL("rst3Aanno"), ANN(AP("rdfs:label"), TL("rs3annoAnno")))));
        String s=r1.toTurtleString()+s1.toTurtleString()+t1.toTurtleString()
            +r2.toTurtleString()+s2.toTurtleString()+t2.toTurtleString()
            +r3.toTurtleString()+s3.toTurtleString()+t3.toTurtleString()
            +djop.toTurtleString()+djopa.toTurtleString()+djopaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(djop));
        assertTrue(consumer.getAxioms().remove(djopa));
        assertTrue(consumer.getAxioms().remove(djopaa));
        assertTrue(consumer.getAxioms().remove(r1));
        assertTrue(consumer.getAxioms().remove(s1));
        assertTrue(consumer.getAxioms().remove(t1));
        assertTrue(consumer.getAxioms().remove(r2));
        assertTrue(consumer.getAxioms().remove(s2));
        assertTrue(consumer.getAxioms().remove(t2));
        assertTrue(consumer.getAxioms().remove(r3));
        assertTrue(consumer.getAxioms().remove(s3));
        assertTrue(consumer.getAxioms().remove(t3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointWithClasses() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration d=Declaration.create(C("D"));
        Axiom dc=DisjointClasses.create(C("C"),C("D"));
        Declaration e=Declaration.create(C("E"));
        Declaration f=Declaration.create(C("F"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(C("E"));
        ces.add(C("F"));
        Axiom dca=DisjointClasses.create(ces, ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration g=Declaration.create(C("G"));
        Declaration h=Declaration.create(C("H"));
        ces=new HashSet<ClassExpression>();
        ces.add(C("G"));
        ces.add(C("H"));
        Axiom dcaa=DisjointClasses.create(ces, ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()+g.toTurtleString()+h.toTurtleString()+dc.toTurtleString()+dca.toTurtleString()+dcaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(dc));
        assertTrue(consumer.getAxioms().remove(dca));
        assertTrue(consumer.getAxioms().remove(dcaa));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(d));
        assertTrue(consumer.getAxioms().remove(e));
        assertTrue(consumer.getAxioms().remove(f));
        assertTrue(consumer.getAxioms().remove(g));
        assertTrue(consumer.getAxioms().remove(h));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDisjointClasses() throws Exception {
        Declaration c1=Declaration.create(C("C1"));
        Declaration d1=Declaration.create(C("D1"));
        Declaration e1=Declaration.create(C("E1"));
        Axiom dc=DisjointClasses.create(C("C1"),C("D1"),C("E1"));
        Declaration c2=Declaration.create(C("C2"));
        Declaration d2=Declaration.create(C("D2"));
        Declaration e2=Declaration.create(C("E2"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(C("C2"));
        ces.add(C("D2"));
        ces.add(C("E2"));
        Axiom dca=DisjointClasses.create(ces, ANN(AP("rdfs:label"), TL("CDE2anno")));
        Declaration c3=Declaration.create(C("C3"));
        Declaration d3=Declaration.create(C("D3"));
        Declaration e3=Declaration.create(C("E3"));
        Set<ClassExpression> ces2=new HashSet<ClassExpression>();
        ces2.add(C("C3"));
        ces2.add(C("D3"));
        ces2.add(C("E3"));
        Axiom dcaa=DisjointClasses.create(ces2, ANN(AP("rdfs:comment"), TL("CDE3Aanno"), ANN(AP("rdfs:label"), TL("CDE3annoAnno"))));
        String s=c1.toTurtleString()+d1.toTurtleString()+e1.toTurtleString()
            +c2.toTurtleString()+d2.toTurtleString()+e2.toTurtleString()
            +c3.toTurtleString()+d3.toTurtleString()+e3.toTurtleString()
            +dc.toTurtleString()+dca.toTurtleString()+dcaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(dc));
        assertTrue(consumer.getAxioms().remove(dca));
        assertTrue(consumer.getAxioms().remove(dcaa));
        assertTrue(consumer.getAxioms().remove(c1));
        assertTrue(consumer.getAxioms().remove(d1));
        assertTrue(consumer.getAxioms().remove(e1));
        assertTrue(consumer.getAxioms().remove(c2));
        assertTrue(consumer.getAxioms().remove(d2));
        assertTrue(consumer.getAxioms().remove(e2));
        assertTrue(consumer.getAxioms().remove(c3));
        assertTrue(consumer.getAxioms().remove(d3));
        assertTrue(consumer.getAxioms().remove(e3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
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
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().remove(ap));
        assertTrue(consumer.getAxioms().remove(ni));
        assertTrue(consumer.getAxioms().remove(dt));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
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
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().remove(ap));
        assertTrue(consumer.getAxioms().remove(ni));
        assertTrue(consumer.getAxioms().remove(dt));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDeclarations() throws Exception {
        Declaration c=Declaration.create(C("C"));
        Declaration op=Declaration.create(OP("op"));
        Declaration dp=Declaration.create(DP("dp"));
        Declaration ap=Declaration.create(AP("ap"));
        Declaration ni=Declaration.create(NI("ni"));
        Declaration dt=Declaration.create(DT("dt"));
        String s=c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().remove(ap));
        assertTrue(consumer.getAxioms().remove(ni));
        assertTrue(consumer.getAxioms().remove(dt));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAnnotationAssertion() throws Exception {
        String s=IRI("CDepr").toString()+" rdf:type owl:DeprecatedClass . "+IRI("PropDepr").toString()+" rdf:type owl:DeprecatedProperty . ";
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        AnnotationAssertion assertion=AnnotationAssertion.create((AnnotationPropertyExpression)AP("owl:deprecated"), (AnnotationSubject)IRI("CDepr"), (AnnotationValue)TL("true", null, IRI("xsd:boolean")));
        AnnotationAssertion assertion2=AnnotationAssertion.create((AnnotationPropertyExpression)AP("owl:deprecated"), (AnnotationSubject)IRI("PropDepr"), (AnnotationValue)TL("true", null, IRI("xsd:boolean")));
        assertTrue(consumer.getAxioms().remove(assertion));
        assertTrue(consumer.getAxioms().remove(assertion2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubAnnotationPropertyOf() throws Exception {
        Declaration ap1=Declaration.create(AP("ap1"));
        Declaration ap2=Declaration.create(AP("ap2"));
        Declaration ap3=Declaration.create(AP("ap3"));
        Axiom sap1=SubAnnotationPropertyOf.create(AP("ap1"), AP("ap2"));
        Axiom sap3=SubAnnotationPropertyOf.create(AP("ap1"), AP("ap3"), ANN(AP("rdfs:label"), TL("Anno"))); 
        Axiom sap4=SubAnnotationPropertyOf.create(AP("ap2"), AP("ap2"), ANN(AP("rdfs:label"), TL("Anno")), ANN(AP("rdfs:comment"), TL("anno2")));
        Axiom sap5=SubAnnotationPropertyOf.create(AP("ap3"), AP("ap2"), ANN(AP("rdfs:label"), TL("Anno"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        Axiom sap6=SubAnnotationPropertyOf.create(AP("ap2"), AP("ap3"), 
                ANN(AP("rdfs:label"), TL("Anno")), 
                ANN(AP("rdfs:comment"), TL("anno2"), ANN(AP("rdfs:label"), TL("AnnoAnno"))));
        String str=ap1.toTurtleString()+ap2.toTurtleString()+ap3.toTurtleString()
            +sap1.toTurtleString()+sap3.toTurtleString()
            +sap4.toTurtleString()+sap5.toTurtleString()+sap6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sap1));
        assertTrue(consumer.getAxioms().remove(sap3));
        assertTrue(consumer.getAxioms().remove(sap4));
        assertTrue(consumer.getAxioms().remove(sap5));
        assertTrue(consumer.getAxioms().remove(sap6));
        assertTrue(consumer.getAxioms().remove(ap1));
        assertTrue(consumer.getAxioms().remove(ap2));
        assertTrue(consumer.getAxioms().remove(ap3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAnnotationPropertyDomain() throws Exception {
        Declaration ap1=Declaration.create(AP("ap1"));
        Axiom sco=AnnotationPropertyDomain.create(AP("ap1"),IRI("iri1"));
        Declaration ap2=Declaration.create(AP("ap2"));
        Axiom scoa=AnnotationPropertyDomain.create(AP("ap1"),IRI("iri2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration ap3=Declaration.create(AP("ap3"));
        Axiom scoaa=AnnotationPropertyDomain.create(AP("ap3"),IRI("iri3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=ap1.toTurtleString()+ap2.toTurtleString()+ap3.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(ap1));
        assertTrue(consumer.getAxioms().remove(ap2));
        assertTrue(consumer.getAxioms().remove(ap3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAnnotationPropertyRange() throws Exception {
        Declaration ap1=Declaration.create(AP("ap1"));
        Axiom sco=AnnotationPropertyRange.create(AP("ap1"),IRI("iri1"));
        Declaration ap2=Declaration.create(AP("ap2"));
        Axiom scoa=AnnotationPropertyRange.create(AP("ap1"),IRI("iri2"),ANN(AP("rdfs:label"), TL("EFanno")));
        Declaration ap3=Declaration.create(AP("ap3"));
        Axiom scoaa=AnnotationPropertyRange.create(AP("ap3"),IRI("iri3"),ANN(AP("rdfs:comment"), TL("GHAanno"), ANN(AP("rdfs:label"), TL("GHannoAnno"))));
        String s=ap1.toTurtleString()+ap2.toTurtleString()+ap3.toTurtleString()
            +sco.toTurtleString()+scoa.toTurtleString()+scoaa.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sco));
        assertTrue(consumer.getAxioms().remove(scoa));
        assertTrue(consumer.getAxioms().remove(scoaa));
        assertTrue(consumer.getAxioms().remove(ap1));
        assertTrue(consumer.getAxioms().remove(ap2));
        assertTrue(consumer.getAxioms().remove(ap3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
}