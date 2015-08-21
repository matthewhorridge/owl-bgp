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
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalDataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;

public class QO_FunctionalDataProperty extends AbstractQueryObject<FunctionalDataProperty> {

    public QO_FunctionalDataProperty(FunctionalDataProperty axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            FunctionalDataProperty assertion = (FunctionalDataProperty) m_axiomTemplate.getBoundVersion(bindingMap);
            DataPropertyExpression dpe = assertion.getDataPropertyExpression();
            if (dpe.isVariable()) {
                int position = bindingPositions.get(dpe);
                return compute(currentBinding, position, false);
            }
            else
                return check(currentBinding, (OWLDataPropertyExpression) dpe.asOWLAPIObject(m_toOWLAPIConverter));
        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }
    }

    protected List<Atomic[]> check(Atomic[] currentBinding, OWLDataPropertyExpression dpe) {
        if (m_reasoner.isEntailed(m_dataFactory.getOWLFunctionalDataPropertyAxiom(dpe)))
            return Collections.singletonList(currentBinding);
        else
            return new ArrayList<Atomic[]>();
    }

    protected List<Atomic[]> compute(Atomic[] currentBinding, int bindingPosition, boolean inverse) {
        // FunctionalObjectProperty(?x)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (DataProperty dp : m_graph.getDataPropertiesInSignature()) {
            if (m_reasoner.isEntailed(m_dataFactory.getOWLFunctionalDataPropertyAxiom((OWLDataProperty) dp.asOWLAPIObject(m_toOWLAPIConverter)))) {
                binding = currentBinding.clone();
                binding[bindingPosition] = DataProperty.create(dp.getIRI().toString());
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    public <O> O accept(DynamicQueryObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound) {
        return visitor.visit(this, bound);
    }
}
