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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseObjectProperties;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class QO_InverseObjectProperties extends AbstractQueryObject<InverseObjectProperties> {

    public QO_InverseObjectProperties(InverseObjectProperties axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            InverseObjectProperties axiom = (InverseObjectProperties) m_axiomTemplate.getBoundVersion(bindingMap);
            Set<ObjectPropertyExpression> opeSet = axiom.getObjectPropertyExpressions();
            Iterator<ObjectPropertyExpression> itr = opeSet.iterator();
            ObjectPropertyExpression expr1 = itr.next();
            ObjectPropertyExpression expr2 = itr.next();
            if (expr1.isVariable() && expr2.isVariable()) {
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(expr1);
                positions[1] = bindingPositions.get(expr2);
                return computeAllInverseRelations(currentBinding, positions);
            }
            else if (expr1.isVariable() && !expr2.isVariable() && expr2.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {
                int position = bindingPositions.get(expr1);
                return computeInverses(currentBinding, (OWLObjectPropertyExpression) expr2.asOWLAPIObject(m_toOWLAPIConverter), position);
                /*if (expr2 instanceof ObjectProperty)
            		
            	else	
            		int position=bindingPositions.get(expr1);
                    ObjectInverseOf=(OWLObjectInverseOf)expr2.asOWLAPIObject(m_toOWLAPIConverter);    
            	return compute2ArgumentInverses(currentBinding,(OWLClassExpression)expr2.asOWLAPIObject(m_toOWLAPIConverter),position);*/
            }
            else if (expr2.isVariable() && !expr1.isVariable() && expr1.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()) {
                int position = bindingPositions.get(expr2);
                return computeInverses(currentBinding, (OWLObjectPropertyExpression) expr1.asOWLAPIObject(m_toOWLAPIConverter), position);
            }
            else if (expr1.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty() && expr2.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty())
                return checkInverseRelation(currentBinding, (OWLObjectPropertyExpression) expr1.asOWLAPIObject(m_toOWLAPIConverter), (OWLObjectPropertyExpression) expr2.asOWLAPIObject(m_toOWLAPIConverter));
            else
                return complex(currentBinding, axiom, bindingPositions);

        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }
    }

    protected List<Atomic[]> computeAllInverseRelations(Atomic[] currentBinding, int[] bindingPositions) {
        // InverseObjectProperties(?x ?y)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (ObjectProperty obprop : m_graph.getObjectPropertiesInSignature()) {
            OWLObjectProperty owlop = (OWLObjectProperty) obprop.asOWLAPIObject(m_toOWLAPIConverter);
            Set<OWLObjectPropertyExpression> invObProp = m_reasoner.getInverseObjectProperties(owlop).getEntities();
            //invObProp.addAll(m_reasoner.getEquivalentObjectProperties(owlop).getEntities());
            for (OWLObjectPropertyExpression op : invObProp) {
                if (op instanceof OWLObjectProperty) {
                    binding = currentBinding.clone();
                    binding[bindingPositions[0]] = obprop;
                    binding[bindingPositions[1]] = (ObjectProperty) FromOWLAPIConverter.convert(op);
                    newBindings.add(binding);
                }
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> computeInverses(Atomic[] currentBinding, OWLObjectPropertyExpression ope, int bindingPosition) {
        // InverseObjectProperties(?x :C)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Set<OWLObjectPropertyExpression> invObProp = m_reasoner.getInverseObjectProperties(ope).getEntities();
        //subs.addAll(m_reasoner.getEquivalentClasses(superClass).getEntities());
        for (OWLObjectPropertyExpression op : invObProp) {
            if (op instanceof OWLObjectProperty) {
                binding = currentBinding.clone();
                binding[bindingPosition] = (ObjectProperty) FromOWLAPIConverter.convert(op);
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> checkInverseRelation(Atomic[] currentBinding, OWLObjectPropertyExpression ope1, OWLObjectPropertyExpression ope2) {
        // InverseObjectProperties(:C :D)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (m_reasoner.isEntailed(m_dataFactory.getOWLInverseObjectPropertiesAxiom(ope1, ope2)))
            newBindings.add(currentBinding);
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
