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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;

public class QO_DataPropertyDomain extends AbstractQueryObject<DataPropertyDomain> {

    public QO_DataPropertyDomain(DataPropertyDomain axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            DataPropertyDomain instantiated = (DataPropertyDomain) m_axiomTemplate.getBoundVersion(bindingMap);
            //System.out.println(instantiated);
            ClassExpression ce = instantiated.getDomain();
            DataProperty dpe = (DataProperty) instantiated.getDataPropertyExpression();
            if ((ce.isVariable()) && !dpe.isVariable()) {//DataPropertyDomain(R ?x)
                int position = bindingPositions.get(ce);
                return computeDomains(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_dataFactory), position);
            }
            else if (!ce.isVariable() && !dpe.isVariable() && ce.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {//DataPropertyDomain(R C)
                return checkDomain(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_dataFactory), (OWLClassExpression) ce.asOWLAPIObject(m_dataFactory));
            }
            else if (!ce.isVariable() && dpe.isVariable() && ce.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {//DataPropertyDomain(?p C)
                int position = bindingPositions.get(dpe);
                return computePropertiesForDomains(currentBinding, (OWLClassExpression) ce.asOWLAPIObject(m_dataFactory), position);
            }
            else if (ce.isVariable() && dpe.isVariable()) {//DataPropertyDomain(?p ?x)
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(ce);
                positions[1] = bindingPositions.get(dpe);
                return computeAllPropertiesAndDomains(currentBinding, positions);
            }
            else {
                return complex(currentBinding, instantiated, bindingPositions);
            }

        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }

    }

    protected List<Atomic[]> checkDomain(Atomic[] currentBinding, OWLDataProperty dp, OWLClassExpression ce) {
        // ObjectPropertyDomain(:op :C)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (m_reasoner.isEntailed(m_dataFactory.getOWLDataPropertyDomainAxiom(dp, ce)))
            newBindings.add(currentBinding);
        return newBindings;
    }

    protected List<Atomic[]> computeDomains(Atomic[] currentBinding, OWLDataProperty dp, int bindingPosition) {
        // DataPropertyDomain(R :a)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Set<OWLClass> domains = m_reasoner.getDataPropertyDomains(dp, false).getFlattened();
        for (OWLClass domain : domains) {
            binding = currentBinding.clone();
            binding[bindingPosition] = Clazz.create(domain.getIRI().toString());
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected List<Atomic[]> computePropertiesForDomains(Atomic[] currentBinding, OWLClassExpression classExpression, int bindingPosition) {
        // DataPropertyDomain(?p C)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Set<OWLDataProperty> properties = m_reasoner.getRootOntology().getDataPropertiesInSignature(true);
        for (OWLDataProperty op : properties) {
            Set<OWLClass> domains = m_reasoner.getDataPropertyDomains(op, false).getFlattened();
            if (domains.contains(classExpression)) {
                binding = currentBinding.clone();
                binding[bindingPosition] = DataProperty.create(op.getIRI().toString());
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> computeAllPropertiesAndDomains(Atomic[] currentBinding, int[] bindingPositions) {
        // DataPropertyDomain(?p ?x)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (OWLDataProperty op : m_reasoner.getRootOntology().getDataPropertiesInSignature(true)) {
            Set<OWLClass> domains = m_reasoner.getDataPropertyDomains(op, false).getFlattened();
            for (OWLClass domain : domains) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = DataProperty.create(op.getIRI().toString());
                binding[bindingPositions[1]] = Clazz.create(domain.getIRI().toString());
                newBindings.add(binding);
            }
        }
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

