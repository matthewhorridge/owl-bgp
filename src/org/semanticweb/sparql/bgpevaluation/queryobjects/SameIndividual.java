/* Copyright 2011 by the Oxford University Computing Laboratory

   This file is part of OWL-BGP.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP. If not, see <http://www.gnu.org/licenses/>.
 */

package  org.semanticweb.sparql.bgpevaluation.queryobjects;


public class SameIndividual {
//
//	protected final OWLDataFactory dataFactory;
//	protected final OWLNamedIndividual ind1;
//	protected final OWLNamedIndividual ind2;
//	protected final int[] bindingPositions;
//	
//	public SameIndividual(List<OWLObject> initialBinding, int[] bindingPositions) {
//		assert bindingPositions.length==2;
//		assert initialBinding.size()>1;
//		assert initialBinding.get(bindingPositions[0]) instanceof OWLNamedIndividual;
//		assert initialBinding.get(bindingPositions[1]) instanceof OWLNamedIndividual;
//		this.dataFactory=OWLManager.createOWLOntologyManager().getOWLDataFactory();
//		this.ind1=(OWLNamedIndividual)initialBinding.get(bindingPositions[0]);
//		this.ind2=(OWLNamedIndividual)initialBinding.get(bindingPositions[1]);
//		this.bindingPositions=bindingPositions;
//	}
//	public List<OWLObject[]> computeBindings(Reasoner reasoner,List<OWLObject[]> candidateBindings) {
//		if (bindingPositions.length!=2)
//			throw new IllegalArgumentException("There should be two binding positions for a query atom of the form SameIndividual(?individual1 ?individual2), "
//					+"but here we got "+bindingPositions.length+" positions. ");
//		// if no solutions are computed yet, candidate bindings should have one all null array as an entry 
//		// if candidateBindings is empty, there are no solutions already due to other constraints
//		if (candidateBindings.size()==0) return candidateBindings;
//
//		List<OWLObject[]> newBindings=new ArrayList<OWLObject[]>();
//		for (int i=0;i<candidateBindings.size();i++) {
//			newBindings.addAll(addBindings(reasoner, candidateBindings.get(i), bindingPositions));
//		}
//		return newBindings;
//	}
//	
//	protected List<OWLObject[]> addBindings(Reasoner reasoner, OWLObject[] candidateBinding, int[] bindingPositions) {
//		OWLObject[] binding;
//		List<OWLObject[]> newBindings=new ArrayList<OWLObject[]>();
//		OWLObject candidateIndividual1=candidateBinding[bindingPositions[0]];
//		OWLObject candidateIndividual2=candidateBinding[bindingPositions[1]];
//		OWLNamedIndividual individual1;
//		OWLNamedIndividual individual2;
//		if ((candidateIndividual1!=null && !(candidateIndividual1 instanceof OWLNamedIndividual)) 
//				|| (candidateIndividual2 !=null && !(candidateIndividual2 instanceof OWLNamedIndividual))) {
//			// candidate is incompatible will not add new bindings in newBindings
//			return newBindings;
//		} else {
//			individual1=(OWLNamedIndividual)candidateBinding[bindingPositions[0]];
//			individual2=(OWLNamedIndividual)candidateBinding[bindingPositions[1]];
//			if (candidateIndividual1==null && candidateIndividual2==null) {
//				// SameIndividual(?x ?y)
//				for (Individual i : reasoner.getDLOntology().getAllIndividuals()) {
//					if (Prefixes.isInternalIRI(i.getIRI())) {
//						OWLNamedIndividual owlIndividual=dataFactory.getOWLNamedIndividual(IRI.create(i.getIRI()));
//						Node<OWLNamedIndividual> instances=reasoner.getSameIndividuals(owlIndividual);
//						for (OWLNamedIndividual ind : instances) {
//							binding=candidateBinding.clone();
//							binding[bindingPositions[0]]=owlIndividual;
//							binding[bindingPositions[1]]=ind;
//							newBindings.add(binding);
//						}
//					}
//				}
//			} else if (candidateIndividual1!=null && candidateIndividual2!=null) {
//				// SameIndividual(:C :a)
//				if (reasoner.hasType(individual1,dataFactory.getOWLObjectOneOf(individual2),false))
//					newBindings.add(candidateBinding);
//			} else {
//				OWLNamedIndividual knownInd;
//				int bindingIndex;
//				if (candidateIndividual1!=null) {
//					// SameIndividual(:a ?x)
//					knownInd=individual1;
//					bindingIndex=0;
//				} else {
//					// SameIndividual(?x :a)
//					knownInd=individual2;
//					bindingIndex=1;
//				}
//				Node<OWLNamedIndividual> sames=reasoner.getSameIndividuals(knownInd);
//				for (OWLNamedIndividual same : sames) {
//					binding=candidateBinding.clone();
//					binding[bindingPositions[bindingIndex]]=same;
//					newBindings.add(binding);
//				}
//			} 
//		}
//		return newBindings;
//	}
}