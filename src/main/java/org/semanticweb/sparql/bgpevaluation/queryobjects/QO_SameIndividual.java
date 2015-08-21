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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;

public class QO_SameIndividual extends AbstractQueryObject<SameIndividual> {

    public QO_SameIndividual(SameIndividual axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        SameIndividual axiom = (SameIndividual) m_axiomTemplate.getBoundVersion(bindingMap);
        assert axiom.getIndividuals().size() == 2; // should be rewritten accordingly
        Iterator<Individual> indIt = axiom.getIndividuals().iterator();
        Individual ind1 = indIt.next();
        Individual ind2 = indIt.next();
        if (ind1.isVariable() && ind2.isVariable()) {
            int[] positions = new int[2];
            positions[0] = bindingPositions.get(ind1);
            positions[1] = bindingPositions.get(ind2);
            return computeAllSameAs(currentBinding, positions);
        }
        else if (ind1.isVariable() && !ind2.isVariable()) {
            int position = bindingPositions.get(ind1);
            return computeSameAs(currentBinding, position, (NamedIndividual) ind2);
        }
        else if (ind2.isVariable() && !ind1.isVariable()) {
            int position = bindingPositions.get(ind2);
            return computeSameAs(currentBinding, position, (NamedIndividual) ind1);
        }
        else
            return check(currentBinding, (NamedIndividual) ind1, (NamedIndividual) ind2);
    }

    protected List<Atomic[]> computeAllSameAs(Atomic[] currentBinding, int[] bindingPositions) {
        // SameIndividual(?x ?y)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (NamedIndividual ind : m_graph.getIndividualsInSignature()) {
            OWLNamedIndividual owlInd = (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter);
            Set<OWLNamedIndividual> sameInds = m_reasoner.getSameIndividuals(owlInd).getEntities();
            for (OWLNamedIndividual same : sameInds) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = ind;
                binding[bindingPositions[1]] = (NamedIndividual) FromOWLAPIConverter.convert(same);
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> computeSameAs(Atomic[] currentBinding, int bindingPosition, NamedIndividual ind) {
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        OWLNamedIndividual owlInd = (OWLNamedIndividual) ind.asOWLAPIObject(m_toOWLAPIConverter);
        Set<OWLNamedIndividual> sameInds = m_reasoner.getSameIndividuals(owlInd).getEntities();
        for (OWLNamedIndividual same : sameInds) {
            binding = currentBinding.clone();
            binding[bindingPosition] = (NamedIndividual) FromOWLAPIConverter.convert(same);
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected List<Atomic[]> check(Atomic[] currentBinding, NamedIndividual ind1, NamedIndividual ind2) {
        // SameIndividual(:a :b)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        OWLNamedIndividual owlInd1 = (OWLNamedIndividual) ind1.asOWLAPIObject(m_toOWLAPIConverter);
        OWLNamedIndividual owlInd2 = (OWLNamedIndividual) ind2.asOWLAPIObject(m_toOWLAPIConverter);
        Set<OWLNamedIndividual> inds = new HashSet<OWLNamedIndividual>();
        inds.add(owlInd1);
        inds.add(owlInd2);
        if (m_reasoner.isEntailed(m_dataFactory.getOWLSameIndividualAxiom(inds)))
            newBindings.add(currentBinding);
        return newBindings;
    }

    public <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound) {
        return visitor.visit(this, bound);
    }

    @Override
    public <O> O accept(DynamicQueryObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

}
