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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.HermiT.hierarchy.InstanceStatistics;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.queryobjects.DynamicQueryObjectVisitorEx;
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
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.DifferentIndividuals;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class DynamicCostEstimationVisitor implements DynamicQueryObjectVisitorEx<double[]> {
    protected double COST_ENTAILMENT = 100;
    protected double COST_LOOKUP = 1;
    protected double COST_CLASS_HIERARCHY_INSERTION = 10 * COST_ENTAILMENT;

    protected final OWLReasoner m_reasoner;
    protected final OWLBGPHermiT m_hermit;
    protected final OWLDataFactory m_dataFactory;
    protected final OWLOntologyGraph m_graph;
    protected final InstanceStatistics m_instanceStatistics;
    protected final Map<NamedIndividual, Set<NamedIndividual>> m_individualToPartition;
    protected final Map<Integer, Set<List<NamedIndividual>>> m_pairIndToPartition;
    protected final Map<Integer, Set<NamedIndividual>> m_sucIndToPartition;
    protected final Map<Integer, Set<NamedIndividual>> m_preIndToPartition;
    protected final int m_classCount;
    protected final int m_opCount;
    protected final int m_dpCount;
    protected final int m_apCount;
    protected final int m_datatypeCount;
    protected final int m_indCount;
    protected final int m_litCount;
    protected final String sampl;
    protected Map<Variable, Integer> m_bindingPositions;
    protected List<Atomic[]> m_candidateBindings;


    public DynamicCostEstimationVisitor(OWLOntologyGraph graph) {
        m_reasoner = graph.getReasoner();
        if (!(m_reasoner instanceof OWLBGPHermiT))
            throw new IllegalArgumentException("Error: The HermiT cost estimator can only be instantiated with a graph that has a (HermiT) Reasoner instance attached to it.");
        m_hermit = (OWLBGPHermiT) m_reasoner;
        //m_instanceStatistics=null;
        m_instanceStatistics = m_hermit.getInstanceStatistics();
        //m_individualToPartition=null;
        //long t=System.currentTimeMillis();
        m_individualToPartition = m_instanceStatistics.getPartitioning();
        //m_individualToPartition=null;
        //System.out.println("The class partitioning lasted "+(System.currentTimeMillis()-t) +" msec and contains " +m_individualToPartition.keySet().size()+ " clusters.");
        //System.out.println("partitioning started");
        m_pairIndToPartition = m_instanceStatistics.getPairIndsPartitioning();
        //m_pairIndToPartition=null;
        //System.out.println("partitioning ended");
        //t=System.currentTimeMillis();
        //System.out.println("partitioning started");
        m_sucIndToPartition = m_instanceStatistics.getPairFirstIndPartitioning();
        //m_sucIndToPartition=null;
        m_preIndToPartition = m_instanceStatistics.getPairSecondIndPartitioning();
        //m_preIndToPartition=null;
        //System.out.println("The partitioning lasted "+(System.currentTimeMillis()-t) +" msec and contains "+m_sucIndToPartition.keySet().size()+ " first ind clusters and "+m_preIndToPartition.keySet().size()+" second ind clusters");
        m_dataFactory = graph.getOntology().getOWLOntologyManager().getOWLDataFactory();
        m_graph = graph;
        m_classCount = graph.getClassesInSignature().size();
        m_opCount = graph.getObjectPropertiesInSignature().size();
        m_dpCount = graph.getDataPropertiesInSignature().size();
        m_apCount = graph.getAnnotationPropertiesInSignature().size();
        m_datatypeCount = graph.getDatatypesInSignature().size();
        m_indCount = graph.getIndividualsInSignature().size();
        m_litCount = graph.getLiteralsInSignature().size();
        sampl = System.getProperty("sampling");
    }

    public void setCandidateBindings(List<Atomic[]> newCandidateBindings) {
        m_candidateBindings = newCandidateBindings;
    }

    public void setBindingPositions(Map<Variable, Integer> bindingPositions) {
        m_bindingPositions = bindingPositions;
    }

    public double[] visit(QO_SubClassOf queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        SubClassOf instantiated = (SubClassOf) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        ClassExpression subClass = instantiated.getSubClassExpression();
        ClassExpression superClass = instantiated.getSuperClassExpression();
        int results = 1;
        if ((subClass instanceof Atomic || subClass.isVariable()) && (superClass instanceof Atomic || superClass.isVariable())) {
            for (int i = 0; i < unbound.size(); i++)
                results *= m_classCount;
            return new double[]{m_candidateBindings.size() * results * COST_LOOKUP, m_candidateBindings.size() * results};
        }
        else return complex(unbound, m_candidateBindings);
    }

    public double[] visit(QO_EquivalentClasses queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        EquivalentClasses instantiated = (EquivalentClasses) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        Set<ClassExpression> equivClasses = instantiated.getClassExpressions();
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
            return new double[]{m_candidateBindings.size() * results * COST_LOOKUP, m_candidateBindings.size() * results};
        }
        else return complex(unbound, m_candidateBindings);
    }

    public double[] visit(QO_DisjointClasses queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        DisjointClasses instantiated = (DisjointClasses) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        //Set<ClassExpression> equivClasses=instantiated.getClassExpressions();
        return complex(unbound, m_candidateBindings);
    }

    //    public double[] visit(QO_DisjointUnion queryObject) {
