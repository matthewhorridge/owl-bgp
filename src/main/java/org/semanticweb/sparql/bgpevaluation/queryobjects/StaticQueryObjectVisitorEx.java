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

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Variable;


public interface StaticQueryObjectVisitorEx<O> {
    O visit(QO_SubClassOf axiom, Set<Variable> bound);

    O visit(QO_EquivalentClasses axiom, Set<Variable> bound);

    O visit(QO_DisjointClasses axiom, Set<Variable> bound);

    O visit(QO_SubObjectPropertyOf axiom, Set<Variable> bound);

    //    O visit(QO_EquivalentObjectProperties axiom, Set<Variable> bound);
//    O visit(QO_DisjointObjectProperties axiom, Set<Variable> bound);
    O visit(QO_InverseObjectProperties axiom, Set<Variable> bound);

    O visit(QO_ObjectPropertyDomain axiom, Set<Variable> bound);

    O visit(QO_ObjectPropertyRange axiom, Set<Variable> bound);

    O visit(QO_FunctionalObjectProperty axiom, Set<Variable> bound);

    O visit(QO_InverseFunctionalObjectProperty axiom, Set<Variable> bound);

    O visit(QO_ReflexiveObjectProperty axiom, Set<Variable> bound);

    O visit(QO_IrreflexiveObjectProperty axiom, Set<Variable> bound);

    O visit(QO_SymmetricObjectProperty axiom, Set<Variable> bound);

    O visit(QO_AsymmetricObjectProperty axiom, Set<Variable> bound);

    O visit(QO_TransitiveObjectProperty axiom, Set<Variable> bound);

    //    O visit(QO_SubDataPropertyOf axiom, Set<Variable> bound);
//    O visit(QO_EquivalentDataProperties axiom, Set<Variable> bound);
//    O visit(QO_DisjointDataProperties axiom, Set<Variable> bound);
    O visit(QO_DataPropertyDomain axiom, Set<Variable> bound);

    O visit(QO_DataPropertyRange axiom, Set<Variable> bound);

    O visit(QO_FunctionalDataProperty axiom, Set<Variable> bound);

    //
//    O visit(QO_DatatypeDefinition axiom, Set<Variable> bound);
//    
//    O visit(QO_HasKey axiom, Set<Variable> bound);
//    
    O visit(QO_SameIndividual axiom, Set<Variable> bound);

    O visit(QO_DifferentIndividuals axiom, Set<Variable> bound);

    O visit(QO_ClassAssertion axiom, Set<Variable> bound);

    O visit(QO_ObjectPropertyAssertion axiom, Set<Variable> bound);

    O visit(QO_NegativeObjectPropertyAssertion axiom, Set<Variable> bound);

    O visit(QO_DataPropertyAssertion axiom, Set<Variable> bound);

    O visit(QO_NegativeDataPropertyAssertion axiom, Set<Variable> bound);
}