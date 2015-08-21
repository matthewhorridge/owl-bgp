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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.HermiT.hierarchy.InstanceStatistics;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ClassAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;

public class BindingsIntersection {

    protected final OWLReasoner m_reasoner;
    protected final OWLBGPHermiT m_hermit;
    protected final OWLDataFactory m_dataFactory;
    protected final OWLOntologyGraph m_graph;
    protected final InstanceStatistics m_instanceStatistics;
    Map<Variable, Integer> bindingPosition;

    public BindingsIntersection(OWLOntologyGraph graph, Map<Variable, Integer> bindingPositions) {
        m_reasoner = graph.getReasoner();
        m_dataFactory = graph.getOntology().getOWLOntologyManager().getOWLDataFactory();
        m_graph = graph;
        bindingPosition = bindingPositions;
        if (m_reasoner instanceof OWLBGPHermiT) {
            m_hermit = (OWLBGPHermiT) m_reasoner;
        }
        else
            throw new IllegalArgumentException("Error: The HermiT cost estimator can only be instantiated with a graph that has a (HermiT) Reasoner instance attached to it.");
        m_instanceStatistics = m_hermit.getInstanceStatistics();
    }

    public List<Atomic[]> reduceClassBindings(List<Atomic[]> candidateBindings, QO_ClassAssertion queryObject) {
        if (candidateBindings.size() == 0)
            return candidateBindings;
//	        List<Atomic[]> newBindings=new ArrayList<Atomic[]>();

        ClassAssertion axiomTemplate = (ClassAssertion) queryObject.getAxiomTemplate();
//	        Set<Variable> vars=axiomTemplate.getVariablesInSignature();
//	        Set<Variable> indVars=axiomTemplate.getIndividual().getVariablesInSignature();
//	        Variable indVar=indVars.isEmpty()?null:indVars.iterator().next();

        ClassExpression ce = axiomTemplate.getClassExpression();
        Set<Variable> ceVars = ce.getVariablesInSignature();
        Individual ind = axiomTemplate.getIndividual();
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (ceVars.isEmpty() && ind.isVariable()) {//C(?x)
            int position = bindingPosition.get(ind);
            Set<OWLNamedIndividual> known = new HashSet<OWLNamedIndividual>();
            Set<OWLNamedIndividual> possible = new HashSet<OWLNamedIndividual>();
            m_instanceStatistics.getKnownAndPossibleInstances((OWLClass) ce.asOWLAPIObject(m_dataFactory), known, possible);
            Set<Atomic> knownAndPossibleSet = new HashSet<Atomic>();
            for (OWLNamedIndividual knownInd : known) {
                knownAndPossibleSet.add(NamedIndividual.create(knownInd.getIRI().toString()));
            }
            for (OWLNamedIndividual possibleInd : possible) {
                knownAndPossibleSet.add(NamedIndividual.create(possibleInd.getIRI().toString()));
            }
            Atomic[] binding;
            for (Atomic[] bind : candidateBindings) {
                if (bind[position] == null) {
                    for (Atomic at : knownAndPossibleSet) {
                        binding = bind.clone();
                        binding[position] = at;
                        newBindings.add(binding);
                    }
                }
                else if (knownAndPossibleSet.contains(bind[position]))
                    newBindings.add(bind);
            }
            candidateBindings = newBindings;
        }
        return candidateBindings;
    }

