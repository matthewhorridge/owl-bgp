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

package org.semanticweb.sparql.bgpevaluation.iteratorqueryobjects;

import java.util.Iterator;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class QO_FunctionalObjectPropertyIterator extends IQO_ObjectPropertyAxiom<FunctionalObjectProperty> implements Iterable<Atomic[]>, Iterator<Atomic[]> {

    public QO_FunctionalObjectPropertyIterator(FunctionalObjectProperty axiomTemplate, OWLOntologyGraph graph, Map<Variable, Integer> bindingPositions) {
        super(axiomTemplate, graph);
        m_toTestObjectProperties.addAll(m_graph.getToTestFunctionalObjectProperties());
        m_knownResults.addAll(m_graph.getKnownFunctionalObjectProperties());
    }

    protected OWLAxiom getEntailmentAxiom(OWLObjectPropertyExpression ope) {
        return m_dataFactory.getOWLFunctionalObjectPropertyAxiom(ope);
    }

    protected ObjectPropertyExpression getObjectPropertyExpression() {
        return m_axiomTemplate.getObjectPropertyExpression();
    }
}
