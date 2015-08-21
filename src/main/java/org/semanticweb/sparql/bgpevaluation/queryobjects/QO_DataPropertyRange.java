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

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;

public class QO_DataPropertyRange extends AbstractQueryObject<DataPropertyRange> {

    public QO_DataPropertyRange(DataPropertyRange axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            DataPropertyRange instantiated = (DataPropertyRange) m_axiomTemplate.getBoundVersion(bindingMap);
            //System.out.println(instantiated);
            DataRange dr = instantiated.getRange();
            DataProperty dpe = (DataProperty) instantiated.getDataPropertyExpression();
            if (!dr.isVariable() && !dpe.isVariable()) {//DataPropertyRange(R C)
                return checkRange(currentBinding, (OWLDataProperty) dpe.asOWLAPIObject(m_dataFactory), (OWLDataRange) dr.asOWLAPIObject(m_dataFactory));
            }
            else {
                return complex(currentBinding, instantiated, bindingPositions);
            }

        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }

    }

    protected List<Atomic[]> checkRange(Atomic[] currentBinding, OWLDataProperty dp, OWLDataRange dr) {
        // ObjectPropertyRange(:op :C)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (m_reasoner.isEntailed(m_dataFactory.getOWLDataPropertyRangeAxiom(dp, dr)))
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