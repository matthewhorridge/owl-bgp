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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAxiom;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public abstract class IQO_ObjectPropertyAxiom<T extends ObjectPropertyAxiom> extends AbstractIteratorQueryObject<T> {

    protected final List<ObjectProperty> m_toTestObjectProperties;
    protected final List<ObjectProperty> m_knownResults;
    protected Integer m_bindingPosition;

    public IQO_ObjectPropertyAxiom(T axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
        m_toTestObjectProperties = new ArrayList<ObjectProperty>();
        m_knownResults = new ArrayList<ObjectProperty>(m_graph.getKnownFunctionalObjectProperties());
    }

    public void setExistingBindings(List<Atomic[]> currentBindings, Map<Variable, Integer> bindingPositions) {
        super.setExistingBindings(currentBindings, bindingPositions);
        Set<Variable> vars = m_axiomTemplate.getVariablesInSignature();
        if (!vars.isEmpty()) {
            m_bindingPosition = m_bindingPositions.get(vars.iterator().next());
        }
        else
            m_bindingPosition = null;
    }

    protected abstract ObjectPropertyExpression getObjectPropertyExpression();

    protected abstract OWLAxiom getEntailmentAxiom(OWLObjectPropertyExpression ope);

    public Iterator<Atomic[]> iterator() {
        return this;
    }

    public boolean hasNext() {
        if (m_variablesInTemplate.isEmpty()) {
            // boolean FunctionalObjectProperty(:f)
            ObjectProperty op = (ObjectProperty) getObjectPropertyExpression();
            if (m_toTestObjectProperties.contains(op)) {
                if (m_reasoner.isEntailed(getEntailmentAxiom((OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter))))
                    m_knownResults.add(op);
                m_toTestObjectProperties.remove(op);
            }
            return m_knownResults.contains(op);
        }
        else if (m_bindingPosition < 0) {
            // no binding yet
            if (m_resultIndex >= m_knownResults.size() && !m_toTestObjectProperties.isEmpty()) {
                // test next one
                boolean foundNext = false;
                while (!foundNext && !m_toTestObjectProperties.isEmpty()) {
                    ObjectProperty nextToTest = m_toTestObjectProperties.remove(m_toTestObjectProperties.size() - 1);
                    if (m_reasoner.isEntailed(getEntailmentAxiom((OWLObjectProperty) nextToTest.asOWLAPIObject(m_toOWLAPIConverter))))
                        foundNext = m_knownResults.add(nextToTest);
                }
            }
            return m_resultIndex < m_knownResults.size();
        }
        else {
            while (m_lastTestedIndex > m_resultIndex && m_resultIndex >= 0) {
                m_lastTestedIndex--;
                ObjectProperty op = (ObjectProperty) m_currentBindings.get(m_lastTestedIndex)[m_bindingPosition];
                if (m_knownResults.contains(op))
                    return true;
                else if (m_toTestObjectProperties.contains(op) && m_reasoner.isEntailed(getEntailmentAxiom((OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter)))) {
                    m_knownResults.add(op);
                    m_toTestObjectProperties.remove(op);
                    return true;
                }
                else {
                    m_currentBindings.remove(m_lastTestedIndex);
                    m_resultIndex--;
                }
            }
            return (m_lastTestedIndex <= m_resultIndex);
        }
    }

    public Atomic[] next() {
        if (!hasNext())
            return null;
        if (m_bindingPosition == null)
            return new Atomic[]{null};
        else if (m_bindingPosition < 0)
            return new Atomic[]{m_knownResults.get(m_resultIndex)};
        else
            return m_currentBindings.get(m_resultIndex);
    }

    public void revert() {
        if (m_bindingPosition != null)
            if (m_bindingPosition < 0)
                m_resultIndex = 0;
            else
                m_resultIndex = m_currentBindings.size() - 1;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
