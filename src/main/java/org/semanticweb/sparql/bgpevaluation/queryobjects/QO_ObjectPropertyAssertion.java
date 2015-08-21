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


package org.semanticweb.sparql.bgpevaluation.queryobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;

public class QO_ObjectPropertyAssertion extends AbstractQueryObject<ObjectPropertyAssertion> {

    public QO_ObjectPropertyAssertion(ObjectPropertyAssertion axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {

        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            ObjectPropertyAssertion assertion = ((ObjectPropertyAssertion) m_axiomTemplate.getBoundVersion(bindingMap));
//            System.out.println(assertion);
            Atomic ope = (Atomic) assertion.getObjectPropertyExpression();
            Individual ind1 = assertion.getIndividual1();
            Individual ind2 = assertion.getIndividual2();
            if (ope.isVariable() || ope instanceof Atomic) {
                if (ope.isVariable() && ind1.isVariable() && ind2.isVariable()) {
                    int[] positions = new int[3];
                    positions[0] = bindingPositions.get(ope);
                    positions[1] = bindingPositions.get(ind1);
                    positions[2] = bindingPositions.get(ind2);
                    return compute012UnBound(currentBinding, positions);
                }
                else if (ope.isVariable() && ind1.isVariable() && !ind2.isVariable()) {
                    int[] positions = new int[2];
                    positions[0] = bindingPositions.get(ope);
                    positions[1] = bindingPositions.get(ind1);
                    return compute2Bound(currentBinding, (OWLNamedIndividual) ind2.asOWLAPIObject(m_toOWLAPIConverter), positions);
                }
                else if (ope.isVariable() && !ind1.isVariable() && ind2.isVariable()) {
                    int[] positions = new int[2];
                    positions[0] = bindingPositions.get(ope);
                    positions[1] = bindingPositions.get(ind2);
                    return compute1Bound(currentBinding, (OWLNamedIndividual) ind1.asOWLAPIObject(m_toOWLAPIConverter), positions);
                }
                else if (ope.isVariable() && !ind1.isVariable() && !ind2.isVariable()) {
                    int[] positions = new int[1];
                    positions[0] = bindingPositions.get(ope);
                    return compute12Bound(currentBinding, (OWLNamedIndividual) ind1.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind2.asOWLAPIObject(m_toOWLAPIConverter), positions);
                }
                else if (!ope.isVariable() && ind1.isVariable() && ind2.isVariable()) {
                    int[] positions = new int[2];
                    positions[0] = bindingPositions.get(ind1);
                    positions[1] = bindingPositions.get(ind2);
                    return compute0Bound(currentBinding, (OWLObjectProperty) ope.asOWLAPIObject(m_toOWLAPIConverter), positions);
                }
                else if (!ope.isVariable() && ind1.isVariable() && !ind2.isVariable()) {
                    int[] positions = new int[1];
                    positions[0] = bindingPositions.get(ind1);
                    return compute02Bound(currentBinding, (OWLObjectProperty) ope.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind2.asOWLAPIObject(m_toOWLAPIConverter), positions);
                }
                else if (!ope.isVariable() && !ind1.isVariable() && ind2.isVariable()) {
                    int[] positions = new int[1];
                    positions[0] = bindingPositions.get(ind2);
                    return compute01Bound(currentBinding, (OWLObjectProperty) ope.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind1.asOWLAPIObject(m_toOWLAPIConverter), positions);
                }
                else if (!ope.isVariable() && !ind1.isVariable() && !ind2.isVariable()) {
                    if (compute012Bound((OWLObjectProperty) ope.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind1.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind2.asOWLAPIObject(m_toOWLAPIConverter)))
                        return Collections.singletonList(currentBinding);
                    else
                        return new ArrayList<Atomic[]>();
                }
                else throw new RuntimeException("There is no other case so it shouldn't have arrived here");
            }
            else {
                return complex(currentBinding, assertion, bindingPositions);
            }
        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }
    }

