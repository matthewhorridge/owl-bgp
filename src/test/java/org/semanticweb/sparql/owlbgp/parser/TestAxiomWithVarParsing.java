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
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;

public class TestAxiomWithVarParsing extends AbstractTest {
    public static final String LB = System.getProperty("line.separator") ;


    public void testDatatypeDefinition() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dt1=Declaration.create(DTV("dt1"));
        Declaration dt2=Declaration.create(DTV("dt2"));
        Declaration dt3=Declaration.create(DTV("dt3"));
        Axiom ax1=DatatypeDefinition.create(DTV("dt1"),DataIntersectionOf.create(DT("xsd:int"), DataComplementOf.create(DT("xsd:string"))));
        Axiom ax2=DatatypeDefinition.create(
                DTV("dt2"),
                DataComplementOf.create(
                        DataUnionOf.create(DT("xsd:int"), DataComplementOf.create(DT("xsd:string")))),
                        ANN(APV("label"), V("EFanno")));
        Axiom ax3=DatatypeDefinition.create(
                DTV("dt2"),
                DataComplementOf.create(
                        DataIntersectionOf.create(DT("xsd:int"), DataOneOf.create(LV("lit1"), LV("lit2")))),
                        ANN(APV("label"), V("EFanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+apv1.toTurtleString()+apv2.toTurtleString()+dt1.toTurtleString()+dt2.toTurtleString()+dt3.toTurtleString()
            +ax1.toTurtleString()+ax2.toTurtleString()+ax3.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(ax1));
        assertTrue(consumer.getAxioms().remove(ax2));
        assertTrue(consumer.getAxioms().remove(ax3));
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().remove(dt1));
        assertTrue(consumer.getAxioms().remove(dt2));
        assertTrue(consumer.getAxioms().remove(dt3));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testTransitiveObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=TransitiveObjectProperty.create(OPV("op1"));
        Axiom ax2=TransitiveObjectProperty.create(IOP(OPV("op2")));
        Axiom ax3=TransitiveObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=TransitiveObjectProperty.create(IOP(OPV("op4")), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=TransitiveObjectProperty.create(IOP(OPV("op5")), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=TransitiveObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAsymmetricObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=AsymmetricObjectProperty.create(OPV("op1"));
        Axiom ax2=AsymmetricObjectProperty.create(IOP(OPV("op2")));
        Axiom ax3=AsymmetricObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=AsymmetricObjectProperty.create(IOP(OPV("op4")), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=AsymmetricObjectProperty.create(IOP(OPV("op5")), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=AsymmetricObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSymmetricObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=SymmetricObjectProperty.create(OPV("op1"));
        Axiom ax2=SymmetricObjectProperty.create(IOPV("op2"));
        Axiom ax3=SymmetricObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=SymmetricObjectProperty.create(IOPV("op4"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=SymmetricObjectProperty.create(IOPV("op5"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=SymmetricObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testIrreflexiveObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=IrreflexiveObjectProperty.create(OPV("op1"));
        Axiom ax2=IrreflexiveObjectProperty.create(IOPV("op2"));
        Axiom ax3=IrreflexiveObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=IrreflexiveObjectProperty.create(IOPV("op4"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=IrreflexiveObjectProperty.create(IOPV("op5"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=IrreflexiveObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testReflexiveObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=ReflexiveObjectProperty.create(OPV("op1"));
        Axiom ax2=ReflexiveObjectProperty.create(IOPV("op2"));
        Axiom ax3=ReflexiveObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=ReflexiveObjectProperty.create(IOPV("op4"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=ReflexiveObjectProperty.create(IOPV("op5"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=ReflexiveObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testInverseFunctionalObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=InverseFunctionalObjectProperty.create(OPV("op1"));
        Axiom ax2=InverseFunctionalObjectProperty.create(IOPV("op2"));
        Axiom ax3=InverseFunctionalObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=InverseFunctionalObjectProperty.create(IOPV("op4"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=InverseFunctionalObjectProperty.create(IOPV("op5"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=InverseFunctionalObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testFunctionalDataProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Declaration dp4=Declaration.create(DPV("dp4"));
        Axiom ax1=FunctionalDataProperty.create(DPV("dp1"));
        Axiom ax2=FunctionalDataProperty.create(DPV("dp2"), ANN(APV("label"), V("Anno"))); 
        Axiom ax3=FunctionalDataProperty.create(DPV("dp3"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax4=FunctionalDataProperty.create(DPV("dp4"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()+dp4.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testFunctionalObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration op4=Declaration.create(OPV("op4"));
        Declaration op5=Declaration.create(OPV("op5"));
        Declaration op6=Declaration.create(OPV("op6"));
        Axiom ax1=FunctionalObjectProperty.create(OPV("op1"));
        Axiom ax2=FunctionalObjectProperty.create(IOPV("op2"));
        Axiom ax3=FunctionalObjectProperty.create(OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax4=FunctionalObjectProperty.create(IOPV("op4"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax5=FunctionalObjectProperty.create(IOPV("op5"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax6=FunctionalObjectProperty.create(OPV("op6"),  
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()+op4.toTurtleString()+op5.toTurtleString()+op6.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testInvObjPropOf() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Axiom sop1=InverseObjectProperties.create(OPV("op1"), OPV("op2"));
        Axiom sop2=InverseObjectProperties.create(IOPV("op2"), OPV("op3"));
        Axiom sop2a=InverseObjectProperties.create(OPV("op2"), IOPV("op3"));
        Axiom sop3=InverseObjectProperties.create(OPV("op1"), OPV("op3"), ANN(APV("label"), V("Anno"))); 
        
        Axiom sop4=InverseObjectProperties.create(IOPV("op1"), OPV("op2"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom sop4a=InverseObjectProperties.create(OPV("op1"), IOPV("op2"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        
        Axiom sop5=InverseObjectProperties.create(IOPV("op1"), IOPV("op2"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        
        Axiom sop6=InverseObjectProperties.create(OPV("op2"), IOPV("op3"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom sop6a=InverseObjectProperties.create(IOPV("op2"), OPV("op3"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
            +sop1.toTurtleString()+sop2.toTurtleString()+sop3.toTurtleString()
            +sop4.toTurtleString()+sop5.toTurtleString()
            +sop6.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(str));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(sop1));
        assertTrue(consumer.getAxioms().remove(sop2) || consumer.getAxioms().remove(sop2a));
        assertTrue(consumer.getAxioms().remove(sop3));
        assertTrue(consumer.getAxioms().remove(sop4) || consumer.getAxioms().remove(sop4a));
        assertTrue(consumer.getAxioms().remove(sop5));
        assertTrue(consumer.getAxioms().remove(sop6) || consumer.getAxioms().remove(sop6a));
        assertTrue(consumer.getAxioms().remove(op1));
        assertTrue(consumer.getAxioms().remove(op2));
        assertTrue(consumer.getAxioms().remove(op3));
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivObjProp() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Axiom sop1=EquivalentObjectProperties.create(OPV("op1"), OPV("op2"));
        Axiom sop2=EquivalentObjectProperties.create(IOPV("op2"), OPV("op3"));
        Axiom sop3=EquivalentObjectProperties.create(OPV("op1"), OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom sop4=EquivalentObjectProperties.create(IOPV("op1"), OPV("op2"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom sop5=EquivalentObjectProperties.create(IOPV("op1"), IOPV("op2"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom sop6=EquivalentObjectProperties.create(OPV("op2"), IOPV("op3"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubChainObjPropOf() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Axiom sop1=SubObjectPropertyOf.create(ObjectPropertyChain.create(OPV("op1"), OPV("op1")), OPV("op2"));
        Axiom sop2=SubObjectPropertyOf.create(ObjectPropertyChain.create(OPV("op3"), OPV("op3"), OPV("op1"), IOPV("op2"), IOPV("op3")), OPV("op3"));
        Axiom sop3=SubObjectPropertyOf.create(ObjectPropertyChain.create(IOPV("op1"), OPV("op1")), OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom sop4=SubObjectPropertyOf.create(ObjectPropertyChain.create(OPV("op1"), IOPV("op1"), OPV("op3")), OPV("op2"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom sop5=SubObjectPropertyOf.create(ObjectPropertyChain.create(OPV("op3"), OPV("op3"), IOPV("op3")), IOPV("op2"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom sop6=SubObjectPropertyOf.create(ObjectPropertyChain.create(OPV("op1"), OPV("op2"), OPV("op2")), IOPV("op3"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubObjPropOf() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Axiom sop1=SubObjectPropertyOf.create(OPV("op1"), OPV("op2"));
        Axiom sop2=SubObjectPropertyOf.create(IOPV("op2"), OPV("op3"));
        Axiom sop3=SubObjectPropertyOf.create(OPV("op1"), OPV("op3"), ANN(APV("label"), V("Anno"))); 
        Axiom sop4=SubObjectPropertyOf.create(IOPV("op1"), OPV("op2"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom sop5=SubObjectPropertyOf.create(IOPV("op1"), IOPV("op2"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom sop6=SubObjectPropertyOf.create(OPV("op2"), IOPV("op3"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubDataPropOf() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Axiom ax1=SubDataPropertyOf.create(DPV("dp1"), DPV("dp2"));
        Axiom ax2=SubDataPropertyOf.create(DPV("dp1"), DPV("dp3"), ANN(APV("label"), V("Anno"))); 
        Axiom ax3=SubDataPropertyOf.create(DPV("dp2"), DPV("dp1"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom ax4=SubDataPropertyOf.create(DPV("dp2"), DPV("dp2"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom ax5=SubDataPropertyOf.create(DPV("dp3"), DPV("dp2"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointUnionOfClasses() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c1=Declaration.create(CV("C1"));
        Declaration d1=Declaration.create(CV("D1"));
        Declaration e1=Declaration.create(CV("E1"));
        Declaration f1=Declaration.create(CV("F1"));
        Declaration op1=Declaration.create(OPV("op1"));
        Axiom ax1a=DisjointUnion.create(CV("C1"), ObjectSomeValuesFrom.create(OPV("op1"), ObjectIntersectionOf.create(CV("D1"), CV("E1"))), CV("F1"));
        Axiom ax1b=DisjointUnion.create(CV("C1"), CV("E1"), ObjectSomeValuesFrom.create(OPV("op1"), ObjectIntersectionOf.create(CV("D1"), CV("E1"))), CV("F1"));
        Annotation ann=ANN(APV("label"), V("anno"));
        Annotation annAnn=ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno")));
        Set<ClassExpression> classes=new HashSet<ClassExpression>();
        classes.add(ObjectSomeValuesFrom.create(OPV("op1"), ObjectIntersectionOf.create(CV("D1"), CV("E1"))));
        classes.add(CV("F1"));
        Axiom ax2a=DisjointUnion.create(CV("C1"), classes, Collections.singleton(ann));
        Axiom ax2b=DisjointUnion.create(CV("E1"), classes, Collections.singleton(ann));
        Axiom ax3a=DisjointUnion.create(CV("C1"), classes, Collections.singleton(annAnn));
        Axiom ax3b=DisjointUnion.create(CV("E1"), classes, Collections.singleton(annAnn));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c1.toTurtleString()+d1.toTurtleString()+e1.toTurtleString()+f1.toTurtleString()+op1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testHasKey2() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        ClassExpression cAndD=ObjectIntersectionOf.create(CV("C"), CV("D"));
        Declaration op=Declaration.create(OPV("op"));
        Declaration dp=Declaration.create(DPV("dp"));
        Axiom hasKey=HasKey.create(cAndD, OPV("op"), DPV("dp"));
        Set<PropertyExpression> props=new HashSet<PropertyExpression>();
        props.add(DPV("dp"));
        Axiom hasKeyAnn=HasKey.create((ClassExpression)ObjectUnionOf.create(CV("C"), CV("D")),props,Collections.singleton(ANN(APV("label"), V("CorDAnno"))));
        props=new HashSet<PropertyExpression>();
        props.add(DPV("dp"));
        props.add(OPV("op"));
        Axiom hasKeyAnnAnn=HasKey.create((ClassExpression)ObjectComplementOf.create(CV("C")),props,
                Collections.singleton(ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno")))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+op.toTurtleString()+dp.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testHasKey() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration op=Declaration.create(OPV("op"));
        Axiom hasKey=HasKey.create(CV("C"), OPV("op"));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+op.toTurtleString()
            +hasKey.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(hasKey));
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyRangeComplex() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(DT("C"));
        Declaration d=Declaration.create(DT("D"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration op=Declaration.create(OPV("op"));
        Axiom sco=DataPropertyRange.create(DPV("dp1"), DataIntersectionOf.create(DT("C"),DT("D")));
        Declaration e=Declaration.create(DT("E"));
        Declaration f=Declaration.create(DT("F"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Axiom scoa=DataPropertyRange.create(DPV("dp2"), 
                DataUnionOf.create(DT("C"), DataIntersectionOf.create(DT("E"),DT("F"))),
                ANN(APV("label"), V("EFanno")));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Axiom scoaa=DataPropertyRange.create(DPV("dp3"),
                DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT))),
                ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyRange() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration c1=Declaration.create(DT("C1"));
        Axiom sco=DataPropertyRange.create(DPV("dp1"),DT("C1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration c2=Declaration.create(DT("C2"));
        Axiom scoa=DataPropertyRange.create(DPV("dp2"),DT("C2"),ANN(APV("label"), V("EFanno")));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Declaration c3=Declaration.create(DT("C3"));
        Axiom scoaa=DataPropertyRange.create(DPV("dp3"),DT("C3"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+c1.toTurtleString()+dp2.toTurtleString()+c2.toTurtleString()+dp3.toTurtleString()+c3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyRangeComplex() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Declaration r=Declaration.create(OPV("r"));
        Axiom sco=ObjectPropertyRange.create(IOPV("r"), ObjectSomeValuesFrom.create(IOPV("r"), ObjectComplementOf.create(CV("C"))));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Declaration s=Declaration.create(OPV("s"));
        Axiom scoa=ObjectPropertyRange.create(OPV("s"), 
                ObjectAllValuesFrom.create(IOPV("s"), ObjectIntersectionOf.create(CV("C"), ObjectIntersectionOf.create(CV("E"),CV("F")))),
                ANN(APV("label"), V("EFanno")));
        Declaration t=Declaration.create(DPV("t"));
        Axiom scoaa=ObjectPropertyRange.create(OPV("r"),
                DataExactCardinality.create(5, DPV("t"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyRange() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration c1=Declaration.create(CV("C1"));
        Axiom sco=ObjectPropertyRange.create(OPV("op1"),CV("C1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration c2=Declaration.create(CV("C2"));
        Axiom scoa=ObjectPropertyRange.create(IOPV("op1"),CV("C2"),ANN(APV("label"), V("EFanno")));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration c3=Declaration.create(CV("C3"));
        Axiom scoaa=ObjectPropertyRange.create(OPV("op3"),CV("C3"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+c1.toTurtleString()+op2.toTurtleString()+c2.toTurtleString()+op3.toTurtleString()+c3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    
    public void testDataPropertyDomainComplex() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration op=Declaration.create(OPV("op"));
        Axiom sco=DataPropertyDomain.create(DPV("dp1"), ObjectSomeValuesFrom.create(IOPV("op"), ObjectIntersectionOf.create(CV("C"),CV("D"))));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Axiom scoa=DataPropertyDomain.create(DPV("dp2"), 
                ObjectAllValuesFrom.create(OPV("op"), ObjectUnionOf.create(CV("C"), ObjectIntersectionOf.create(CV("E"),CV("F")))),
                ANN(APV("label"), V("EFanno")));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Axiom scoaa=DataPropertyDomain.create(DPV("dp3"),
                DataMaxCardinality.create(5, DPV("dp1"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyDomain() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration c1=Declaration.create(CV("C1"));
        Axiom sco=DataPropertyDomain.create(DPV("dp1"),CV("C1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration c2=Declaration.create(CV("C2"));
        Axiom scoa=DataPropertyDomain.create(DPV("dp2"),CV("C2"),ANN(APV("label"), V("EFanno")));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Declaration c3=Declaration.create(CV("C3"));
        Axiom scoaa=DataPropertyDomain.create(DPV("dp3"),CV("C3"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+c1.toTurtleString()+dp2.toTurtleString()+c2.toTurtleString()+dp3.toTurtleString()+c3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyDomainComplex() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Declaration r=Declaration.create(OPV("r"));
        Axiom sco=ObjectPropertyDomain.create(IOPV("r"), ObjectSomeValuesFrom.create(IOPV("r"), ObjectIntersectionOf.create(CV("C"),CV("D"))));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Declaration s=Declaration.create(OPV("s"));
        Axiom scoa=ObjectPropertyDomain.create(OPV("s"), 
                ObjectAllValuesFrom.create(IOPV("s"), ObjectUnionOf.create(CV("C"), ObjectIntersectionOf.create(CV("E"),CV("F")))),
                ANN(APV("label"), V("EFanno")));
        Declaration t=Declaration.create(DPV("t"));
        Axiom scoaa=ObjectPropertyDomain.create(OPV("r"),
                DataMaxCardinality.create(5, DPV("t"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyDomain() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration c1=Declaration.create(CV("C1"));
        Axiom sco=ObjectPropertyDomain.create(OPV("op1"),CV("C1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration c2=Declaration.create(CV("C2"));
        Axiom scoa=ObjectPropertyDomain.create(IOPV("op1"),CV("C2"),ANN(APV("label"), V("EFanno")));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration c3=Declaration.create(CV("C3"));
        Axiom scoaa=ObjectPropertyDomain.create(OPV("op3"),CV("C3"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+c1.toTurtleString()+op2.toTurtleString()+c2.toTurtleString()+op3.toTurtleString()+c3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointWithObjectProperty() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Axiom spo=DisjointObjectProperties.create(IOPV("op1"), OPV("op2"));
        Axiom spoa=DisjointObjectProperties.create(OPV("op1"),OPV("op3"),ANN(APV("label"), V("opanno")));
        Axiom spoaa=DisjointObjectProperties.create(OPV("op2"),IOPV("op3"),ANN(APV("comment"), V("opAanno"), ANN(APV("label"), V("opannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointWithDataProperty() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Axiom spo=DisjointDataProperties.create(DPV("dp1"), DPV("dp2"));
        Axiom spoa=DisjointDataProperties.create(DPV("dp1"),DPV("dp3"),ANN(APV("label"), V("dpanno")));
        Axiom spoaa=DisjointDataProperties.create(DPV("dp2"),DPV("dp3"),ANN(APV("comment"), V("dpAanno"), ANN(APV("label"), V("dpannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentDataProperties() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Axiom spo=EquivalentDataProperties.create(DPV("dp1"), DPV("dp2"));
        Axiom spoa=EquivalentDataProperties.create(DPV("dp1"),DPV("dp3"),ANN(APV("label"), V("dpanno")));
        Axiom spoaa=EquivalentDataProperties.create(DPV("dp2"),DPV("dp3"),ANN(APV("comment"), V("dpAanno"), ANN(APV("label"), V("dpannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+dp2.toTurtleString()+dp3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentObjectProperties() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration op3=Declaration.create(OPV("op3"));
        Axiom spo=EquivalentObjectProperties.create(IOPV("op1"), OPV("op2"));
        Axiom spoa=EquivalentObjectProperties.create(OPV("op1"),OPV("op3"),ANN(APV("label"), V("opanno")));
        Axiom spoaa=EquivalentObjectProperties.create(OPV("op2"),IOPV("op3"),ANN(APV("comment"), V("opAanno"), ANN(APV("label"), V("opannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+op2.toTurtleString()+op3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentClassComplex() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Declaration r=Declaration.create(OPV("r"));
        Axiom sco=EquivalentClasses.create(ObjectSomeValuesFrom.create(IOPV("r"), CV("C")),ObjectIntersectionOf.create(CV("C"),CV("D")));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Declaration s=Declaration.create(OPV("s"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(ObjectSomeValuesFrom.create(OPV("s"), ObjectComplementOf.create(ObjectIntersectionOf.create(CV("E"),CV("F")))));
        ces.add(ObjectMinCardinality.create(5, IOPV("r")));
        Axiom scoa=EquivalentClasses.create(ces,ANN(APV("label"), V("EFanno")));
        Declaration t=Declaration.create(DPV("t"));
        Axiom scoaa=EquivalentClasses.create(
                SET(DataAllValuesFrom.create(DPV("t"), DataIntersectionOf.create(DT("xsd:string"), DataComplementOf.create(DT("rdf:PlainLiteral")))),
                    DataExactCardinality.create(1000, DPV("t"), DatatypeRestriction.create(DT("xsd:decimal"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_INTEGER))))),
                ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testEquivalentClasses() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Axiom sco=EquivalentClasses.create(CV("C"),CV("D"));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Axiom scoa=EquivalentClasses.create(SET(CV("E"),CV("F")),ANN(APV("label"), V("EFanno")));
        Declaration g=Declaration.create(CV("G"));
        Declaration h=Declaration.create(CV("H"));
        Axiom scoaa=EquivalentClasses.create(SET(CV("G"), CV("H")),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()+g.toTurtleString()+h.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubClassOfComplex() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Declaration r=Declaration.create(OPV("r"));
        Axiom sco=SubClassOf.create(ObjectSomeValuesFrom.create(IOPV("r"), CV("C")),ObjectIntersectionOf.create(CV("C"),CV("D")));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Declaration s=Declaration.create(OPV("s"));
        Axiom scoa=SubClassOf.create(ObjectAllValuesFrom.create(OPV("s"), ObjectUnionOf.create(CV("C"), ObjectIntersectionOf.create(CV("E"),CV("F")))), ObjectMinCardinality.create(5, OPV("r"), ObjectIntersectionOf.create(CV("E"),CV("F"))),ANN(APV("label"), V("EFanno")));
        Declaration t=Declaration.create(DPV("t"));
        Axiom scoaa=SubClassOf.create(
                DataSomeValuesFrom.create(DPV("t"), DataUnionOf.create(DT("xsd:string"), DT("rdf:PlainLiteral"))), 
                DataMaxCardinality.create(5, DPV("t"), DatatypeRestriction.create(DT("xsd:int"), 
                        FacetRestriction.create(OWL2_FACET.MAX_EXCLUSIVE, TL("23", null, Datatype.XSD_BYTE)), 
                        FacetRestriction.create(OWL2_FACET.MIN_INCLUSIVE, TL("21", null, Datatype.XSD_SHORT)))),
                ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubClassOf() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Axiom sco=SubClassOf.create(CV("C"),CV("D"));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Axiom scoa=SubClassOf.create(CV("E"),CV("F"),ANN(APV("label"), V("EFanno")));
        Declaration g=Declaration.create(CV("G"));
        Declaration h=Declaration.create(CV("H"));
        Axiom scoaa=SubClassOf.create(CV("G"),CV("H"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()+g.toTurtleString()+h.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testNegativeDataPropertyAssertion() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration a1=Declaration.create(NI("a1"));
        Declaration r1=Declaration.create(DPV("r1"));
        Axiom negDP=NegativeDataPropertyAssertion.create(DPV("r1"),NI("a1"),LV("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration r2=Declaration.create(DPV("r2"));
        Axiom negDPa=NegativeDataPropertyAssertion.create(DPV("r2"),NI("a2"),LV("b2"),ANN(APV("label"), V("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration r3=Declaration.create(DPV("r3"));
        Axiom negDPaa=NegativeDataPropertyAssertion.create(DPV("r3"),NI("a3"),LV("b3"),ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+a1.toTurtleString()+r1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testNegativeObjectPropertyAssertion() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Declaration r1=Declaration.create(OPV("r1"));
        Axiom negOP=NegativeObjectPropertyAssertion.create(OPV("r1"),NI("a1"),NI("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Declaration r2=Declaration.create(OPV("r2"));
        Axiom negOPa=NegativeObjectPropertyAssertion.create(OPV("r2"),NI("a2"),NI("b2"),ANN(APV("label"), V("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Declaration r3=Declaration.create(OPV("r3"));
        Axiom negOPaa=NegativeObjectPropertyAssertion.create(OPV("r3"),NI("a3"),NI("b3"),ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+a1.toTurtleString()+b1.toTurtleString()+r1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testClassAssertion() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c1=Declaration.create(CV("C1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom ax1=ClassAssertion.create(CV("C1"),NI("b1"));
        Declaration c2=Declaration.create(CV("C2"));
        Declaration b2=Declaration.create(NI("b2"));
        Axiom ax2=ClassAssertion.create(CV("C2"),NI("b2"), ANN(APV("label"), V("rst2anno")));
        Declaration c3=Declaration.create(CV("C3"));
        Declaration b3=Declaration.create(NI("b3"));
        Axiom ax3=ClassAssertion.create(CV("C3"),NI("b3"), ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c1.toTurtleString()+b1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testObjectPropertyAssertion() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration op1=Declaration.create(OPV("op1"));
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom ax1=ObjectPropertyAssertion.create(OPV("op1"),NI("a1"),NI("b1"));
        Declaration op2=Declaration.create(OPV("op2"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Axiom ax2=ObjectPropertyAssertion.create(OPV("op2"),NI("a2"),NI("b2"),ANN(APV("label"), V("rst2anno")));
        Declaration op3=Declaration.create(OPV("op3"));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Axiom ax3=ObjectPropertyAssertion.create(OPV("op3"),NI("a3"),NI("b3"),ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+op1.toTurtleString()+a1.toTurtleString()+b1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDataPropertyAssertion() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration dp1=Declaration.create(DPV("dp1"));
        Declaration a1=Declaration.create(NI("a1"));
        Axiom ax1=DataPropertyAssertion.create(DPV("dp1"),NI("a1"),LV("b1"));
        Declaration dp2=Declaration.create(DPV("dp2"));
        Declaration a2=Declaration.create(NI("a2"));
        Axiom ax2=DataPropertyAssertion.create(DPV("dp2"),NI("a2"),LV("b2"),ANN(APV("label"), V("rst2anno")));
        Declaration dp3=Declaration.create(DPV("dp3"));
        Declaration a3=Declaration.create(NI("a3"));
        Axiom ax3=DataPropertyAssertion.create(DPV("dp3"),NI("a3"),LV("b3"),ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+dp1.toTurtleString()+a1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSameAs() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom ax1=SameIndividual.create(NI("a1"),NI("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        Axiom ax2=SameIndividual.create(inds, ANN(APV("label"), V("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        Axiom ax3=SameIndividual.create(inds2, ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+a1.toTurtleString()+b1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDifferentFrom() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration a1=Declaration.create(NI("a1"));
        Declaration b1=Declaration.create(NI("b1"));
        Axiom diffInds=DifferentIndividuals.create(NI("a1"),NI("b1"));
        Declaration a2=Declaration.create(NI("a2"));
        Declaration b2=Declaration.create(NI("b2"));
        Set<Individual> inds=new HashSet<Individual>();
        inds.add(NI("a2"));
        inds.add(NI("b2"));
        Axiom diffIndsa=DifferentIndividuals.create(inds, ANN(APV("label"), V("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        Axiom diffIndsaa=DifferentIndividuals.create(inds2, ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+a1.toTurtleString()+b1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDifferent() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
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
        Axiom diffIndsa=DifferentIndividuals.create(inds, ANN(APV("label"), V("rst2anno")));
        Declaration a3=Declaration.create(NI("a3"));
        Declaration b3=Declaration.create(NI("b3"));
        Declaration c3=Declaration.create(NI("c3"));
        Set<Individual> inds2=new HashSet<Individual>();
        inds2.add(NI("a3"));
        inds2.add(NI("b3"));
        inds2.add(NI("c3"));
        Axiom diffIndsaa=DifferentIndividuals.create(inds2, ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+a1.toTurtleString()+b1.toTurtleString()+c1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDisjointDataProperties() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration r1=Declaration.create(DPV("r1"));
        Declaration s1=Declaration.create(DPV("s1"));
        Declaration t1=Declaration.create(DPV("t1"));
        Axiom djop=DisjointDataProperties.create(SET(DPV("r1"),DPV("s1"),DPV("t1")));
        Declaration r2=Declaration.create(DPV("r2"));
        Declaration s2=Declaration.create(DPV("s2"));
        Declaration t2=Declaration.create(DPV("t2"));
        Axiom djopa=DisjointDataProperties.create(SET(DPV("r2"), DPV("s2"), DPV("t2")), SET(ANN(APV("label"), V("rst2anno"))));
        Declaration r3=Declaration.create(DPV("r3"));
        Declaration s3=Declaration.create(DPV("s3"));
        Declaration t3=Declaration.create(DPV("t3"));
        Axiom djopaa=DisjointDataProperties.create(SET(DPV("r3"), DPV("s3"), DPV("t3")), SET(ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno")))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+r1.toTurtleString()+s1.toTurtleString()+t1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDisjointObjectProperties() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration r1=Declaration.create(OPV("r1"));
        Declaration s1=Declaration.create(OPV("s1"));
        Declaration t1=Declaration.create(OPV("t1"));
        Axiom djop=DisjointObjectProperties.create(SET(OPV("r1"),OPV("s1"),OPV("t1")));
        Declaration r2=Declaration.create(OPV("r2"));
        Declaration s2=Declaration.create(OPV("s2"));
        Declaration t2=Declaration.create(OPV("t2"));
        Axiom djopa=DisjointObjectProperties.create(SET(OPV("r2"), OPV("s2"), OPV("t2")), SET(ANN(APV("label"), V("rst2anno"))));
        Declaration r3=Declaration.create(OPV("r3"));
        Declaration s3=Declaration.create(OPV("s3"));
        Declaration t3=Declaration.create(OPV("t3"));
        Axiom djopaa=DisjointObjectProperties.create(SET(OPV("r3"), OPV("s3"), OPV("t3")), SET(ANN(APV("comment"), V("rst3Aanno"), ANN(APV("label"), V("rs3annoAnno")))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+r1.toTurtleString()+s1.toTurtleString()+t1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDisjointWithClasses() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration d=Declaration.create(CV("D"));
        Axiom dc=DisjointClasses.create(CV("C"),CV("D"));
        Declaration e=Declaration.create(CV("E"));
        Declaration f=Declaration.create(CV("F"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(CV("E"));
        ces.add(CV("F"));
        Axiom dca=DisjointClasses.create(ces, ANN(APV("label"), V("EFanno")));
        Declaration g=Declaration.create(CV("G"));
        Declaration h=Declaration.create(CV("H"));
        ces=new HashSet<ClassExpression>();
        ces.add(CV("G"));
        ces.add(CV("H"));
        Axiom dcaa=DisjointClasses.create(ces, ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+d.toTurtleString()+e.toTurtleString()+f.toTurtleString()+g.toTurtleString()+h.toTurtleString()+dc.toTurtleString()+dca.toTurtleString()+dcaa.toTurtleString();
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAllDisjointClasses() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c1=Declaration.create(CV("C1"));
        Declaration d1=Declaration.create(CV("D1"));
        Declaration e1=Declaration.create(CV("E1"));
        Axiom dc=DisjointClasses.create(CV("C1"),CV("D1"),CV("E1"));
        Declaration c2=Declaration.create(CV("C2"));
        Declaration d2=Declaration.create(CV("D2"));
        Declaration e2=Declaration.create(CV("E2"));
        Set<ClassExpression> ces=new HashSet<ClassExpression>();
        ces.add(CV("C2"));
        ces.add(CV("D2"));
        ces.add(CV("E2"));
        Axiom dca=DisjointClasses.create(ces, ANN(APV("label"), V("CDE2anno")));
        Declaration c3=Declaration.create(CV("C3"));
        Declaration d3=Declaration.create(CV("D3"));
        Declaration e3=Declaration.create(CV("E3"));
        Set<ClassExpression> ces2=new HashSet<ClassExpression>();
        ces2.add(CV("C3"));
        ces2.add(CV("D3"));
        ces2.add(CV("E3"));
        Axiom dcaa=DisjointClasses.create(ces2, ANN(APV("comment"), V("CDE3Aanno"), ANN(APV("label"), V("CDE3annoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c1.toTurtleString()+d1.toTurtleString()+e1.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDeclarationsWithAnnotationAnnotations() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Annotation caa=ANN(AP("ap"), V("caa"));
        Annotation ca=ANN(AP("ap"), V("ca"),caa);
        Annotation opaa=ANN(AP("ap"), V("opaa"));
        Annotation opa=ANN(AP("ap"), V("opa"),opaa);
        Annotation dpaa=ANN(AP("ap"), V("dpaa"));
        Annotation dpa=ANN(AP("ap"), V("dpa"),dpaa);
        Annotation apaa=ANN(AP("ap"), V("apaa"));
        Annotation apa=ANN(AP("ap"), V("apa"),apaa);
        Annotation niaa=ANN(AP("ap"), V("niaa"));
        Annotation nia=ANN(AP("ap"), V("nia"),niaa);
        Annotation dtaa=ANN(AP("ap"), V("dtaa"));
        Annotation dta=ANN(AP("ap"), V("dta"),dtaa);
        Declaration c=Declaration.create(CV("C"), ca);
        Declaration op=Declaration.create(OPV("op"), opa);
        Declaration dp=Declaration.create(DPV("dp"), dpa);
        Declaration ap=Declaration.create(AP("ap"), apa);
        Declaration ni=Declaration.create(NI("ni"), nia);
        Declaration dt=Declaration.create(DT("dt"), dta);
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().remove(ap));
        assertTrue(consumer.getAxioms().remove(ni));
        assertTrue(consumer.getAxioms().remove(dt));
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDeclarationsWithAnnotations() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Annotation ca=ANN(AP("ap"), V("ca"));
        Annotation opa=ANN(AP("ap"), V("opa"));
        Annotation dpa=ANN(AP("ap"), V("dpa"));
        Annotation apa=ANN(AP("ap"), V("apa"));
        Annotation nia=ANN(AP("ap"), V("nia"));
        Annotation dta=ANN(AP("ap"), V("dta"));
        Declaration c=Declaration.create(CV("C"), ca);
        Declaration op=Declaration.create(OPV("op"), opa);
        Declaration dp=Declaration.create(DPV("dp"), dpa);
        Declaration ap=Declaration.create(AP("ap"), apa);
        Declaration ni=Declaration.create(NI("ni"), nia);
        Declaration dt=Declaration.create(DT("dt"), dta);
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().remove(ap));
        assertTrue(consumer.getAxioms().remove(ni));
        assertTrue(consumer.getAxioms().remove(dt));
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testDeclarations() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration c=Declaration.create(CV("C"));
        Declaration op=Declaration.create(OPV("op"));
        Declaration dp=Declaration.create(DPV("dp"));
        Declaration ap=Declaration.create(APV("ap"));
        Declaration ni=Declaration.create(NI("ni"));
        Declaration dt=Declaration.create(DTV("dt"));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+c.toTurtleString()+op.toTurtleString()+dp.toTurtleString()+ap.toTurtleString()+ni.toTurtleString()+dt.toTurtleString();
        OWLBGPParser parser=new OWLBGPParser(new StringReader(s));
        TripleConsumer consumer=parser.handler;
        parser.parse();
        assertTrue(consumer.getAxioms().remove(c));
        assertTrue(consumer.getAxioms().remove(op));
        assertTrue(consumer.getAxioms().remove(dp));
        assertTrue(consumer.getAxioms().remove(ap));
        assertTrue(consumer.getAxioms().remove(ni));
        assertTrue(consumer.getAxioms().remove(dt));
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testSubAnnotationPropertyOf() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration ap1=Declaration.create(APV("ap1"));
        Declaration ap2=Declaration.create(APV("ap2"));
        Declaration ap3=Declaration.create(APV("ap3"));
        Axiom sap1=SubAnnotationPropertyOf.create(APV("ap1"), APV("ap2"));
        Axiom sap3=SubAnnotationPropertyOf.create(APV("ap1"), APV("ap3"), ANN(APV("label"), V("Anno"))); 
        Axiom sap4=SubAnnotationPropertyOf.create(APV("ap2"), APV("ap2"), ANN(APV("label"), V("Anno")), ANN(APV("comment"), V("anno2")));
        Axiom sap5=SubAnnotationPropertyOf.create(APV("ap3"), APV("ap2"), ANN(APV("label"), V("Anno"), ANN(APV("label"), V("AnnoAnno"))));
        Axiom sap6=SubAnnotationPropertyOf.create(APV("ap2"), APV("ap3"), 
                ANN(APV("label"), V("Anno")), 
                ANN(APV("comment"), V("anno2"), ANN(APV("label"), V("AnnoAnno"))));
        String str=apv1.toTurtleString()+apv2.toTurtleString()+ap1.toTurtleString()+ap2.toTurtleString()+ap3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAnnotationPropertyDomain() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration ap1=Declaration.create(APV("ap1"));
        Axiom sco=AnnotationPropertyDomain.create(APV("ap1"),IRI("iri1"));
        Declaration ap2=Declaration.create(APV("ap2"));
        Axiom scoa=AnnotationPropertyDomain.create(APV("ap1"),IRI("iri2"),ANN(APV("label"), V("EFanno")));
        Declaration ap3=Declaration.create(APV("ap3"));
        Axiom scoaa=AnnotationPropertyDomain.create(APV("ap3"),IRI("iri3"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+ap1.toTurtleString()+ap2.toTurtleString()+ap3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
    public void testAnnotationPropertyRange() throws Exception {
        Declaration apv1=Declaration.create(APV("label"));
        Declaration apv2=Declaration.create(APV("comment"));
        Declaration ap1=Declaration.create(APV("ap1"));
        Axiom sco=AnnotationPropertyRange.create(APV("ap1"),IRI("iri1"));
        Declaration ap2=Declaration.create(APV("ap2"));
        Axiom scoa=AnnotationPropertyRange.create(APV("ap1"),IRI("iri2"),ANN(APV("label"), V("EFanno")));
        Declaration ap3=Declaration.create(APV("ap3"));
        Axiom scoaa=AnnotationPropertyRange.create(APV("ap3"),IRI("iri3"),ANN(APV("comment"), V("GHAanno"), ANN(APV("label"), V("GHannoAnno"))));
        String s=apv1.toTurtleString()+apv2.toTurtleString()+ap1.toTurtleString()+ap2.toTurtleString()+ap3.toTurtleString()
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
        assertTrue(consumer.getAxioms().remove(apv1));
        assertTrue(consumer.getAxioms().remove(apv2));
        assertTrue(consumer.getAxioms().isEmpty());
        assertNoTriplesLeft(consumer);
    }
}