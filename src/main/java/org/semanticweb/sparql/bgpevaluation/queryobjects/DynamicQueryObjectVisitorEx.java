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

public interface DynamicQueryObjectVisitorEx<O> {
    O visit(QO_SubClassOf axiom);

    O visit(QO_EquivalentClasses axiom);

    O visit(QO_DisjointClasses axiom);

    O visit(QO_SubObjectPropertyOf axiom);

    //    O visit(QO_EquivalentObjectProperties axiom);
//    O visit(QO_DisjointObjectProperties axiom);
    O visit(QO_InverseObjectProperties axiom);

    O visit(QO_ObjectPropertyDomain axiom);

    O visit(QO_ObjectPropertyRange axiom);

    O visit(QO_FunctionalObjectProperty axiom);

    O visit(QO_InverseFunctionalObjectProperty axiom);

    O visit(QO_ReflexiveObjectProperty axiom);

    O visit(QO_IrreflexiveObjectProperty axiom);

    O visit(QO_SymmetricObjectProperty axiom);

    O visit(QO_AsymmetricObjectProperty axiom);

    O visit(QO_TransitiveObjectProperty axiom);

    //    O visit(QO_SubDataPropertyOf axiom);
//    O visit(QO_EquivalentDataProperties axiom);
//    O visit(QO_DisjointDataProperties axiom);
    O visit(QO_DataPropertyDomain axiom);

    O visit(QO_DataPropertyRange axiom);

    O visit(QO_FunctionalDataProperty axiom);

    //    O visit(QO_DatatypeDefinition axiom);
//    
//    O visit(QO_HasKey axiom);
//    
    O visit(QO_SameIndividual axiom);

    O visit(QO_DifferentIndividuals axiom);

    O visit(QO_ClassAssertion axiom);

    O visit(QO_ObjectPropertyAssertion axiom);

    O visit(QO_NegativeObjectPropertyAssertion axiom);

    O visit(QO_DataPropertyAssertion axiom);

    O visit(QO_NegativeDataPropertyAssertion axiom);
}