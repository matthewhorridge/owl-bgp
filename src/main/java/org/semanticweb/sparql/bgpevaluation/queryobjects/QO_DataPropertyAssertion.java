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

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;

public class QO_DataPropertyAssertion extends AbstractQueryObject<DataPropertyAssertion> {

    public QO_DataPropertyAssertion(DataPropertyAssertion axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            DataPropertyAssertion assertion = ((DataPropertyAssertion) m_axiomTemplate.getBoundVersion(bindingMap));
            Atomic dpe = (Atomic) assertion.getDataPropertyExpression();
            Individual ind = assertion.getIndividual();
            Literal lit = assertion.getLiteral();
            if (dpe.isVariable() && ind.isVariable() && lit.isVariable()) {
                // ?x(?y ?z)
                int[] positions = new int[3];
                positions[0] = bindingPositions.get(dpe);
                positions[1] = bindingPositions.get(ind);
                positions[2] = bindingPositions.get(lit);
                return compute012UnBound(currentBinding, positions);
            }
            else if (dpe.isVariable() && ind.isVariable() && !lit.isVariable()) {
                // ?x(?y lit)
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(dpe);
                positions[1] = bindingPositions.get(ind);
                return compute2Bound(currentBinding, (OWLLiteral) lit.asOWLAPIObject(m_dataFactory), positions);
            }
            else if (dpe.isVariable() && !ind.isVariable() && lit.isVariable()) {
                // ?x(:a ?z)
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(dpe);
                positions[1] = bindingPositions.get(lit);
                return compute1Bound(currentBinding, (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter), positions);
            }
            else if (dpe.isVariable() && !ind.isVariable() && !lit.isVariable()) {
                // ?x(:a lit)
                int[] positions = new int[1];
                positions[0] = bindingPositions.get(dpe);
                return compute12Bound(currentBinding, (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter), (OWLLiteral) lit.asOWLAPIObject(m_toOWLAPIConverter), positions);
            }
            else if (!dpe.isVariable() && ind.isVariable() && lit.isVariable()) {
                // dp(?y ?z)
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(ind);
                positions[1] = bindingPositions.get(lit);
                return compute0Bound(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_toOWLAPIConverter), positions);
            }
            else if (!dpe.isVariable() && ind.isVariable() && !lit.isVariable()) {
                // dp(?y lit)
                int[] positions = new int[1];
                positions[0] = bindingPositions.get(ind);
                return compute02Bound(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_toOWLAPIConverter), (OWLLiteral) lit.asOWLAPIObject(m_toOWLAPIConverter), positions);
            }
            else if (!dpe.isVariable() && !ind.isVariable() && lit.isVariable()) {
                // dp(:a ?z)
                int[] positions = new int[1];
                positions[0] = bindingPositions.get(lit);
                return compute01Bound(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter), positions);
            }
            else if (!dpe.isVariable() && !ind.isVariable() && !lit.isVariable()) {
                // dp(:a lit)
                if (compute012Bound(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter), (OWLLiteral) lit.asOWLAPIObject(m_toOWLAPIConverter)))
                    return Collections.singletonList(currentBinding);
                else
                    return new ArrayList<Atomic[]>();
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
        // DataPropertyAssertion(?x ?y ?z)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (DataProperty dp : m_graph.getDataPropertiesInSignature()) {
            OWLDataProperty owlDp = (OWLDataProperty) dp.asOWLAPIObject(m_toOWLAPIConverter);
            for (NamedIndividual ind : m_graph.getIndividualsInSignature()) {
                OWLNamedIndividual owlInd = (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter);
                for (OWLLiteral lit : m_reasoner.getDataPropertyValues(owlInd, owlDp)) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = dp;
                    binding[bindingPositions[1]] = ind;
                    binding[bindingPositions[2]] = (TypedLiteral) FromOWLAPIConverter.convert(lit);
                    newBindings.add(binding);
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute2Bound(Atomic[] currentBinding, OWLLiteral lit, int[] bindingPositions) {
        // DataPropertyAssertion(?x ?y :a)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
//        OWLOntology ont=reasoner.getRootOntology();
        for (DataProperty dp : m_graph.getDataPropertiesInSignature()) {
            OWLDataProperty owlDp = (OWLDataProperty) dp.asOWLAPIObject(m_dataFactory);
            for (NamedIndividual ind : m_graph.getIndividualsInSignature()) {
                OWLNamedIndividual owlInd = (OWLNamedIndividual) ind.asOWLAPIObject(m_dataFactory);
                if (m_reasoner.getDataPropertyValues(owlInd, owlDp).contains(lit)) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = dp;
                    binding[bindingPositions[1]] = ind;
                    newBindings.add(binding);
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute1Bound(Atomic[] currentBinding, OWLNamedIndividual ind, int[] bindingPositions) {
        // DataPropertyAssertion(?x :a ?y)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (DataProperty dp : m_graph.getDataPropertiesInSignature()) {
            OWLDataProperty owlDp = (OWLDataProperty) dp.asOWLAPIObject(m_toOWLAPIConverter);
            for (OWLLiteral lit : m_reasoner.getDataPropertyValues(ind, owlDp)) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = dp;
                binding[bindingPositions[1]] = (TypedLiteral) FromOWLAPIConverter.convert(lit);
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute12Bound(Atomic[] currentBinding, OWLNamedIndividual ind, OWLLiteral lit, int[] bindingPositions) {
        // DataPropertyAssertion(?x :a :b)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (DataProperty dp : m_graph.getDataPropertiesInSignature()) {
            OWLDataProperty owlDp = (OWLDataProperty) dp.asOWLAPIObject(m_toOWLAPIConverter);
            if (m_reasoner.getDataPropertyValues(ind, owlDp).contains(lit)) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = dp;
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute0Bound(Atomic[] currentBinding, OWLDataProperty dpe, int[] bindingPositions) {
        // DataPropertyAssertion(:r ?x ?y)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (NamedIndividual ind : m_graph.getIndividualsInSignature()) {
            OWLNamedIndividual owlInd = (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter);
            for (OWLLiteral lit : m_reasoner.getDataPropertyValues(owlInd, dpe)) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = ind;
                binding[bindingPositions[1]] = (TypedLiteral) FromOWLAPIConverter.convert(lit);
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute02Bound(Atomic[] currentBinding, OWLDataProperty dpe, OWLLiteral lit, int[] bindingPositions) {
        // DataPropertyAssertion(:r ?x :a)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Atomic[] binding;
        for (NamedIndividual ind : m_graph.getIndividualsInSignature()) {
            OWLNamedIndividual owlInd = (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter);
            if (m_reasoner.getDataPropertyValues(owlInd, dpe).contains(lit)) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = ind;
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> compute01Bound(Atomic[] currentBinding, OWLDataProperty dpe, OWLNamedIndividual ind, int[] bindingPositions) {
        // DataPropertyAssertion(:r :a ?y)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (OWLLiteral lit : m_reasoner.getDataPropertyValues(ind, dpe)) {
            binding = currentBinding.clone();
            binding[bindingPositions[0]] = (TypedLiteral) FromOWLAPIConverter.convert(lit);
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected boolean compute012Bound(Atomic[] currentBinding, OWLDataProperty dpe, OWLNamedIndividual ind, OWLLiteral lit) {
        // DataPropertyAssertion(:r :a :b)
        return (m_reasoner.getDataPropertyValues(ind, dpe).contains(lit));
    }

    public <O> O accept(DynamicQueryObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound) {
        return visitor.visit(this, bound);
    }
}