    public List<Atomic[]> reduceObjectPropertyBindings(List<Atomic[]> candidateBindings, QO_ObjectPropertyAssertion queryObject) {
        if (candidateBindings.size() == 0)
            return candidateBindings;

        ObjectPropertyAssertion axiomTemplate = (ObjectPropertyAssertion) queryObject.getAxiomTemplate();
        Atomic ope = (Atomic) axiomTemplate.getObjectPropertyExpression();
        Individual ind1 = axiomTemplate.getIndividual1();
        Individual ind2 = axiomTemplate.getIndividual2();
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();

        if (!ope.isVariable() && ind1.isVariable() && ind2.isVariable()) {
            int[] positions = new int[2];
            positions[0] = bindingPosition.get(ind1);
            positions[1] = bindingPosition.get(ind2);
            Map<OWLNamedIndividual, Set<OWLNamedIndividual>> known = new HashMap<OWLNamedIndividual, Set<OWLNamedIndividual>>();
            Map<OWLNamedIndividual, Set<OWLNamedIndividual>> possible = new HashMap<OWLNamedIndividual, Set<OWLNamedIndividual>>();
            m_instanceStatistics.getKnownAndPossibleInstances((OWLObjectProperty) ope.asOWLAPIObject(m_dataFactory), known, possible);
            Map<Atomic, Set<Atomic>> knownAndPossibleMap = new HashMap<Atomic, Set<Atomic>>();
            for (OWLNamedIndividual individual : known.keySet()) {
                Set<Atomic> indSet = new HashSet<Atomic>();
                for (OWLNamedIndividual ind : known.get(individual))
                    indSet.add(NamedIndividual.create(ind.getIRI().toString()));
                knownAndPossibleMap.put(NamedIndividual.create((individual.getIRI()).toString()), indSet);
            }
            for (OWLNamedIndividual individual : possible.keySet()) {
                Set<Atomic> indSet = new HashSet<Atomic>();
                for (OWLNamedIndividual ind : possible.get(individual))
                    indSet.add(NamedIndividual.create(ind.getIRI().toString()));
                knownAndPossibleMap.put(NamedIndividual.create((individual.getIRI()).toString()), indSet);
            }
            for (Atomic[] bind : candidateBindings) {
                //if (!candidateBindings.isEmpty()) {
                Atomic[] binding;
                if (bind[positions[0]] == null && bind[positions[1]] == null) {
                    for (Atomic individual : knownAndPossibleMap.keySet()) {
                        for (Atomic ind : knownAndPossibleMap.get(individual)) {
                            binding = bind.clone();
                            binding[positions[0]] = individual;
                            binding[positions[1]] = ind;
                            newBindings.add(binding);
                        }
                    }
                }
                else if (bind[positions[0]] != null && bind[positions[1]] == null) {
                    Set<Atomic> sucSet = knownAndPossibleMap.get(bind[positions[0]]);
                    for (Atomic ind : sucSet) {
                        binding = bind.clone();
                        binding[positions[1]] = ind;
                        newBindings.add(binding);
                    }
                }
                else if (bind[positions[0]] == null && bind[positions[1]] != null) {
                    Set<OWLNamedIndividual> knownPredecessors = new HashSet<OWLNamedIndividual>();
                    Set<OWLNamedIndividual> possiblePredecessors = new HashSet<OWLNamedIndividual>();
                    m_instanceStatistics.getKnownAndPossiblePredeccessors((OWLObjectProperty) ope.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual) bind[positions[1]].asOWLAPIObject(m_dataFactory), knownPredecessors, possiblePredecessors);
                    for (OWLNamedIndividual indK : knownPredecessors) {
                        binding = bind.clone();
                        binding[positions[0]] = NamedIndividual.create(indK.getIRI().toString());
                        newBindings.add(binding);
                    }
                }
                else for (Atomic individual : knownAndPossibleMap.keySet()) {
                        Set<Atomic> indSet = knownAndPossibleMap.get(individual);
                        if (bind[positions[0]] == individual) {
                            if (indSet.contains(bind[positions[1]])) {
                                newBindings.add(bind);
                            }
                        }
                    }
            }
            candidateBindings = newBindings;
        }
        else if (!ope.isVariable() && !ind1.isVariable() && ind2.isVariable()) {
            int position;
            position = bindingPosition.get(ind2);
            Set<OWLNamedIndividual> knownSuccessors = new HashSet<OWLNamedIndividual>();
            Set<OWLNamedIndividual> possibleSuccessors = new HashSet<OWLNamedIndividual>();
            m_instanceStatistics.getKnownAndPossibleSuccessors((OWLObjectProperty) ope.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual) ind1.asOWLAPIObject(m_dataFactory), knownSuccessors, possibleSuccessors);
            Set<OWLNamedIndividual> knownAndPossibleSuccessors = new HashSet<OWLNamedIndividual>();
            knownAndPossibleSuccessors.addAll(knownSuccessors);
            knownAndPossibleSuccessors.addAll(possibleSuccessors);
            for (Atomic[] bind : candidateBindings) {
                Atomic[] binding;
                if (bind[position] == null) {
                    for (OWLNamedIndividual ind : knownAndPossibleSuccessors) {
                        binding = bind.clone();
                        binding[position] = NamedIndividual.create(ind.getIRI().toString());
                        newBindings.add(binding);
                    }
                }
                else if (knownAndPossibleSuccessors.contains((OWLNamedIndividual) bind[position].asOWLAPIObject(m_dataFactory))) {
                    newBindings.add(bind);
                }
            }
            candidateBindings = newBindings;
        }
        else if (!ope.isVariable() && ind1.isVariable() && !ind2.isVariable()) {
            int position;
            position = bindingPosition.get(ind1);
            Set<OWLNamedIndividual> knownPredecessors = new HashSet<OWLNamedIndividual>();
            Set<OWLNamedIndividual> possiblePredecessors = new HashSet<OWLNamedIndividual>();
            m_instanceStatistics.getKnownAndPossiblePredeccessors((OWLObjectProperty) ope.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual) ind2.asOWLAPIObject(m_dataFactory), knownPredecessors, possiblePredecessors);
            Set<OWLNamedIndividual> knownAndPossiblePredecessors = new HashSet<OWLNamedIndividual>();
            knownAndPossiblePredecessors.addAll(knownPredecessors);
            knownAndPossiblePredecessors.addAll(possiblePredecessors);
            for (Atomic[] bind : candidateBindings) {
                Atomic[] binding;
                if (bind[position] == null) {
                    for (OWLNamedIndividual ind : knownAndPossiblePredecessors) {
                        binding = bind.clone();
                        binding[position] = NamedIndividual.create(ind.getIRI().toString());
                        newBindings.add(binding);
                    }
                }
                else if (knownAndPossiblePredecessors.contains((OWLNamedIndividual) bind[position].asOWLAPIObject(m_dataFactory))) {
                    newBindings.add(bind);
                }
            }
            candidateBindings = newBindings;
        }
        return candidateBindings;
    }
}	 
