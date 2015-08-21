/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of the OWL-BGP project.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.semanticweb.sparql.bgpevaluation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_AsymmetricObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ClassAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DataPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DataPropertyDomain;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DataPropertyRange;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DifferentIndividuals;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DisjointClasses;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_EquivalentClasses;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_FunctionalDataProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_FunctionalObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_InverseFunctionalObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_InverseObjectProperties;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_IrreflexiveObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_NegativeDataPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyDomain;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyRange;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ReflexiveObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SameIndividual;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SubClassOf;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SubObjectPropertyOf;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SymmetricObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_TransitiveObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.bgpevaluation.queryobjects.StaticQueryObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DifferentIndividuals;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class StaticCostEstimationVisitor implements StaticQueryObjectVisitorEx<double[]> {
    //protected double COST_CONCEPT_ENTAILMENT=100;
    //protected double COST_ROLE_ENTAILMENT=120;
    protected double COST_ENTAILMENT = 100;
    protected double COST_LOOKUP = 1;
    //protected double COST_CLASS_HIERARCHY_INSERTION=10*COST_CONCEPT_ENTAILMENT;
    protected double COST_CLASS_HIERARCHY_INSERTION = 10 * COST_ENTAILMENT;

    protected final OWLReasoner m_reasoner;
    protected final OWLDataFactory m_dataFactory;
    protected final OWLOntologyGraph m_graph;
    protected final int m_classCount;
    protected final int m_opCount;
    protected final int m_dpCount;
    protected final int m_apCount;
    protected final int m_datatypeCount;
    protected final int m_indCount;
    protected final int m_litCount;

    protected List<Atomic[]> m_candidateBindings;

    public StaticCostEstimationVisitor(OWLOntologyGraph graph) {
        m_reasoner = graph.getReasoner();
        m_dataFactory = graph.getOntology().getOWLOntologyManager().getOWLDataFactory();
        m_graph = graph;
        m_classCount = graph.getClassesInSignature().size();
        m_opCount = graph.getObjectPropertiesInSignature().size();
        m_dpCount = graph.getDataPropertiesInSignature().size();
        m_apCount = graph.getAnnotationPropertiesInSignature().size();
        m_datatypeCount = graph.getDatatypesInSignature().size();
        m_indCount = graph.getIndividualsInSignature().size();
        m_litCount = graph.getLiteralsInSignature().size();
//        System.out.println("HermiT did "+countingMonitor.getOverallNumberOfTests()+" tests. ");
//	    System.out.println("This took "+countingMonitor.getOverallTime()+" ms. ");
//	    System.out.println("The last test took "+countingMonitor.getTime()+" ms. ");
//	    System.out.println("The last model contained "+countingMonitor.getNumberOfNodes()+" nodes/individuals. ");
//       System.out.println("The 2 hardest satisfiability tests were:");
    }

    public double[] visit(QO_SubClassOf queryObject, Set<Variable> boundVar) {
        SubClassOf template = (SubClassOf) queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        ClassExpression subClass = template.getSubClassExpression();
        ClassExpression superClass = template.getSuperClassExpression();
        int results = 1;

        if ((subClass instanceof Atomic || subClass.isVariable()) && (superClass instanceof Atomic || superClass.isVariable())) {
            for (int i = 0; i < unbound.size(); i++)
                results *= m_classCount;
            return new double[]{results * COST_LOOKUP, results};
        }
        else return complex(unbound);
    }

    /*    public double[] visit(QO_SubClassOf queryObject, Set<Variable> boundVar) {
            double[] estimate=new double[2];
            SubClassOf axiomTemplate=queryObject.getAxiomTemplate();
            Set<Variable> vars=axiomTemplate.getVariablesInSignature();
            Set<Variable> unbound=vars;
            unbound.removeAll(boundVar);
            ClassExpression subexpression= axiomTemplate.getSubClassExpression();
            ClassExpression superexpression= axiomTemplate.getSuperClassExpression();
            if (((subexpression instanceof Atomic || subexpression.isVariable())) && (superexpression instanceof Atomic || superexpression.isVariable())){
                double[] currentEstimate=getSubClassOfCost(subexpression, superexpression, boundVar);
                estimate[0]+=currentEstimate[0];
                estimate[1]+=currentEstimate[1];
                return estimate;
            }
            else return complex(unbound);
        }
        protected double[] getSubClassOfCost(ClassExpression subce, ClassExpression superce, Set<Variable> boundVar) {
            if (subce.isVariable() && !boundVar.contains(subce)){
                if (superce.isVariable() && !boundVar.contains(superce)) //SubClassOf(?x ?y)
                    return new double[] { m_classCount * m_classCount * COST_LOOKUP, m_classCount * m_classCount};
                else if (superce.isVariable() && boundVar.contains(superce)) //SubClass(?x :C) <- SubClassOf(?x ?y)
                    return new double[] { m_classCount * COST_LOOKUP, m_classCount};
                else //SubClassOf(?x :C)
                    return new double[] { m_classCount * COST_LOOKUP, m_classCount};
            }
            else if (subce.isVariable() && boundVar.contains(subce) ) {
                if (superce.isVariable() && !boundVar.contains(superce)) //SubClassOf(:C ?y) <- SubClassOf(?x ?y)
                    return new double[] { m_classCount * COST_LOOKUP, m_classCount};
                else //SubClass(:C :D) <- SubClassOf(?x ?y)
                    return new double[] { COST_LOOKUP, 1};
            }
            else {
                if (superce.isVariable() && !boundVar.contains(superce)) //SubClassOf(:C ?y)
                    return new double[] { m_classCount * COST_LOOKUP, m_classCount };
                else //SubClassOf(:C :D) <- subClassOf(:C ?y) or SubClassOf(:C :D)
                    return new double[] { COST_LOOKUP, 1};
            }
        }
    */
    public double[] visit(QO_EquivalentClasses queryObject, Set<Variable> boundVar) {
        EquivalentClasses template = (EquivalentClasses) queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        Set<ClassExpression> equivClasses = template.getClassExpressions();
        int exprSize = equivClasses.size();
        Iterator<ClassExpression> classIt = equivClasses.iterator();
        ClassExpression cls1 = classIt.next();
        ClassExpression cls2;
        if (exprSize == 1)
            cls2 = cls1;
        else
            cls2 = classIt.next();
        int results = 1;
        if ((cls1 instanceof Atomic || cls1.isVariable()) && (cls2 instanceof Atomic || cls2.isVariable())) {
            for (int i = 0; i < unbound.size(); i++)
                results *= m_classCount;
            return new double[]{results * COST_LOOKUP, results};
        }
        /*int flag=0;
        while (classIt.hasNext()) {
        	ClassExpression clsi=classIt.next();
        	if (!(clsi instanceof Atomic) && !clsi.isVariable()) {
        		flag=1;
        		break;
        	}
        }
        int results=1;
        if (flag==0){
        	for (int i=0;i<unbound.size();i++)
        		results*=m_classCount;	
            return new double[] { results*COST_LOOKUP, results };
        }*/
        else return complex(unbound);
    }

    public double[] visit(QO_DisjointClasses queryObject, Set<Variable> boundVar) {
        //DisjointClasses template=(DisjointClasses)queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        return complex(unbound);
    }

    //    public double[] visit(QO_DisjointUnion queryObject) {
//        return new double[] { 0, 0 };
//    }
    public double[] visit(QO_SubObjectPropertyOf queryObject, Set<Variable> boundVar) {
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = template.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        int results = 1;
        for (int i = 0; i < unbound.size(); i++)
            results *= m_opCount;
        return new double[]{results * COST_LOOKUP, results};
    }

    //    public double[] visit(QO_EquivalentObjectProperties queryObject) {
//        return new double[] { 0, 0 };
//    }
//    public double[] visit(QO_DisjodointObjectProperties queryObject) {
//        return new double[] { 0, 0 };
//    }
    public double[] visit(QO_InverseObjectProperties queryObject, Set<Variable> boundVar) {
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = template.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        int results = 1;
        for (int i = 0; i < unbound.size(); i++)
            results *= m_opCount;
        return new double[]{results * COST_LOOKUP, results};
    }

    public double[] visit(QO_ObjectPropertyDomain queryObject, Set<Variable> boundVar) {
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = template.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        return complex(unbound);
    }

    public double[] visit(QO_ObjectPropertyRange queryObject, Set<Variable> boundVar) {
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = template.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        return complex(unbound);
    }

    public double[] visit(QO_FunctionalObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    protected double[] getObjectPropertyAxiomCost(QueryObject<? extends Axiom> queryObject, Set<Variable> boundVar) {
        Set<Variable> opeVar = queryObject.getAxiomTemplate().getVariablesInSignature();
        Set<Variable> unbound = opeVar;
        unbound.removeAll(opeVar);
        if (unbound.isEmpty())
            //return new double[] { COST_ROLE_ENTAILMENT, 1 };
            return new double[]{COST_ENTAILMENT, 1};
        else
            //return new double[] { m_opCount*COST_ROLE_ENTAILMENT, m_opCount }; // better return told numbers
            return new double[]{m_opCount * COST_ENTAILMENT, m_opCount}; // better return told numbers
    }

    protected double[] getDataPropertyAxiomCost(QueryObject<? extends Axiom> queryObject, Set<Variable> boundVar) {
        Set<Variable> dpeVar = queryObject.getAxiomTemplate().getVariablesInSignature();
        Set<Variable> unbound = dpeVar;
        unbound.removeAll(dpeVar);
        if (unbound.isEmpty())
            //return new double[] { COST_ROLE_ENTAILMENT, 1 };
            return new double[]{COST_ENTAILMENT, 1};
        else
            //return new double[] { m_dpCount*COST_ROLE_ENTAILMENT, m_dpCount }; // better return told numbers
            return new double[]{m_dpCount * COST_ENTAILMENT, m_dpCount}; // better return told numbers
    }

    public double[] visit(QO_InverseFunctionalObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    public double[] visit(QO_ReflexiveObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    public double[] visit(QO_IrreflexiveObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    public double[] visit(QO_SymmetricObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    public double[] visit(QO_AsymmetricObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    public double[] visit(QO_TransitiveObjectProperty queryObject, Set<Variable> boundVar) {
        return getObjectPropertyAxiomCost(queryObject, boundVar);
    }

    //    public double[] visit(QO_SubDataPropertyOf queryObject) {
//        return new double[] { 0, 0 };
//    }
//    public double[] visit(QO_EquivalentDataProperties queryObject) {
//        return new double[] { 0, 0 };
//    }
//    public double[] visit(QO_DisjointDataProperties queryObject) {
//        return new double[] { 0, 0 };
//    }
    public double[] visit(QO_DataPropertyDomain queryObject, Set<Variable> boundVar) {
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = template.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        return complex(unbound);
    }

    public double[] visit(QO_DataPropertyRange queryObject, Set<Variable> boundVar) {
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = template.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        return complex(unbound);
    }

    public double[] visit(QO_FunctionalDataProperty queryObject, Set<Variable> boundVar) {
        return getDataPropertyAxiomCost(queryObject, boundVar);
    }

    //    public double[] visit(QO_DatatypeDefinition queryObject) {
//        return new double[] { 0, 0 };
//    }
//    public double[] visit(QO_HasKey queryObject) {
//        return new double[] { 0, 0 };
//    }
    /*public double[] visit(QO_SameIndividual queryObject, Set<Variable> boundVar) {
        SameIndividual axiomTemplate=queryObject.getAxiomTemplate();
        Set<Variable> vars=axiomTemplate.getVariablesInSignature();
        int multiplier=1;        
        for (@SuppressWarnings("unused") Variable var : vars)
            multiplier*=m_indCount;
        return new double[] { multiplier*COST_ENTAILMENT, multiplier };
    }*/
    public double[] visit(QO_SameIndividual queryObject, Set<Variable> boundVar) {
        double[] estimate = new double[2];
        SameIndividual axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        Iterator<Individual> it = axiomTemplate.getIndividuals().iterator();
        Individual ind1 = it.next();
        Individual ind2 = it.next();
        double[] currentEstimate = getSameIndividualCost(ind1, ind2, boundVar);
        estimate[0] += currentEstimate[0];
        estimate[1] += currentEstimate[1];
        return estimate;
    }

    protected double[] getSameIndividualCost(Individual ind1, Individual ind2, Set<Variable> boundVar) {
        if (ind1.isVariable() && ind2.isVariable()) {
            if (!boundVar.contains(ind1) && !boundVar.contains(ind2)) { //SameIndividual(?x ?y)
                return new double[]{m_indCount * m_indCount * COST_ENTAILMENT, m_indCount * m_indCount};
            }
            else if ((boundVar.contains(ind1) && !boundVar.contains(ind2)) || (!boundVar.contains(ind1) && boundVar.contains(ind2)))//SameIndividual(?x :a) or SameIndividual(:a ?x)
                return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
            else //SameIndividual(?a :b)
                return new double[]{COST_ENTAILMENT, 1};
        }
        else if (!ind1.isVariable() && ind2.isVariable()) {
            if (!boundVar.contains(ind2)) //SameIndividual(:a ?x)
                return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
            else //SameIndividual(:a :b)
                return new double[]{COST_ENTAILMENT, 1};
        }
        else if (ind1.isVariable() && !ind2.isVariable()) {
            if (!boundVar.contains(ind1)) //SameIndividual(?x :a)
                return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
            else //SameIndividual(:a :b)
                return new double[]{COST_ENTAILMENT, 1};
        }
        else //SameIndividual(:a :b)
            return new double[]{COST_ENTAILMENT, 1};
    }

    public double[] visit(QO_DifferentIndividuals queryObject, Set<Variable> boundVar) {
        DifferentIndividuals axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        int multiplier = 1;
        for (@SuppressWarnings("unused") Variable var : vars)
            multiplier *= m_indCount;
        return new double[]{multiplier * COST_ENTAILMENT, multiplier};
    }

    public double[] visit(QO_ClassAssertion queryObject, Set<Variable> boundVar) {
        double[] estimate = new double[2];
        ClassAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Set<Variable> indVars = axiomTemplate.getIndividual().getVariablesInSignature();
        Variable indVar = indVars.isEmpty() ? null : indVars.iterator().next();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        ClassExpression expression = axiomTemplate.getClassExpression();
        if ((expression instanceof Atomic || expression.isVariable())) {
            double[] currentEstimate = getClassAssertionCost(axiomTemplate.getClassExpression(), axiomTemplate.getIndividual(), boundVar, indVar);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
            return estimate;
        }
        else return complex(unbound);
    }

    protected double[] getClassAssertionCost(ClassExpression ce, Individual ind, Set<Variable> boundVar, Variable indVar) {
        if (indVar != null && !boundVar.contains(indVar)) {
            if (ce instanceof Atomic) { //C(?x)
                //return new double[] { m_indCount * COST_CONCEPT_ENTAILMENT, m_indCount };
                return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
            }
            else //?x(?y)
                //return new double[] { m_indCount * m_classCount * COST_CONCEPT_ENTAILMENT, m_indCount * m_classCount};
                return new double[]{m_indCount * m_classCount * COST_ENTAILMENT, m_indCount * m_classCount};
        }
        else if (indVar != null && boundVar.contains(indVar)) {//C(a)<-C(x)
            if (ce instanceof Atomic) {
                //return new double[] {COST_CONCEPT_ENTAILMENT, 1};
                return new double[]{COST_ENTAILMENT, 1};
            }
            else //?x(a)<-?x(?y)
                //return new double[] { m_classCount * COST_CONCEPT_ENTAILMENT, m_classCount};
                return new double[]{m_classCount * COST_ENTAILMENT, m_classCount};
        }
        else //C(a)
            //return new double[] {COST_CONCEPT_ENTAILMENT, 1};
            return new double[]{COST_ENTAILMENT, 1};
    }

    public double[] visit(QO_ObjectPropertyAssertion queryObject, Set<Variable> boundVar) {
        double[] estimate = new double[2];
        ObjectPropertyAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Set<Variable> opVars = axiomTemplate.getObjectPropertyExpression().getVariablesInSignature();
        Variable opVar = opVars.isEmpty() ? null : opVars.iterator().next();
        ObjectPropertyExpression ope = axiomTemplate.getObjectPropertyExpression();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        if (ope instanceof Atomic || opVar.isVariable()) {
            double[] currentEstimate = getObjectPropertyAssertionCost(axiomTemplate.getObjectPropertyExpression(), axiomTemplate.getIndividual1(), axiomTemplate.getIndividual2(), boundVar, opVar);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
            return estimate;
        }
        else return complex(unbound);
    }

    protected double[] getObjectPropertyAssertionCost(ObjectPropertyExpression op, Individual ind1, Individual ind2, Set<Variable> boundVar, Variable opVar) {
        Set<Variable> unbound = new HashSet<Variable>();
        if (opVar != null && !boundVar.contains(op))
            unbound.add(opVar);
        if (ind1 instanceof Variable && !boundVar.contains(ind1))
            unbound.add((Variable) ind1);
        if (ind2 instanceof Variable && !boundVar.contains(ind2))
            unbound.add((Variable) ind2);
        if (unbound.size() == 0) //r(a,b)
            return new double[]{COST_ENTAILMENT, 1};
        else if (unbound.size() == 1 && opVar != null) // ?x(a, b)
            return new double[]{m_opCount * COST_ENTAILMENT, m_opCount};
        else if (unbound.size() == 1 && opVar == null) // op(:a ?x) or op(?x :a)
            return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
        else if (unbound.size() == 2 && opVar != null) // ?x(:a ?y) or ?x(?y :a)
            return new double[]{m_opCount * m_indCount * COST_ENTAILMENT, m_indCount * m_opCount};
        else if (unbound.size() == 2 && opVar == null) // op(?x ?y)
            return new double[]{m_indCount * m_indCount * COST_ENTAILMENT, m_indCount * m_indCount};
        else
            return new double[]{m_indCount * m_indCount * m_opCount * COST_ENTAILMENT, m_indCount * m_indCount * m_opCount};
        /*if (unbound.size()==0) //r(a,b)
            return new double[] { COST_ROLE_ENTAILMENT, 1 };
        else if (unbound.size()==1 && opVar!=null) // ?x(a, b)
            return new double[] { m_opCount * COST_ROLE_ENTAILMENT, m_opCount };
        else if (unbound.size()==1 && opVar==null) // op(:a ?x) or op(?x :a)  
            return new double[] { m_indCount * COST_ROLE_ENTAILMENT, m_indCount };
        else if (unbound.size()==2 && opVar!=null) // ?x(:a ?y) or ?x(?y :a)  
            return new double[] { m_opCount * m_indCount * COST_ROLE_ENTAILMENT, m_indCount * m_opCount };
        else if (unbound.size()==2 && opVar==null) // op(?x ?y)
            return new double[] { m_indCount * m_indCount * COST_ROLE_ENTAILMENT, m_indCount * m_indCount };
        else return new double[] { m_indCount * m_indCount * m_opCount * COST_ROLE_ENTAILMENT, m_indCount * m_indCount * m_opCount};
     */
    }

    public double[] visit(QO_NegativeObjectPropertyAssertion queryObject, Set<Variable> boundVar) {
        double[] estimate = new double[2];
        NegativeObjectPropertyAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Set<Variable> opVars = axiomTemplate.getObjectPropertyExpression().getVariablesInSignature();
        Variable opVar = opVars.isEmpty() ? null : opVars.iterator().next();
        Set<Variable> unbound = vars;
        unbound.removeAll(boundVar);
        if (opVar instanceof Atomic || opVar.isVariable()) {
            double[] currentEstimate = getObjectPropertyAssertionCost((ObjectProperty) axiomTemplate.getObjectPropertyExpression(), axiomTemplate.getIndividual1(), axiomTemplate.getIndividual2(), boundVar, opVar);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
            return estimate;
        }
        else return complex(unbound);
    }

    public double[] visit(QO_DataPropertyAssertion queryObject, Set<Variable> boundVar) {
        double[] estimate = new double[2];

        DataPropertyAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Variable indVar = null;
        Variable dpVar = null;
        Variable litVar = null;
        for (Variable var : vars) {
            if (var instanceof DataPropertyVariable)
                dpVar = var;
            else if (var instanceof IndividualVariable)
                indVar = var;
            else
                litVar = var;
        }

        double[] currentEstimate = getDataPropertyAssertionCost(axiomTemplate.getDataPropertyExpression(), axiomTemplate.getIndividual(), axiomTemplate.getLiteral(), boundVar, dpVar, indVar, litVar);
        estimate[0] += currentEstimate[0];
        estimate[1] += currentEstimate[1];

        return estimate;
    }

    protected double[] getDataPropertyAssertionCost(DataPropertyExpression dp, Individual ind, Literal lit, Set<Variable> boundVar, Variable dpVar, Variable indVar, Variable litVar) {
        Set<Variable> unbound = new HashSet<Variable>();
        if (dpVar != null && !boundVar.contains(dpVar))
            unbound.add(dpVar);
        if (indVar != null && !boundVar.contains(indVar))
            unbound.add((Variable) indVar);
        if (litVar != null && !boundVar.contains(litVar))
            unbound.add((Variable) litVar);

        if (unbound.size() == 0)
            return new double[]{COST_ENTAILMENT, 1};
        else if (unbound.size() == 1 && dpVar != null) // ?x(i, lit)
            return new double[]{m_dpCount * COST_ENTAILMENT, m_dpCount};
        else if (unbound.size() == 1 && indVar != null) // dp(?x :a)
            return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
        else if (unbound.size() == 1 && litVar != null) // dp(:a ?x)
            return new double[]{m_litCount * COST_ENTAILMENT, m_litCount};
        else if (unbound.size() == 2 && litVar == null) // ?x(?y :a)
            return new double[]{m_dpCount * m_indCount * COST_ENTAILMENT, m_indCount * m_dpCount};
        else if (unbound.size() == 2 && indVar == null) // ?x(:a ?y)
            return new double[]{m_dpCount * m_litCount * COST_ENTAILMENT, m_litCount * m_dpCount};
        else if (unbound.size() == 2 && dpVar == null) // dp(?x ?y)
            return new double[]{m_indCount * m_litCount * COST_ENTAILMENT, m_indCount * m_litCount};
        else  //?x(?y ?z)
            return new double[]{m_indCount * m_litCount * m_opCount * COST_ENTAILMENT, m_indCount * m_litCount * m_opCount};
        /*if (unbound.size()==0)
            return new double[] { COST_ROLE_ENTAILMENT, 1 };
        else if (unbound.size()==1 && dpVar!=null) // ?x(i, lit)
            return new double[] { m_dpCount * COST_ROLE_ENTAILMENT, m_dpCount };
        else if (unbound.size()==1 && indVar!=null) // dp(?x :a)  
            return new double[] { m_indCount * COST_ROLE_ENTAILMENT, m_indCount };
        else if (unbound.size()==1 && litVar!=null) // dp(:a ?x)  
            return new double[] { m_litCount * COST_ROLE_ENTAILMENT, m_litCount };
        else if (unbound.size()==2 && litVar==null) // ?x(?y :a)  
            return new double[] { m_dpCount * m_indCount * COST_ROLE_ENTAILMENT, m_indCount * m_dpCount };
        else if (unbound.size()==2 && indVar==null) // ?x(:a ?y)  
            return new double[] { m_dpCount * m_litCount * COST_ROLE_ENTAILMENT, m_litCount * m_dpCount };
        else if (unbound.size()==2 && dpVar==null) // dp(?x ?y)
            return new double[] { m_indCount * m_litCount * COST_ROLE_ENTAILMENT, m_indCount * m_litCount };
        else  //?x(?y ?z)
        	return new double[] { m_indCount * m_litCount * m_opCount * COST_ROLE_ENTAILMENT, m_indCount * m_litCount * m_opCount};    
    */
    }

    public double[] visit(QO_NegativeDataPropertyAssertion queryObject, Set<Variable> boundVar) {
        double[] estimate = new double[2];
        NegativeDataPropertyAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Variable indVar = null;
        Variable dpVar = null;
        Variable litVar = null;
        for (Variable var : vars) {
            if (var instanceof DataPropertyVariable)
                dpVar = var;
            else if (var instanceof IndividualVariable)
                indVar = var;
            else
                litVar = var;
        }
        double[] currentEstimate = getDataPropertyAssertionCost((DataProperty) axiomTemplate.getDataPropertyExpression(), axiomTemplate.getIndividual(), axiomTemplate.getLiteral(), boundVar, dpVar, indVar, litVar);
        estimate[0] += currentEstimate[0];
        estimate[1] += currentEstimate[1];
        return estimate;
    }

    protected double[] complex(Set<Variable> unbound) {
        int tests = 0;
        // complex
        boolean first = true;
        for (Variable var : unbound) {
            int signatureSize = 0;
            if (var instanceof ClassVariable)
                signatureSize = m_classCount;
            else if (var instanceof DatatypeVariable)
                signatureSize = m_datatypeCount;
            else if (var instanceof ObjectPropertyVariable)
                signatureSize = m_opCount;
            else if (var instanceof DataPropertyVariable)
                signatureSize = m_dpCount;
            else if (var instanceof AnnotationPropertyVariable)
                signatureSize = m_apCount;
            else if (var instanceof IndividualVariable)
                signatureSize = m_indCount;
            if (first) {
                tests += signatureSize;
                first = false;
            }
            else
                tests *= signatureSize;
        }
        return new double[]{tests * COST_ENTAILMENT, tests};
    }
}

