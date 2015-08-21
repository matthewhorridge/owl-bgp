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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;

public abstract class IQO_ClassAssertion extends AbstractIteratorQueryObject<ClassAssertion> {

    protected Integer m_bindingPosition;
    protected boolean m_isEntailed = false;
    protected final ClassExpression m_ce;
    protected final Individual m_ind;

    public IQO_ClassAssertion(ClassAssertion axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
        m_ce = axiomTemplate.getClassExpression();
        m_ind = axiomTemplate.getIndividual();
    }

    public Iterator<Atomic[]> iterator() {
        return this;
    }

    public boolean hasNext() {
        if (m_variablesInTemplate.isEmpty()) {
            // boolean ClassAssertion(:C :a)
            m_isEntailed = checkType((OWLClassExpression) m_ce.asOWLAPIObject(m_toOWLAPIConverter), (OWLNamedIndividual) m_ind.asOWLAPIObject(m_toOWLAPIConverter));
            return m_isEntailed;
        }
        else if (m_unboundVariables.isEmpty()) {
            while (m_lastTestedIndex > m_resultIndex && m_resultIndex >= 0) {
                m_lastTestedIndex--;
                Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
                for (Variable var : m_variablesInTemplate)
                    bindingMap.put(var, m_currentBindings.get(m_lastTestedIndex)[m_bindingPositions.get(var)]);
                OWLClassAssertionAxiom instantiated = (OWLClassAssertionAxiom) ((ClassAssertion) m_axiomTemplate.getBoundVersion(bindingMap)).asOWLAPIObject(m_toOWLAPIConverter);
                if (checkType(instantiated.getClassExpression(), (OWLNamedIndividual) instantiated.getIndividual())) {
                    return true;
                }
                else {
                    m_currentBindings.remove(m_lastTestedIndex);
                    m_resultIndex--;
                }
            }
            return (m_lastTestedIndex <= m_resultIndex);
        }
        return false;
//        if (m_bindingPosition<0) {
//            // no binding yet
//            if (m_resultIndex >= m_knownResults.size() && !m_toTestObjectProperties.isEmpty()) {
//                // test next one
//                boolean foundNext=false;
//                while (!foundNext && !m_toTestObjectProperties.isEmpty()) {
//                    ObjectProperty nextToTest=m_toTestObjectProperties.remove(m_toTestObjectProperties.size()-1);
//                    if (m_reasoner.isEntailed(getEntailmentAxiom((OWLObjectProperty)nextToTest.asOWLAPIObject(m_toOWLAPIConverter))))
//                         foundNext=m_knownResults.add(nextToTest);
//                }
//            }
//            return m_resultIndex < m_knownResults.size();
//        } else {
//
//        }
    }

    public Atomic[] next() {
//        if (!hasNext())
//            return null;
//        if (m_bindingPosition<0)
//            return new Atomic[] { m_knownResults.get(m_resultIndex) };
//        else 
//            return m_currentBindings.get(m_resultIndex);
        return null;
    }

    public void revert() {
        if (m_bindingPosition < 0)
            m_resultIndex = 0;
        else
            m_resultIndex = m_currentBindings.size() - 1;

    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected boolean checkType(OWLClassExpression classExpression, OWLNamedIndividual individual) {
        // ClassAssertion(:C :a)
        if (m_reasoner instanceof Reasoner)
            return ((Reasoner) m_reasoner).hasType(individual, classExpression, false);
        else
            return m_reasoner.getInstances(classExpression, false).containsEntity(individual);
    }
}
