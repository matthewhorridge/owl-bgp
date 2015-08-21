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


public class DifferentIndividualsNary {
//
//	protected final OWLNamedIndividual[] inds;
//	protected final Axiom axiom;
//	
//	public DifferentIndividualsNary(List<OWLObject> initialBinding, int[] bindingPositions, Axiom axiom) {
//	    super(initialBinding, bindingPositions);
//	    inds=new OWLNamedIndividual[bindingPositions.length];
//	    for (int i : bindingPositions) { 
//		  assert initialBinding.get(bindingPositions[i]) instanceof OWLNamedIndividual;
//		  this.inds[i]=(OWLNamedIndividual)initialBinding.get(bindingPositions[i]);
//	    }
//	    this.axiom=axiom;
//	}
//	
//	protected List<OWLObject[]> addBindings(Reasoner reasoner, OWLObject[] candidateBinding) {
//		OWLObject[] binding;
//		List<OWLObject[]> newBindings=new ArrayList<OWLObject[]>();
//		Map<Variable,Set<Atomic>> varToBindingSets=new HashMap<Variable, Set<Atomic>>();
//		for (int i : bindingPositions) {
//		    if (candidateBinding[i]!=null) {
//		        if (!(candidateBinding[i] instanceof OWLNamedIndividual))
//		          return newBindings;
//		        Set<Atomic> bindings=new HashSet<Atomic>();
//		        bindings.add(NamedIndividual.create(candidateBinding[i].toString()));
////		        varToBindingSets.put(  , bindings);
//		    }
//		}
//        for (Map<Variable,Atomic> bindings : new BindingIterator(varToBindingSets)) {
//            ExtendedOWLObject bound=axiom.getBoundVersion(bindings);
//            
//        }
//		return newBindings;
//	}
}