    protected List<Atomic[]> compute012UnBound(Atomic[] currentBinding, int[] bindingPositions) {
        // ObjectPropertyAssertion(?x ?y ?z)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (ObjectProperty op : m_graph.getObjectPropertiesInSignature()) {
            OWLObjectProperty owlOP = (OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter);
            if (m_reasoner instanceof Reasoner) {
                Map<OWLNamedIndividual, Set<OWLNamedIndividual>> instances = ((Reasoner) m_reasoner).getObjectPropertyInstances(owlOP);
                if (!instances.isEmpty()) {
                    for (OWLNamedIndividual ind1 : instances.keySet()) {
                        NamedIndividual ind = (NamedIndividual) FromOWLAPIConverter.convert(ind1);
                        for (OWLNamedIndividual ind2 : instances.get(ind1)) {
                            binding = currentBinding.clone();
                            binding[bindingPositions[0]] = op;
                            binding[bindingPositions[1]] = ind;
                            binding[bindingPositions[2]] = (NamedIndividual) FromOWLAPIConverter.convert(ind2);
                            newBindings.add(binding);
                        }
                    }
                }
            }
            else {
                for (NamedIndividual ind1 : m_graph.getIndividualsInSignature()) {
                    OWLNamedIndividual owlInd1 = (OWLNamedIndividual) ind1.asOWLAPIObject(m_toOWLAPIConverter);
                    NodeSet<OWLNamedIndividual> instances = m_reasoner.getObjectPropertyValues(owlInd1, owlOP);
                    if (!instances.isEmpty()) {
                        for (OWLNamedIndividual ind2 : instances.getFlattened()) {
                            binding = currentBinding.clone();
                            binding[bindingPositions[0]] = op;
                            binding[bindingPositions[1]] = ind1;
                            binding[bindingPositions[2]] = (NamedIndividual) FromOWLAPIConverter.convert(ind2);
                            newBindings.add(binding);
                        }
                    }
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute2Bound(Atomic[] currentBinding, OWLNamedIndividual ind, int[] bindingPositions) {
        // ObjectPropertyAssertion(?x ?y :a)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (ObjectProperty op : m_graph.getObjectPropertiesInSignature()) {
            OWLObjectProperty owlOP = (OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter);
            NodeSet<OWLNamedIndividual> instances = m_reasoner.getObjectPropertyValues(ind, owlOP.getInverseProperty());
            if (!instances.isEmpty()) {
                for (OWLNamedIndividual relatedInd : instances.getFlattened()) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = op;
                    binding[bindingPositions[1]] = (NamedIndividual) FromOWLAPIConverter.convert(relatedInd);
                    newBindings.add(binding);
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute1Bound(Atomic[] currentBinding, OWLNamedIndividual ind, int[] bindingPositions) {
        // ObjectPropertyAssertion(?x :a ?y)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (ObjectProperty op : m_graph.getObjectPropertiesInSignature()) {
            OWLObjectProperty owlOP = (OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter);
            NodeSet<OWLNamedIndividual> instances = m_reasoner.getObjectPropertyValues(ind, owlOP);
            if (!instances.isEmpty()) {
                for (OWLNamedIndividual relatedInd : instances.getFlattened()) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = op;
                    binding[bindingPositions[1]] = (NamedIndividual) FromOWLAPIConverter.convert(relatedInd);
                    newBindings.add(binding);
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute12Bound(Atomic[] currentBinding, OWLNamedIndividual ind1, OWLNamedIndividual ind2, int[] bindingPositions) {
        // ObjectPropertyAssertion(?x :a :b)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (ObjectProperty op : m_graph.getObjectPropertiesInSignature()) {
            OWLObjectProperty owlOP = (OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter);
            if (compute012Bound(owlOP, ind1, ind2)) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = op;
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute0Bound(Atomic[] currentBinding, OWLObjectProperty ope, int[] bindingPositions) {
        // ObjectPropertyAssertion(:r ?x ?y)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        if (m_reasoner instanceof Reasoner) {
            Map<OWLNamedIndividual, Set<OWLNamedIndividual>> instances = ((Reasoner) m_reasoner).getObjectPropertyInstances(ope);
            for (OWLNamedIndividual ind1 : instances.keySet())
                for (OWLNamedIndividual ind2 : instances.get(ind1)) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = (NamedIndividual) FromOWLAPIConverter.convert(ind1);
                    binding[bindingPositions[1]] = (NamedIndividual) FromOWLAPIConverter.convert(ind2);
                    newBindings.add(binding);
                }
        }
        else {
            for (OWLNamedIndividual ind1 : m_reasoner.getRootOntology().getIndividualsInSignature(true)) {
                NodeSet<OWLNamedIndividual> instances = m_reasoner.getObjectPropertyValues(ind1, ope);
                for (OWLNamedIndividual ind2 : instances.getFlattened()) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = (NamedIndividual) FromOWLAPIConverter.convert(ind1);
                    binding[bindingPositions[1]] = (NamedIndividual) FromOWLAPIConverter.convert(ind2);
                    newBindings.add(binding);
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute02Bound(Atomic[] currentBinding, OWLObjectProperty ope, OWLNamedIndividual ind, int[] bindingPositions) {
        // ObjectPropertyAssertion(:r ?x :a)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        NodeSet<OWLNamedIndividual> instances = m_reasoner.getObjectPropertyValues(ind, ope.getInverseProperty());
        for (OWLNamedIndividual relatedInd : instances.getFlattened()) {
            binding = currentBinding.clone();
            binding[bindingPositions[0]] = (NamedIndividual) FromOWLAPIConverter.convert(relatedInd);
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected List<Atomic[]> compute01Bound(Atomic[] currentBinding, OWLObjectPropertyExpression ope, OWLNamedIndividual ind, int[] bindingPositions) {
        // ObjectPropertyAssertion(:r :a ?x)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        NodeSet<OWLNamedIndividual> instances = m_reasoner.getObjectPropertyValues(ind, ope);
        for (OWLNamedIndividual relatedInd : instances.getFlattened()) {
            binding = currentBinding.clone();
            binding[bindingPositions[0]] = (NamedIndividual) FromOWLAPIConverter.convert(relatedInd);
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected boolean compute012Bound(OWLObjectProperty ope, OWLNamedIndividual ind1, OWLNamedIndividual ind2) {
        // ObjectPropertyAssertion(:r :a :b)
        if (m_reasoner instanceof Reasoner)
            return ((Reasoner) m_reasoner).hasObjectPropertyRelationship(ind1, ope, ind2);
        else
            return m_reasoner.getObjectPropertyValues(ind1, ope).containsEntity(ind2);
    }
    
/*    protected List<Atomic[]> filteringPass(Atomic[] currentBinding, Map<Variable,Integer> bindingPositions){
        Map<Variable,Atomic> bindingMap=new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            ObjectPropertyAssertion assertion=((ObjectPropertyAssertion)m_axiomTemplate.getBoundVersion(bindingMap));
//            System.out.println(assertion);
            Atomic ope=(Atomic)assertion.getObjectPropertyExpression();
            Individual ind1=assertion.getIndividual1();
            Individual ind2=assertion.getIndividual2();

            if (!ope.isVariable() && ind1.isVariable() && ind2.isVariable()) {//:r(?x,?y)
                int[] positions=new int[2];
                positions[0]=bindingPositions.get(ind1);
                positions[1]=bindingPositions.get(ind2);
                return [getKnownAndPossibleInstances]
                //compute0Bound(currentBinding,(OWLObjectProperty)ope.asOWLAPIObject(m_toOWLAPIConverter),positions);
            } else if (!ope.isVariable() && ind1.isVariable() && !ind2.isVariable()) {//:r(?x,:a)
                int[] positions=new int[1];
                positions[0]=bindingPositions.get(ind1);
                return [getKnownAndPossiblePredecessors] 
                //compute02Bound(currentBinding,(OWLObjectProperty)ope.asOWLAPIObject(m_toOWLAPIConverter),(OWLNamedIndividual)ind2.asOWLAPIObject(m_toOWLAPIConverter),positions);
            } else if (!ope.isVariable() && !ind1.isVariable() && ind2.isVariable()) {//:r(:a,?x)
                int[] positions=new int[1];
                positions[0]=bindingPositions.get(ind2);
                return compute01Bound(currentBinding,(OWLObjectProperty)ope.asOWLAPIObject(m_toOWLAPIConverter),(OWLNamedIndividual)ind1.asOWLAPIObject(m_toOWLAPIConverter),positions);
            } else if (!ope.isVariable() && !ind1.isVariable() && !ind2.isVariable()) {//:r(:a,:b)
                if (ind1,ind2 isKnownOrPossibleInstance)
                	return Collections.singletonList(currentBinding);
                //(compute012Bound((OWLObjectProperty)ope.asOWLAPIObject(m_toOWLAPIConverter),(OWLNamedIndividual)ind1.asOWLAPIObject(m_toOWLAPIConverter),(OWLNamedIndividual)ind2.asOWLAPIObject(m_toOWLAPIConverter)))
                //    return Collections.singletonList(currentBinding);
                else 
                    return new ArrayList<Atomic[]>();
            } else {
                return complex(currentBinding,assertion,bindingPositions);
            }
        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }
    	
    }*/

    public <O> O accept(DynamicQueryObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound) {
        return visitor.visit(this, bound);
    }
}
