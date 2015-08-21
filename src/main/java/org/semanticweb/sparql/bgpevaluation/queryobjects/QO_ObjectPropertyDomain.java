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
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class QO_ObjectPropertyDomain extends AbstractQueryObject<ObjectPropertyDomain> {

    public QO_ObjectPropertyDomain(ObjectPropertyDomain axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            ObjectPropertyDomain instantiated = (ObjectPropertyDomain) m_axiomTemplate.getBoundVersion(bindingMap);
            //System.out.println(instantiated);
            ClassExpression ce = instantiated.getDomain();
            ObjectPropertyExpression ope = instantiated.getObjectPropertyExpression();
            if ((ce.isVariable()) && !ope.isVariable()) {//ObjectPropertyDomain(R ?x)
                int position = bindingPositions.get(ce);
                return computeDomains(currentBinding, ope, position);
            }
            else if (!ce.isVariable() && !ope.isVariable() && ce.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {//ObjectPropertyRange(R C)
                return checkDomain(currentBinding, (OWLObjectPropertyExpression) ope.asOWLAPIObject(m_dataFactory), (OWLClassExpression) ce.asOWLAPIObject(m_dataFactory));
            }
            else if (!ce.isVariable() && ope.isVariable() && ce.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {//ObjectPropertyRange(?p C)
                int position = bindingPositions.get(ope);
                return computePropertiesForDomains(currentBinding, (OWLClassExpression) ce.asOWLAPIObject(m_dataFactory), position);
            }
            else if (ce.isVariable() && ope.isVariable() && ce.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {//ObjectPropertyRange(?p ?x)
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(ce);
                positions[1] = bindingPositions.get(ope);
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

    protected List<Atomic[]> checkDomain(Atomic[] currentBinding, OWLObjectPropertyExpression ope, OWLClassExpression ce) {
        // ObjectPropertyRange(:op :C)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (m_reasoner.isEntailed(m_dataFactory.getOWLObjectPropertyDomainAxiom(ope, ce)))
            newBindings.add(currentBinding);
        return newBindings;
    }

    protected List<Atomic[]> computeDomains(Atomic[] currentBinding, ObjectPropertyExpression ope, int bindingPosition) {
        // ObjectPropertyRange(R :a)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        OWLObjectPropertyExpression owlope;
        if (ope instanceof ObjectProperty)
            owlope = (OWLObjectProperty) ope.asOWLAPIObject(m_toOWLAPIConverter);
        else {
            ObjectInverseOf inverseop = (ObjectInverseOf) ope;
            owlope = (OWLObjectInverseOf) inverseop.asOWLAPIObject(m_toOWLAPIConverter);
        }
        Set<OWLClass> domains = m_reasoner.getObjectPropertyDomains(owlope, false).getFlattened();
        for (OWLClass domain : domains) {
            binding = currentBinding.clone();
            binding[bindingPosition] = Clazz.create(domain.getIRI().toString());
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected List<Atomic[]> computePropertiesForDomains(Atomic[] currentBinding, OWLClassExpression classExpression, int bindingPosition) {
        // ObjectPropertyRange(?p C)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Set<OWLObjectProperty> properties = m_reasoner.getRootOntology().getObjectPropertiesInSignature(true);
        for (OWLObjectProperty op : properties) {
            Set<OWLClass> domains = m_reasoner.getObjectPropertyDomains(op, false).getFlattened();
            if (domains.contains(classExpression)) {
                binding = currentBinding.clone();
                binding[bindingPosition] = ObjectProperty.create(op.getIRI().toString());
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> computeAllPropertiesAndDomains(Atomic[] currentBinding, int[] bindingPositions) {
        // ObjectPropertyRange(?p ?x)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (OWLObjectProperty op : m_reasoner.getRootOntology().getObjectPropertiesInSignature(true)) {
            Set<OWLClass> domains = m_reasoner.getObjectPropertyDomains(op, false).getFlattened();
            for (OWLClass domain : domains) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = ObjectProperty.create(op.getIRI().toString());
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