//        return new double[] { 0, 0 };
//    }
    public double[] visit(QO_SubObjectPropertyOf queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        Axiom instantiated = (Axiom) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        int results = 1;
        for (int i = 0; i < unbound.size(); i++)
            results *= m_opCount;
        return new double[]{m_candidateBindings.size() * results * COST_LOOKUP, m_candidateBindings.size() * results};
    }

    //    public double[] visit(QO_EquivalentObjectProperties queryObject) {
//        return new double[] { 0, 0 };
//    }
//    public double[] visit(QO_DisjodointObjectProperties queryObject) {
//        return new double[] { 0, 0 };
//    }
    public double[] visit(QO_InverseObjectProperties queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        Axiom instantiated = (Axiom) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        int results = 1;
        for (int i = 0; i < unbound.size(); i++)
            results *= m_opCount;
        return new double[]{m_candidateBindings.size() * results * COST_LOOKUP, m_candidateBindings.size() * results};
    }

    public double[] visit(QO_ObjectPropertyDomain queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        ObjectPropertyDomain instantiated = (ObjectPropertyDomain) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        return complex(unbound, m_candidateBindings);
    }

    public double[] visit(QO_ObjectPropertyRange queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        ObjectPropertyRange instantiated = (ObjectPropertyRange) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        return complex(unbound, m_candidateBindings);
    }

    public double[] visit(QO_FunctionalObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
    }

    protected double[] getObjectPropertyAxiomCost(QueryObject<? extends Axiom> queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Set<Variable> opeVar = queryObject.getAxiomTemplate().getVariablesInSignature();
        if (opeVar.isEmpty() || testBinding[m_bindingPositions.get(opeVar.iterator().next())] != null)
            return new double[]{COST_ENTAILMENT, 1};
        else
            return new double[]{m_opCount * COST_ENTAILMENT, m_opCount}; // better return told numbers
    }

    protected double[] getDataPropertyAxiomCost(QueryObject<? extends Axiom> queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Set<Variable> dpeVar = queryObject.getAxiomTemplate().getVariablesInSignature();
        if (dpeVar.isEmpty() || testBinding[m_bindingPositions.get(dpeVar.iterator().next())] != null)
            return new double[]{COST_ENTAILMENT, 1};
        else
            return new double[]{m_dpCount * COST_ENTAILMENT, m_dpCount}; // better return told numbers
    }

    public double[] visit(QO_InverseFunctionalObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
    }

    public double[] visit(QO_ReflexiveObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
    }

    public double[] visit(QO_IrreflexiveObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
    }

    public double[] visit(QO_SymmetricObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
    }

    public double[] visit(QO_AsymmetricObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
    }

    public double[] visit(QO_TransitiveObjectProperty queryObject) {
        return getObjectPropertyAxiomCost(queryObject);
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
    public double[] visit(QO_DataPropertyDomain queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        DataPropertyDomain instantiated = (DataPropertyDomain) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        return complex(unbound, m_candidateBindings);
    }

    public double[] visit(QO_DataPropertyRange queryObject) {
        double[] result = new double[2];
        if (m_candidateBindings.isEmpty())
            return result; // no answers, no tests
        // check just one binding
        Atomic[] testBinding = m_candidateBindings.get(0);
        Axiom template = queryObject.getAxiomTemplate();
        Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        for (Variable var : vars) {
            Atomic binding = testBinding[m_bindingPositions.get(var)];
            if (binding != null)
                existingBindings.put(var, binding);
        }
        DataPropertyRange instantiated = (DataPropertyRange) template.getBoundVersion(existingBindings);
        Set<Variable> unbound = instantiated.getVariablesInSignature();
        return complex(unbound, m_candidateBindings);
    }

    public double[] visit(QO_FunctionalDataProperty queryObject) {
        return getDataPropertyAxiomCost(queryObject);
    }
//    public double[] visit(QO_DatatypeDefinition queryObject) {
//        return new double[] { 0, 0 };
//    }
//    public double[] visit(QO_HasKey queryObject) {
//        return new double[] { 0, 0 };
//    }
    /*public double[] visit(QO_SameIndividual queryObject) {
        double[] estimate=new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
        SameIndividual axiomTemplate=queryObject.getAxiomTemplate();
        Set<Variable> vars=axiomTemplate.getVariablesInSignature();
        int multiplier=1;
        for (Variable var : vars) {
            int varPos=m_bindingPositions.get(var);
            if (m_candidateBindings.get(0)[varPos]==null) //unbound
                multiplier*=m_indCount;
            else // bound
                multiplier*=m_candidateBindings.size();
        }
        return new double[] { multiplier*COST_ENTAILMENT, multiplier };
    }*/

    public double[] visit(QO_SameIndividual queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
        SameIndividual axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        Set<Variable> unbound = new HashSet<Variable>();

        for (Atomic[] testBinding : m_candidateBindings) {
            existingBindings.clear();
            for (Variable var : vars) {
                Atomic binding = testBinding[m_bindingPositions.get(var)];
                if (binding != null)
                    existingBindings.put(var, binding);
            }
            SameIndividual instantiated = (SameIndividual) axiomTemplate.getBoundVersion(existingBindings);
            unbound.addAll(vars);
            unbound.removeAll(existingBindings.keySet());
            Iterator<Individual> it = instantiated.getIndividuals().iterator();
            Individual ind1 = it.next();
            Individual ind2 = it.next();
            double[] currentEstimate = getSameIndividualCost(ind1, ind2, unbound);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
        }
        return estimate;
    }

    protected double[] getSameIndividualCost(Individual ind1, Individual ind2, Set<Variable> unbound) {
        if (unbound.size() == 0) //SameIndividual(:a :b)
            return new double[]{COST_ENTAILMENT, 1};
        else if (unbound.size() == 1) // SameIndividual(:a ?x) or SameIndividual(?x :b)
            return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
        else //SameIndividual(?x ?y) 
            return new double[]{m_indCount * m_indCount * COST_ENTAILMENT, m_indCount * m_indCount};
    }

    public double[] visit(QO_DifferentIndividuals queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
        DifferentIndividuals axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        int multiplier = 1;
        for (Variable var : vars) {
            int varPos = m_bindingPositions.get(var);
            if (m_candidateBindings.get(0)[varPos] == null) //unbound
                multiplier *= m_indCount;
            else // bound
                multiplier *= m_candidateBindings.size();
        }
        return new double[]{multiplier * COST_ENTAILMENT, multiplier};
    }

    public double[] visit(QO_ClassAssertion queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
        ClassAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Set<Variable> indVars = axiomTemplate.getIndividual().getVariablesInSignature();
        Variable indVar = indVars.isEmpty() ? null : indVars.iterator().next();
        ClassExpression expression = axiomTemplate.getClassExpression();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        Set<Variable> unbound = new HashSet<Variable>();


        //boolean[] result=m_instanceStatistics.isKnownOrPossibleInstance((OWLClass)ce.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory)); 
        //estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));

        if (expression instanceof Atomic || expression.isVariable()) {
            //Map<Integer, Set<OWLNamedIndividual>> candidateMap=new HashMap<Integer,Set<OWLNamedIndividual>>();

            if (sampl != null && sampl.equals("RSampling")) {
                int sampleSize = m_candidateBindings.size() * 5 / 10;
                List<Atomic[]> sampleBindings = new ArrayList<Atomic[]>();
                ArrayList<Atomic[]> shuffledBindings = new ArrayList<Atomic[]>();
                shuffledBindings.addAll(m_candidateBindings);
                Collections.shuffle(shuffledBindings);
                sampleBindings = shuffledBindings.subList(0, sampleSize);
                m_candidateBindings = new ArrayList<Atomic[]>();
                m_candidateBindings.addAll(sampleBindings);

                for (Atomic[] testBinding : m_candidateBindings) {
                    existingBindings.clear();
                    for (Variable var : vars) {
                        Atomic binding = testBinding[m_bindingPositions.get(var)];
                        if (binding != null)
                            existingBindings.put(var, binding);
                    }
                    ClassAssertion instantiated = (ClassAssertion) axiomTemplate.getBoundVersion(existingBindings);
                    unbound.addAll(vars);
                    unbound.removeAll(existingBindings.keySet());
                    double[] currentEstimate = getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
                    estimate[0] += currentEstimate[0];
                    estimate[1] += currentEstimate[1];
                }

            }
            else if (sampl != null && sampl.equals("CSampling")) {
                Set<NamedIndividual> candidateIndSet = new HashSet<NamedIndividual>();
                for (Atomic[] testBinding : m_candidateBindings) {
                    Atomic binding = testBinding[m_bindingPositions.get(indVar)];
                    if (binding == null) break;
                    candidateIndSet.add((NamedIndividual) binding);
                }
                if (candidateIndSet.isEmpty()) {
                    existingBindings.clear();
                    ClassAssertion instantiated = (ClassAssertion) axiomTemplate.getBoundVersion(existingBindings);
                    unbound.addAll(vars);
                    unbound.removeAll(existingBindings.keySet());

                    double[] currentEstimate = getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
                    estimate[0] += currentEstimate[0];
                    estimate[1] += currentEstimate[1];
                    //estimate[0]=estimate[0]*m_candidateBindings.size();
                    //estimate[1]=estimate[1]*m_candidateBindings.size();
                }
                else {
                    //long h=System.currentTimeMillis();
                    while (!candidateIndSet.isEmpty()) {
                        Set<NamedIndividual> holdIndSet = candidateIndSet;
                        Iterator<NamedIndividual> itr = candidateIndSet.iterator();
                        NamedIndividual element = itr.next();
                        Set<NamedIndividual> indSet = m_individualToPartition.get(element);
                        holdIndSet.retainAll(indSet);
                        existingBindings.put(indVar, element);
                        ClassAssertion instantiated = (ClassAssertion) axiomTemplate.getBoundVersion(existingBindings);
                        unbound.addAll(vars);
                        unbound.removeAll(existingBindings.keySet());

                        double[] currentEstimate = getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
                        estimate[0] += currentEstimate[0];
                        estimate[1] += currentEstimate[1];
                        estimate[0] += currentEstimate[0] * holdIndSet.size();
                        estimate[1] += currentEstimate[1] * holdIndSet.size();
                        candidateIndSet.removeAll(holdIndSet);
                    }
                    //System.out.println("The sampling time is" +(System.currentTimeMillis()-h)+ "ms.");
                }
            }
            else for (Atomic[] testBinding : m_candidateBindings) {
                    existingBindings.clear();
                    for (Variable var : vars) {
                        Atomic binding = testBinding[m_bindingPositions.get(var)];
                        if (binding != null)
                            existingBindings.put(var, binding);
                    }
                    ClassAssertion instantiated = (ClassAssertion) axiomTemplate.getBoundVersion(existingBindings);
                    unbound.addAll(vars);
                    unbound.removeAll(existingBindings.keySet());
                    double[] currentEstimate = getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
                    estimate[0] += currentEstimate[0];
                    estimate[1] += currentEstimate[1];
                }
//}           
            return estimate;
        }
        else return complex(unbound, m_candidateBindings);

        //HashMap<Integer,Atomic> candidateClusterMap=new HashMap<Integer,Atomic>();
            /*if (!candidateIndSet.isEmpty()){
        	for (Integer code:m_clusterMap.keySet()) {
        		Set<OWLNamedIndividual> indSet=candidateIndSet;
        		Set<OWLNamedIndividual> clusterSet=m_clusterMap.get(code);
        		indSet.retainAll(clusterSet);
        		if (!indSet.isEmpty()){        		
        			OWLNamedIndividual ind=indSet.iterator().next();
        		    Atomic binding=NamedIndividual.create(ind.getIRI().toString());
                    if (binding!=null)
                    	existingBindings.put(indVar,binding);
                    ClassAssertion instantiated=(ClassAssertion)axiomTemplate.getBoundVersion(existingBindings);
                    unbound.addAll(vars);
                    unbound.removeAll(existingBindings.keySet());
                 
                    double[] currentEstimate=getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
                    estimate[0]+=currentEstimate[0];
                    estimate[1]+=currentEstimate[1];
                    estimate[0]=estimate[0]*indSet.size();
                    estimate[1]=estimate[1]*indSet.size(); 
//        		candidateMap.put(code,indSet);
        		}      
            }
            }
        	else for (Atomic[] testBinding : m_candidateBindings) {
                existingBindings.clear();
                for (Variable var : vars) {
                   Atomic binding=testBinding[m_bindingPositions.get(var)];
                   if (binding!=null)
                      existingBindings.put(var,binding);
                   else flag=1;
                }
//                 if (!existingBindings.isEmpty()){
                ClassAssertion instantiated=(ClassAssertion)axiomTemplate.getBoundVersion(existingBindings);
                unbound.addAll(vars);
                unbound.removeAll(existingBindings.keySet());
                 
                double[] currentEstimate=getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
                estimate[0]+=currentEstimate[0];
                estimate[1]+=currentEstimate[1];
                if (flag==1) break;    
//                } 
           }
           }*/
           /*else for (Atomic[] testBinding : m_candidateBindings) {
           existingBindings.clear();
           for (Variable var : vars) {
              Atomic binding=testBinding[m_bindingPositions.get(var)];
              if (binding!=null)
                 existingBindings.put(var,binding);
           }
           ClassAssertion instantiated=(ClassAssertion)axiomTemplate.getBoundVersion(existingBindings);
           unbound.addAll(vars);
           unbound.removeAll(existingBindings.keySet()); 
           double[] currentEstimate=getClassAssertionCost(instantiated.getClassExpression(), instantiated.getIndividual(), unbound, indVar);
           estimate[0]+=currentEstimate[0];
           estimate[1]+=currentEstimate[1]; 
           }*/
    }

    protected double[] getClassAssertionCost(ClassExpression ce, Individual ind, Set<Variable> unbound, Variable indVar) {
        if (unbound.size() == 0)
            return new double[]{COST_ENTAILMENT, 1};
        else if (unbound.size() == 1 && indVar != null) // C(?x)
            return new double[]{m_indCount * COST_ENTAILMENT, m_indCount};
        else if (indVar == null && unbound.size() == 1 && ce instanceof Atomic) // ?x(:a)
            return new double[]{m_classCount * COST_ENTAILMENT, m_classCount};
        else {
            throw new RuntimeException("it shouldn't have arrived here");
            //double tests=complex(unbound);
            //return new double[] { tests, tests }; // assume all tests succeed
        }
    }

    public double[] visit(QO_ObjectPropertyAssertion queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
        ObjectPropertyAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        ObjectPropertyExpression ope = axiomTemplate.getObjectPropertyExpression();
        Set<Variable> opVars = ope.getVariablesInSignature();
        Variable opVar = opVars.isEmpty() ? null : opVars.iterator().next();
        Set<Variable> ind1Vars = axiomTemplate.getIndividual1().getVariablesInSignature();
        Variable ind1Var = ind1Vars.isEmpty() ? null : ind1Vars.iterator().next();
        Set<Variable> ind2Vars = axiomTemplate.getIndividual2().getVariablesInSignature();
        Variable ind2Var = ind2Vars.isEmpty() ? null : ind2Vars.iterator().next();

        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        Set<Variable> unbound = new HashSet<Variable>();
        if (ope instanceof Atomic || ope.isVariable()) {
            if (sampl != null && sampl.equals("RSampling")) {
                int sampleSize = m_candidateBindings.size() * 5 / 10;
                List<Atomic[]> sampleBindings = new ArrayList<Atomic[]>();
                ArrayList<Atomic[]> shuffledBindings = new ArrayList<Atomic[]>();
                shuffledBindings.addAll(m_candidateBindings);
                Collections.shuffle(shuffledBindings);
                sampleBindings = shuffledBindings.subList(0, sampleSize);
                m_candidateBindings = new ArrayList<Atomic[]>();
                m_candidateBindings = sampleBindings;

                for (Atomic[] testBinding : m_candidateBindings) {
                    existingBindings.clear();
                    for (Variable var : vars) {
                        Atomic binding = testBinding[m_bindingPositions.get(var)];
                        if (binding != null)
                            existingBindings.put(var, binding);
                    }
                    ObjectPropertyAssertion instantiated = (ObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
                    unbound.addAll(vars);
                    unbound.removeAll(existingBindings.keySet());

                    double[] currentEstimate = getObjectPropertyAssertionCost(instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
                    estimate[0] += currentEstimate[0];
                    estimate[1] += currentEstimate[1];
                }
            }
            if (sampl != null && sampl.equals("CSampling")) {
                int flag = 0;
                //Set<NamedIndividual> candidateIndSucOrPre=new HashSet<NamedIndividual>();
                Set<List<NamedIndividual>> pairInds = new HashSet<List<NamedIndividual>>();
                Set<NamedIndividual> candidateIndSet = new HashSet<NamedIndividual>();
                for (Atomic[] testBinding : m_candidateBindings) {
                    if (ind1Var != null && ind2Var != null) {
                        Atomic binding1 = testBinding[m_bindingPositions.get(ind1Var)];
                        Atomic binding2 = testBinding[m_bindingPositions.get(ind2Var)];
                        if (binding1 != null && binding2 != null) {
                            List<NamedIndividual> indList = new ArrayList<NamedIndividual>();
                            indList.add((NamedIndividual) binding1);
                            indList.add((NamedIndividual) binding2);
                            pairInds.add(indList);
                            flag = 1;
                        }
                        else if (binding1 != null && binding2 == null) {
                            candidateIndSet.add((NamedIndividual) binding1);
                            flag = 2;

                        }
                        else if (binding1 == null && binding2 != null) {
                            candidateIndSet.add((NamedIndividual) binding2);
                            flag = 3;
                        }
                        else break;
                    }
                }
                if (flag == 0) {
                    for (Atomic[] testBinding : m_candidateBindings) {
                        existingBindings.clear();
                        for (Variable var : vars) {
                            Atomic binding = testBinding[m_bindingPositions.get(var)];
                            if (binding != null)
                                existingBindings.put(var, binding);
                        }
                        ObjectPropertyAssertion instantiated = (ObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
                        unbound.addAll(vars);
                        unbound.removeAll(existingBindings.keySet());

                        double[] currentEstimate = getObjectPropertyAssertionCost(instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
                        estimate[0] += currentEstimate[0];
                        estimate[1] += currentEstimate[1];
                    }
                    return estimate;
                }
                else if (flag == 1) {
                    for (int f : m_pairIndToPartition.keySet()) {
                        if (!pairInds.isEmpty()) {
                            Set<List<NamedIndividual>> keyValues = m_pairIndToPartition.get(f);
                            Set<List<NamedIndividual>> holdIndSet = new HashSet<List<NamedIndividual>>();
                            holdIndSet.addAll(pairInds);
                            //for (List<NamedIndividual> ind1List:holdIndSet) {
                            //	for (List<NamedIndividual> ind2List:keyValues) {
                            //		if (ind1List.get(0)==ind2List.get(0) && ind1List.get(1)==ind2List.get(1)) {
                            //			System.out.println("one found");
                            //		}
                            //	}
                            //}
                            holdIndSet.retainAll(keyValues);

                            if (!holdIndSet.isEmpty()) {
                                //System.out.println("with intersection found with size "+holdIndSet.size());
                                Iterator<List<NamedIndividual>> itr = holdIndSet.iterator();
                                List<NamedIndividual> element = itr.next();
                                existingBindings.put(ind1Var, element.get(0));
                                existingBindings.put(ind2Var, element.get(1));

                                ObjectPropertyAssertion instantiated = (ObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
                                unbound.addAll(vars);
                                unbound.removeAll(existingBindings.keySet());

                                double[] currentEstimate = getObjectPropertyAssertionCost(instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
                                estimate[0] += currentEstimate[0];
                                estimate[1] += currentEstimate[1];
                                estimate[0] += currentEstimate[0] * holdIndSet.size();
                                estimate[1] += currentEstimate[1] * holdIndSet.size();
                                pairInds.removeAll(holdIndSet);
                            }

                        }
                    }
                }
                else if (flag == 2) {
                    if (!candidateIndSet.isEmpty()) {
                        for (Integer code : m_preIndToPartition.keySet()) {
                            Set<NamedIndividual> individualSet = new HashSet<NamedIndividual>();
                            individualSet.addAll(candidateIndSet);
                            Set<NamedIndividual> clusterSet = m_preIndToPartition.get(code);
                            individualSet.retainAll(clusterSet);
                            if (!individualSet.isEmpty()) {
                                NamedIndividual binding = individualSet.iterator().next();
                                existingBindings.put(ind1Var, binding);
                                ObjectPropertyAssertion instantiated = (ObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
                                unbound.addAll(vars);
                                unbound.removeAll(existingBindings.keySet());

                                double[] currentEstimate = getObjectPropertyAssertionCost(instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
                                estimate[0] += currentEstimate[0];
                                estimate[1] += currentEstimate[1];
                                estimate[0] += currentEstimate[0] * individualSet.size();
                                estimate[1] += currentEstimate[1] * individualSet.size();
                                candidateIndSet.removeAll(individualSet);
                            }

                        }
                    }
                }
                else {//flag==3
                    if (!candidateIndSet.isEmpty()) {
                        for (Integer code : m_sucIndToPartition.keySet()) {
                            Set<NamedIndividual> individualSet = new HashSet<NamedIndividual>();
                            individualSet.addAll(candidateIndSet);
                            Set<NamedIndividual> clusterSet = m_sucIndToPartition.get(code);
                            individualSet.retainAll(clusterSet);
                            if (!individualSet.isEmpty()) {
                                NamedIndividual binding = individualSet.iterator().next();
                                existingBindings.put(ind2Var, binding);
                                ObjectPropertyAssertion instantiated = (ObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
                                unbound.addAll(vars);
                                unbound.removeAll(existingBindings.keySet());

                                double[] currentEstimate = getObjectPropertyAssertionCost(instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
                                estimate[0] += currentEstimate[0];
                                estimate[1] += currentEstimate[1];
                                estimate[0] += currentEstimate[0] * individualSet.size();
                                estimate[1] += currentEstimate[1] * individualSet.size();
                                candidateIndSet.removeAll(individualSet);
                            }

                        }
                    }
                }
            }
            else for (Atomic[] testBinding : m_candidateBindings) {
                existingBindings.clear();
                for (Variable var : vars) {
                    Atomic binding = testBinding[m_bindingPositions.get(var)];
                    if (binding != null)
                        existingBindings.put(var, binding);
                }
                ObjectPropertyAssertion instantiated = (ObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
                unbound.addAll(vars);
                unbound.removeAll(existingBindings.keySet());

                double[] currentEstimate = getObjectPropertyAssertionCost(instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
                estimate[0] += currentEstimate[0];
                estimate[1] += currentEstimate[1];
            }
//}
            return estimate;
        }
        else return complex(unbound, m_candidateBindings);
    }

    protected double[] getObjectPropertyAssertionCost(ObjectPropertyExpression op, Individual ind1, Individual ind2, Set<Variable> unbound, Variable opVar) {
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
        else {
            return new double[]{m_indCount * m_indCount * m_opCount * COST_ENTAILMENT, m_indCount * m_indCount * m_opCount};
            //    int tests=complex(unbound);
            //    return new double[] { tests, tests }; // assume all tests succeed
        }
    }

    public double[] visit(QO_NegativeObjectPropertyAssertion queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
        NegativeObjectPropertyAssertion axiomTemplate = queryObject.getAxiomTemplate();
        Set<Variable> vars = axiomTemplate.getVariablesInSignature();
        Set<Variable> opVars = axiomTemplate.getObjectPropertyExpression().getVariablesInSignature();
        Variable opVar = opVars.isEmpty() ? null : opVars.iterator().next();
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        Set<Variable> unbound = new HashSet<Variable>();
        for (Atomic[] testBinding : m_candidateBindings) {
            existingBindings.clear();
            for (Variable var : vars) {
                Atomic binding = testBinding[m_bindingPositions.get(var)];
                if (binding != null)
                    existingBindings.put(var, binding);
            }
            NegativeObjectPropertyAssertion instantiated = (NegativeObjectPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
            unbound.addAll(vars);
            unbound.removeAll(existingBindings.keySet());

            double[] currentEstimate = getObjectPropertyAssertionCost((ObjectProperty) instantiated.getObjectPropertyExpression(), instantiated.getIndividual1(), instantiated.getIndividual2(), unbound, opVar);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
        }
        return estimate;
    }

    public double[] visit(QO_DataPropertyAssertion queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
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
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        Set<Variable> unbound = new HashSet<Variable>();

        /*if (sampl!=null && sampl.equals("Sampling")) { 
          int sampleSize=m_candidateBindings.size()*5/10;
          List<Atomic[]> sampleBindings= new ArrayList<Atomic[]>();
          ArrayList<Atomic[]> shuffledBindings=new ArrayList<Atomic[]>();
          shuffledBindings.addAll(m_candidateBindings);
          Collections.shuffle(shuffledBindings);
          sampleBindings=shuffledBindings.subList(0, sampleSize);
          m_candidateBindings=new ArrayList<Atomic[]>();
          m_candidateBindings=sampleBindings;
        }*/

        for (Atomic[] testBinding : m_candidateBindings) {
            existingBindings.clear();
            for (Variable var : vars) {
                Atomic binding = testBinding[m_bindingPositions.get(var)];
                if (binding != null)
                    existingBindings.put(var, binding);
            }
            DataPropertyAssertion instantiated = (DataPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
            unbound.addAll(vars);
            unbound.removeAll(existingBindings.keySet());

            double[] currentEstimate = getDataPropertyAssertionCost(instantiated.getDataPropertyExpression(), instantiated.getIndividual(), instantiated.getLiteral(), unbound, dpVar, indVar, litVar);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
        }
        return estimate;
    }

    protected double[] getDataPropertyAssertionCost(DataPropertyExpression dp, Individual ind, Literal lit, Set<Variable> unbound, Variable dpVar, Variable indVar, Variable litVar) {
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
        else {
            throw new RuntimeException("it shouldn't have arrived here");
            //int tests=complex(unbound);
            //return new double[] { tests, tests }; // assume all tests succeed
        }
    }

    public double[] visit(QO_NegativeDataPropertyAssertion queryObject) {
        double[] estimate = new double[2];
        if (m_candidateBindings.isEmpty())
            return estimate; // no answers, no tests
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
        Map<Variable, Atomic> existingBindings = new HashMap<Variable, Atomic>();
        Set<Variable> unbound = new HashSet<Variable>();
        for (Atomic[] testBinding : m_candidateBindings) {
            existingBindings.clear();
            for (Variable var : vars) {
                Atomic binding = testBinding[m_bindingPositions.get(var)];
                if (binding != null)
                    existingBindings.put(var, binding);
            }
            NegativeDataPropertyAssertion instantiated = (NegativeDataPropertyAssertion) axiomTemplate.getBoundVersion(existingBindings);
            unbound.addAll(vars);
            unbound.removeAll(existingBindings.keySet());

            double[] currentEstimate = getDataPropertyAssertionCost((DataProperty) instantiated.getDataPropertyExpression(), instantiated.getIndividual(), instantiated.getLiteral(), unbound, dpVar, indVar, litVar);
            estimate[0] += currentEstimate[0];
            estimate[1] += currentEstimate[1];
        }
        return estimate;
    }

    protected double[] complex(Set<Variable> unbound, List<Atomic[]> candidateBindings) {
        int tests = 1;
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
                tests += signatureSize - 1;
                first = false;
            }
            else
                tests *= signatureSize;
        }
        return new double[]{m_candidateBindings.size() * tests * COST_ENTAILMENT, m_candidateBindings.size() * tests};
    }
}
