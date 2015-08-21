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

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAxiom;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public abstract class QO_ObjectPropertyAxiom<T extends ObjectPropertyAxiom> extends AbstractQueryObject<T> {

    public QO_ObjectPropertyAxiom(T axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            @SuppressWarnings("unchecked")
            T assertion = (T) m_axiomTemplate.getBoundVersion(bindingMap);
            ObjectPropertyExpression ope = assertion.getObjectPropertyExpression().getNormalized();
            if (ope.isVariable()) {
                int position = bindingPositions.get(ope);
                return compute(currentBinding, position, false);
            }
            else if (ope instanceof ObjectInverseOf) {
                ObjectInverseOf inv = (ObjectInverseOf) ope;
                if (inv.getInvertedObjectProperty().isVariable()) {
                    int position = bindingPositions.get(ope);
                    return compute(currentBinding, position, true);
                }
            }
            return check(currentBinding, (OWLObjectPropertyExpression) ope.asOWLAPIObject(m_dataFactory));
        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }
    }

    protected abstract OWLAxiom getEntailmentAxiom(OWLObjectPropertyExpression ope);

    protected List<Atomic[]> check(Atomic[] currentBinding, OWLObjectPropertyExpression ope) {
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (m_reasoner.isEntailed(getEntailmentAxiom(ope)))
            newBindings.add(currentBinding);
        return newBindings;
    }

    protected List<Atomic[]> compute(Atomic[] currentBinding, int bindingPosition, boolean inverse) {
        // FunctionalObjectProperty(?x)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (OWLObjectProperty op : m_reasoner.getRootOntology().getObjectPropertiesInSignature(true)) {
            OWLObjectPropertyExpression ope = inverse ? m_dataFactory.getOWLObjectInverseOf(op) : op;
            if (m_reasoner.isEntailed(getEntailmentAxiom(ope))) {
                binding = currentBinding.clone();
                binding[bindingPosition] = ObjectProperty.create(op.getIRI().toString());
                newBindings.add(binding);
            }
        }
        return newBindings;
    }
}
