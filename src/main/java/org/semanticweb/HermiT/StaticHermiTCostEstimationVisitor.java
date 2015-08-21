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
package  org.semanticweb.HermiT;

import java.util.Set;

import org.semanticweb.HermiT.hierarchy.InstanceStatistics;
import org.semanticweb.HermiT.hierarchy.InstanceStatistics.RoleInstanceStatistics;
import org.semanticweb.HermiT.model.DLClause;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.StaticCostEstimationVisitor;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class StaticHermiTCostEstimationVisitor extends StaticCostEstimationVisitor {
	protected double POSSIBLE_INSTANCE_SUCCESS=0.5;
	protected final OWLBGPHermiT m_hermit;
	protected final InstanceStatistics m_instanceStatistics;
	protected final double m_numDisjunctions;
	protected Integer m_classHierarchyDepth;
	protected Integer m_opHierarchyDepth;
	    
	public StaticHermiTCostEstimationVisitor(OWLOntologyGraph graph) {
		super(graph);
	    if (m_reasoner instanceof OWLBGPHermiT) {
	    	m_hermit=(OWLBGPHermiT)m_reasoner;
	        m_instanceStatistics=m_hermit.getInstanceStatistics();
	        double numDisjunctions=0;
	        for (DLClause clause : m_hermit.getDLOntology().getDLClauses()){
                //System.out.println(clause);
	        	if (clause.getHeadLength()>1) 
	        		numDisjunctions+=clause.getHeadLength();	
	        }
	        m_numDisjunctions=numDisjunctions;
	    } else 
	    	throw new IllegalArgumentException("Error: The HermiT cost estimator can only be instantiated with a graph that has a (HermiT) Reasoner instance attached to it.");
	    COST_ENTAILMENT=(double)EntailmentLookUpCostEstimation.entailmentCost;
        COST_LOOKUP=(double)EntailmentLookUpCostEstimation.lookUpCost;
	    //COST_ENTAILMENT=100;
        //COST_LOOKUP=1;
	    
	}
	
/*	protected double[] getSubClassOfCost(ClassExpression subce, ClassExpression superce, Set<Variable> boundVar) {
		if (subce.isVariable() && !boundVar.contains(subce)){ 
    		if (superce.isVariable() && !boundVar.contains(superce)){ //SubClassOf(?x ?y)
    			double[] costMatrix={0.0,0.0};
    		    for (Clazz clas : m_graph.getClassesInSignature()) {
    		    	int[] estimate=m_instanceStatistics.getNumberOfSubClasses((OWLClass)superce.asOWLAPIObject(m_dataFactory));
                    costMatrix[0]+=estimate[0]*COST_LOOKUP;
                    costMatrix[1]+=estimate[0];
                }   
    		return costMatrix;
		    }
    		else if (superce.isVariable() && boundVar.contains(superce)) //SubClassOf(?x :C) <- SubClassOf(?x ?y)
    			return new double[] { m_classCount*COST_LOOKUP, m_classCount };
    		else //SubClassOf(?x :C) 
    			int[] estimate=m_instanceStatistics.getNumberOfSubClasses((OWLClass)superce.asOWLAPIObject(m_dataFactory));
    		    return new double[] { estimate[0]*COST_LOOKUP, estimate[0]};
    	}	
    	else if (subce.isVariable() && boundVar.contains(subce) ) {
    		if (superce.isVariable() && !boundVar.contains(superce)) //SubClassOf(:C ?y) <- SubClassOf(?x ?y)
    			return new double[] { m_classCount*COST_LOOKUP, m_classCount };
    		else //SubClass(:C :D) <- SubClassOf(?x ?y)
    			return new double[] { COST_LOOKUP, 1};
    	}
    	else {
    		if (superce.isVariable() && !boundVar.contains(superce)) //SubClassOf(:C ?y) 
    			int[] estimate=m_instanceStatistics.getNumberOfSuperClasses((OWLClass)subrce.asOWLAPIObject(m_dataFactory));
    		    return new double[] { estimate[0]*COST_LOOKUP, estimate[0]};
    		else //SubClassOf(:C :D) <- SubClassOf(:C ?y) 
    			return new double[] { COST_LOOKUP, 1};
    		else //SubClassOf(:C :D)
    		    if isSubClass(subce, superce)
    		        return new double[] { COST_LOOKUP, 1};
    		    else return new double[] { COST_LOOKUP, 0};    
    	}
	}
*/	
    protected double[] getClassAssertionCost(ClassExpression ce, Individual ind, Set<Variable> bound, Variable indVar) {
    	double cost=0;	        
//	        if (ce instanceof Atomic && (m_instanceStatistics==null || !m_instanceStatistics.areClassesInitialised()))
//	            cost+=(m_classCount*m_indCount*COST_LOOKUP+COST_ENTAILMENT); // initialization required
	    if (ce instanceof Variable && !bound.contains(ce)) { 
	    	if (indVar==null) {//?x(a)
	    		int[] estimate=m_instanceStatistics.getNumberOfTypes((OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory));
	    		return new double[] { cost+estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), estimate[0]+(POSSIBLE_INSTANCE_SUCCESS*estimate[1])};
	    	}	
	    	else if (bound.contains(indVar)) {//?x(a)<-?x(?y)
	    		double[] costMatrix={0.0,0.0};
	    		for (Clazz clas : m_graph.getClassesInSignature()) {
	    			int[] estimate=m_instanceStatistics.getNumberOfInstances((OWLClass)clas.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
	                costMatrix[1]+=(estimate[0]/(double)m_indCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount);
	            }
	    		return costMatrix;
	    		//return new double[] { cost+m_classCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_classCount};
	    	}
	    	else {//?x(?y)
	    		double[] costMatrix={0.0,0.0};
	    		for (Clazz clas : m_graph.getClassesInSignature()) {
	    			int[] estimate=m_instanceStatistics.getNumberOfInstances((OWLClass)clas.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
	                costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
	            }
	    		return costMatrix;
	    	}
	    }
	    else if (ce instanceof Variable && bound.contains(ce)) {
	    	if (indVar==null) {//C(a)<-?x(a)
	    		 int[] estimate=m_instanceStatistics.getNumberOfTypes((OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory));
	             return new double[] { cost + (estimate[0]/(double)(m_classCount))*COST_LOOKUP+(estimate[1]/(double)(m_classCount))*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), (estimate[0]/(double)(m_classCount))+(POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)(m_classCount))) };
	    	}	
	    	else if (bound.contains(indVar)) {//C(a)<-?x(?y)
	    		double[] costMatrix={0.0,0.0};
	    		for (Clazz clas : m_graph.getClassesInSignature()) {
	    			int[] estimate=m_instanceStatistics.getNumberOfInstances((OWLClass)clas.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=(estimate[0]/(double)(m_classCount*m_indCount))*COST_LOOKUP+(estimate[1]/(double)(m_classCount*m_indCount))*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
	                costMatrix[1]+=estimate[0]/(double)(m_classCount*m_indCount)+POSSIBLE_INSTANCE_SUCCESS*estimate[1]/(double)(m_classCount*m_indCount);
	            }
	    		return costMatrix;
	    		//return new double[] { cost+COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), 1};
	    	}
	    	else {//C(?y)<-?x(?y)
	    		double[] costMatrix={0.0,0.0};
	    		for (Clazz clas : m_graph.getClassesInSignature()) {
	    			int[] estimate=m_instanceStatistics.getNumberOfInstances((OWLClass)clas.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=(estimate[0]/(double)m_classCount)*COST_LOOKUP+(estimate[1]/(double)m_classCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
	                costMatrix[1]+=(estimate[0]/(double)m_classCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)(m_classCount));
	            }
	    		return costMatrix;
	    		//return new double[] { cost+m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_indCount};
	    	}
	    }
	    else {
	    	//if (ce instanceof Atomic) {
	    		if (indVar!=null && !bound.contains(indVar)){ //C(?x)
	    			int[] estimate=m_instanceStatistics.getNumberOfInstances((OWLClass)ce.asOWLAPIObject(m_dataFactory));
	    		    //System.out.println("known: "+estimate[0]+"  possible: "+estimate[1]);
	                return new double[] { cost+estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), estimate[0]+(POSSIBLE_INSTANCE_SUCCESS*estimate[1]) };
	            }    
	            else if (indVar!=null && bound.contains(indVar)) {//C(a)<-C(x)
	        	    int[] estimate=m_instanceStatistics.getNumberOfInstances((OWLClass)ce.asOWLAPIObject(m_dataFactory));
	                return new double[] { cost+(estimate[0]/(double)m_indCount)*COST_LOOKUP + (estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), (estimate[0]+estimate[1]*POSSIBLE_INSTANCE_SUCCESS)/(double)m_indCount};
	            }
	            else {//C(a)
	        	    boolean[] result=m_instanceStatistics.isKnownOrPossibleInstance((OWLClass)ce.asOWLAPIObject(m_dataFactory),(OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory)); 
	                if (result[0])
	            	    return new double[] { cost+COST_LOOKUP, 1 };
	                else if (result[1])
	            	    return new double[] { cost+((double)0.5*COST_ENTAILMENT*m_instanceStatistics.getClassHierarchyDepth()), 1*POSSIBLE_INSTANCE_SUCCESS };
	                else return new double[] { cost+COST_LOOKUP, 0};   
	            }
	        //}
	    }	
	     //return new double[] {0.0, 0.0};
	}
	protected double[] getObjectPropertyAssertionCost(ObjectPropertyExpression op, Individual ind1, Individual ind2, Set<Variable> bound, Variable opVar) {
		double cost=0;
//	        if (m_instanceStatistics==null || !m_instanceStatistics.arePropertiesInitialised())
//	            cost+=(m_opCount*m_indCount*COST_LOOKUP+COST_ENTAILMENT); // initialization required
		
		if (opVar!=null && ind1 instanceof Variable && ind2 instanceof Variable) {
			if (!bound.contains(opVar) && !bound.contains(ind1) && !bound.contains(ind2)) {//?z(?x ?y)
				double[] costMatrix={0.0,0.0};
	        	for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
	            	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=roleStatistics.getNumberOfKnownInstances()*COST_LOOKUP+roleStatistics.getNumberOfPossibleInstances()*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
	                costMatrix[1]+=roleStatistics.getNumberOfKnownInstances()+POSSIBLE_INSTANCE_SUCCESS*roleStatistics.getNumberOfPossibleInstances();
	            }
	            return costMatrix;        		
			}
			else if (!bound.contains(opVar) && !bound.contains(ind1) && bound.contains(ind2)) {//?z(?x,b)<-?z(?x ?y)
				double[] costMatrix={0.0,0.0};
			    for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
			    	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
			        if (roleStatistics.getNumberOfDistinctKnownSuccessors()!=0 && roleStatistics.getNumberOfDistinctPossibleSuccessors()!=0) {
			        	costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())*COST_LOOKUP+ (roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
			            costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())+POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors());   
			        }
			        else if (roleStatistics.getNumberOfDistinctPossibleSuccessors()!=0) {
			        	costMatrix[0]+=(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
			            costMatrix[1]+=POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors());
		            } 
			        else if (roleStatistics.getNumberOfDistinctKnownSuccessors()!=0) {
			        	costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())*COST_LOOKUP; 
			            costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors());
			        }
			        else {
			        	costMatrix[0]=0.0;
			            costMatrix[1]=0.0;
                    }
		        }
			    return costMatrix;
			    //return new double[] {cost+m_opCount*m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_opCount*m_indCount};	
			}
			else if (!bound.contains(opVar) && bound.contains(ind1) && !bound.contains(ind2)) {//?z(a ?y)<-?z(?x ?y) 
				double[] costMatrix={0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
					if (roleStatistics.getNumberOfDistinctKnownPredecessors()!=0 && roleStatistics.getNumberOfDistinctPossiblePredecessors()!=0) {
					    costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())*COST_LOOKUP+ (roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
					    costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())+POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors());   
					}
					else if (roleStatistics.getNumberOfDistinctPossiblePredecessors()!=0){
					    costMatrix[0]+=(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
					    costMatrix[1]+=POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors());
				    } 
					else if (roleStatistics.getNumberOfDistinctKnownPredecessors()!=0) {
					    costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())*COST_LOOKUP; 
					    costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors());
					}
					else {
		                costMatrix[0]=0.0;
		                costMatrix[1]=0.0;
		            }
				}
				return costMatrix;
				//return new double[] {cost+m_opCount*m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_opCount*m_indCount};	
			}	
			else if (bound.contains(opVar) && !bound.contains(ind1) && !bound.contains(ind2)) {//R(?x,?y)<-?z(?x ?y)
				double[] costMatrix={0.0,0.0};
	        	for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
	            	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=roleStatistics.getNumberOfKnownInstances()*COST_LOOKUP+roleStatistics.getNumberOfPossibleInstances()*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
	                costMatrix[1]+=roleStatistics.getNumberOfKnownInstances()+POSSIBLE_INSTANCE_SUCCESS*roleStatistics.getNumberOfPossibleInstances();
	            }
	        	costMatrix[0]=costMatrix[0]/(double)m_opCount;
	        	costMatrix[1]=costMatrix[1]/(double)m_opCount;
	            return costMatrix;
				//return new double[] {cost+m_indCount*m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_indCount*m_indCount};	
			}
			else if (!bound.contains(opVar) && bound.contains(ind1) && bound.contains(ind2)) {//?z(a b)<-?z(?x ?y) 
				double[] costMatrix={0.0,0.0};
	        	for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
	            	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=roleStatistics.getNumberOfKnownInstances()*COST_LOOKUP+roleStatistics.getNumberOfPossibleInstances()*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
	                costMatrix[1]+=roleStatistics.getNumberOfKnownInstances()+POSSIBLE_INSTANCE_SUCCESS*roleStatistics.getNumberOfPossibleInstances();
	            }
	        	costMatrix[0]=costMatrix[0]/(double)(m_indCount * m_indCount);
	        	costMatrix[1]=costMatrix[1]/(double)(m_indCount * m_indCount);
	            return costMatrix;
				//return new double[] {cost+m_opCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_opCount};	
			}
			else if (bound.contains(opVar) && bound.contains(ind1) && !bound.contains(ind2)) {//?R(a ?y)<-?z(?x ?y)
				double[] costMatrix={0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
				    if (roleStatistics.getNumberOfDistinctKnownPredecessors()!=0 && roleStatistics.getNumberOfDistinctPossiblePredecessors()!=0) {
				    	costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())*COST_LOOKUP+ (roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
				        costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())+POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors());   
				    }
				    else if (roleStatistics.getNumberOfDistinctPossiblePredecessors()!=0){
				    	costMatrix[0]+=(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
				    	costMatrix[1]+=POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors());
			        } 
				    else if (roleStatistics.getNumberOfDistinctKnownPredecessors()!=0) {
				    	costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())*COST_LOOKUP; 
				    	costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors());
				    }
				    else {
	                	costMatrix[0]=0.0;
	                	costMatrix[1]=0.0;
	                }
	            }
	        	costMatrix[0]=costMatrix[0]/(double)(m_opCount);
	        	costMatrix[1]=costMatrix[1]/(double)(m_opCount);
	            return costMatrix;
				//return new double[] {cost+m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_indCount};	
			}
			else if (bound.contains(opVar) && !bound.contains(ind1) && bound.contains(ind2)) {//R(?x b)<-?z(?x ?y)
				double[] costMatrix={0.0,0.0};
			    for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
			    	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
			        if (roleStatistics.getNumberOfDistinctKnownSuccessors()!=0 && roleStatistics.getNumberOfDistinctPossibleSuccessors()!=0) {
			        	costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())*COST_LOOKUP+ (roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
			            costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())+POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors());   
			        }
			        else if (roleStatistics.getNumberOfDistinctPossibleSuccessors()!=0) {
			        	costMatrix[0]+=(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
			            costMatrix[1]+=POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors());
		            } 
			        else if (roleStatistics.getNumberOfDistinctKnownSuccessors()!=0) {
			        	costMatrix[0]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())*COST_LOOKUP; 
			            costMatrix[1]+=(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors());
			        }
			        else {
			        	costMatrix[0]=0.0;
			            costMatrix[1]=0.0;
                    }
		        }
	        	costMatrix[0]=costMatrix[0]/(double)(m_opCount);
	        	costMatrix[1]=costMatrix[1]/(double)(m_opCount);
	            return costMatrix;
				//return new double[] {cost+m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_indCount};	
			}
			else if (bound.contains(opVar) && bound.contains(ind1) && bound.contains(ind2)) {//R(a b)<-?z(?x ?y)
				double[] costMatrix={0.0,0.0};
	        	for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
	            	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=roleStatistics.getNumberOfKnownInstances()*COST_LOOKUP+roleStatistics.getNumberOfPossibleInstances()*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
	                costMatrix[1]+=roleStatistics.getNumberOfKnownInstances()+POSSIBLE_INSTANCE_SUCCESS*roleStatistics.getNumberOfPossibleInstances();
	            }
	        	costMatrix[0]=costMatrix[0]/(double)(m_opCount * m_indCount * m_indCount);
	        	costMatrix[1]=costMatrix[1]/(double)(m_opCount * m_indCount * m_indCount);
	            return costMatrix;
				//return new double[] {cost+COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), 1};	
			}
        }
		else if (opVar!=null && ind1 instanceof Variable) {
			if (!bound.contains(opVar) && !bound.contains(ind1)) {//?z(?x b)
				double[] costMatrix= {0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					int[] estimate=m_instanceStatistics.getNumberOfPredecessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
					costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
			        costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
		        }	
	            return costMatrix;
		    }
		    else if (bound.contains(opVar) && !bound.contains(ind1)) {//R(?x b)<-?z(?x b)
			    //return new double[] {cost+m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_indCount};	
			    double[] costMatrix= {0.0,0.0};
			    for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
				    int[] estimate=m_instanceStatistics.getNumberOfPredecessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
			        costMatrix[0]+=(estimate[0]/(double)m_opCount)*COST_LOOKUP+(estimate[1]/(double)m_opCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		            costMatrix[1]+=(estimate[0]/(double)m_opCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_opCount);
			    }
		        return costMatrix;
		    }
		    else if (!bound.contains(opVar) && bound.contains(ind1)) {//?z(a b)<-?z(?y b))
			    //return new double[] {cost+m_opCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_opCount};
			    double[] costMatrix= {0.0,0.0};
			    for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
				    int[] estimate=m_instanceStatistics.getNumberOfPredecessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
			        costMatrix[0]+=(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		            costMatrix[1]+=(estimate[0]/(double)m_indCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount);
			    }
			    return costMatrix;
		    }
		    else {//R(a b)<-?z(?y b) 
			    //return new double[] {cost+COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), 1};
			    double[] costMatrix= {0.0,0.0};
			    for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
				    int[] estimate=m_instanceStatistics.getNumberOfPredecessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
			        costMatrix[0]+=(estimate[0]/(double)m_opCount*m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_opCount*m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		            costMatrix[1]+=(estimate[0]/(double)m_opCount*m_indCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_opCount*m_indCount);
			    }
			    return costMatrix;
	        }
	    }
		else if (opVar!=null && ind2 instanceof Variable) {
			if (!bound.contains(opVar) && !bound.contains(ind2)) {//?z(a ?y)
				double[] costMatrix= {0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					int[] estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));
					 costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		             costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
				}	
                return costMatrix;
			}
			else if (bound.contains(opVar) && !bound.contains(ind2)) {//R(a ?y)<-?z(a ?y)
            	//return new double[] {cost+m_indCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_indCount};
				double[] costMatrix= {0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					int[] estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));
					 costMatrix[0]+=(estimate[0]/(double)m_opCount)*COST_LOOKUP+(estimate[1]/(double)m_opCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		             costMatrix[1]+=(estimate[0]/(double)m_opCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_opCount);
				}
			    return costMatrix;
				
			}
			else if (!bound.contains(opVar) && bound.contains(ind1)) {//?z(a b)<-?z(a ?y))
				//return new double[] {cost+m_opCount*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), m_opCount};
				double[] costMatrix= {0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					int[] estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));
					 costMatrix[0]+=(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		             costMatrix[1]+=(estimate[0]/(double)m_indCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount);
				}
			    return costMatrix;
			}
			else {//R(a b)<-?z(a ?y)
				//return new double[] {cost+COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), 1};
				double[] costMatrix= {0.0,0.0};
				for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
					int[] estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));
					 costMatrix[0]+=(estimate[0]/(double)m_opCount*m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_opCount*m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth();
		             costMatrix[1]+=(estimate[0]/(double)m_opCount*m_indCount)+POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_opCount*m_indCount);
				}
			    return costMatrix;
			}
		}
		else if (opVar!=null) {//?z(a b) 
			double[] costMatrix = {0.0, 0.0};
			for (ObjectProperty prop : m_graph.getObjectPropertiesInSignature()) {
				boolean[] result=m_instanceStatistics.hasSuccessor((OWLObjectProperty)prop.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
	            if (result[0]) {
	            	costMatrix[0]+=COST_LOOKUP;
	                costMatrix[1]+=1;
	            }    
	            else if (result[1]) {
	        	   costMatrix[0]+=(double)0.5*COST_ENTAILMENT*m_instanceStatistics.getObjectPropertyHierarchyDepth();
	               costMatrix[1]=1*POSSIBLE_INSTANCE_SUCCESS;
	            }
	            else {
	            	costMatrix[0]+=COST_LOOKUP;
	            	costMatrix[1]+=0;
	            }
		    }
			return costMatrix;
		}
		else if (opVar==null && ind1 instanceof Variable && ind2 instanceof Variable) {// op(?x ?y)
        	if (!bound.contains(ind1) && !bound.contains(ind2)){//op(?x ?y)
        		RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory));
//	            System.out.println("known "+ op.toString()+ " instances: " +estimate[0]+"  possible: "+estimate[1]);
	            return new double[] { cost+roleStatistics.getNumberOfKnownInstances()*COST_LOOKUP+(roleStatistics.getNumberOfPossibleInstances()*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth()), roleStatistics.getNumberOfKnownInstances()+(POSSIBLE_INSTANCE_SUCCESS*roleStatistics.getNumberOfPossibleInstances()) };
	        }
	        else if (bound.contains(ind1) && !bound.contains(ind2)) {//op(a ?y)<-op(?x ?y)
	        	//int[] estimate=m_instanceStatistics.getNumberOfPropertyInstances(AtomicRole.create(op.getIRIString()));
	        	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory));
	            if (roleStatistics.getNumberOfDistinctKnownPredecessors()!=0 && roleStatistics.getNumberOfDistinctPossiblePredecessors()!=0) 
	            	return new double[] { cost+(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())*COST_LOOKUP+ (roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), (roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())+POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors()) };   
	            else if (roleStatistics.getNumberOfDistinctPossiblePredecessors()!=0)  
	            	return new double[] {cost+(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossiblePredecessors()) };
	            else if (roleStatistics.getNumberOfDistinctKnownPredecessors()!=0)
	            	return new double[] {cost+(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())*COST_LOOKUP, (roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownPredecessors())};
	            else return new double[] {cost+0, 0};
	        }	
	        else if (!bound.contains(ind1) && bound.contains(ind2)){//op(?x a)<-op(?x ?y)
	        	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory));
	        	if (roleStatistics.getNumberOfDistinctKnownSuccessors()!=0 && roleStatistics.getNumberOfDistinctPossibleSuccessors()!=0) 
	        		return new double[] { cost+(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())*COST_LOOKUP+ (roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), (roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())+POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors()) };   
	            else if (roleStatistics.getNumberOfDistinctKnownSuccessors()!=0)
	            	return new double[] {cost+(roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())*COST_LOOKUP, (roleStatistics.getNumberOfKnownInstances()/roleStatistics.getNumberOfDistinctKnownSuccessors())};
	            else if (roleStatistics.getNumberOfDistinctPossibleSuccessors()!=0)
	                return new double[] {cost+(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors())*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), POSSIBLE_INSTANCE_SUCCESS*(roleStatistics.getNumberOfPossibleInstances()/roleStatistics.getNumberOfDistinctPossibleSuccessors()) };
	            else return new double[] {cost+0, 0};  
	        }
	        else {//op(a b) <- op(?x ?y)
	        	RoleInstanceStatistics roleStatistics=m_instanceStatistics.getRoleInstanceStatistics((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory));
	            return new double[] { cost+(roleStatistics.getNumberOfKnownInstances()/(double)m_indCount*m_indCount)*COST_LOOKUP + (roleStatistics.getNumberOfPossibleInstances()/(double)m_indCount*m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), (roleStatistics.getNumberOfKnownInstances()+roleStatistics.getNumberOfPossibleInstances()*POSSIBLE_INSTANCE_SUCCESS)/(double)m_indCount*(double)m_indCount};  
	        }  
	    }
	    else if (opVar==null && ind1 instanceof Variable) {  
	    	if (!bound.contains(ind1)) {// op(?x :a) 
	    		int[] estimate=m_instanceStatistics.getNumberOfPredecessors((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
	    		return new double[] { cost+estimate[0]*COST_LOOKUP+(estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth()), estimate[0]+(POSSIBLE_INSTANCE_SUCCESS*estimate[1]) };
	        }
	        else { //op(:b :a) <- op(?x :a) 
	    	    int[] estimate=m_instanceStatistics.getNumberOfPredecessors((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
	    		return new double[] { cost+(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), (estimate[0]/(double)m_indCount)+(POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount)) };
	        } 
	    } 
        else if (opVar==null && ind2 instanceof Variable) {// op(:a ?x)
        	if (!bound.contains(ind2)) {
        		int[] estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));
	            return new double[] { cost+estimate[0]*COST_LOOKUP+(estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth()), estimate[0]+(POSSIBLE_INSTANCE_SUCCESS*estimate[1]) };
	        }
	        else {
	        	int[] estimate=m_instanceStatistics.getNumberOfSuccessors((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory));
	            return new double[] { cost+(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getObjectPropertyHierarchyDepth(), (estimate[0]/(double)m_indCount)+(POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount)) };
	        }	                
//	            return new double[] { cost+m_indCount*COST_LOOKUP, m_indCount }; // needs refinement 
        }
        //else {
        	boolean[] result=m_instanceStatistics.hasSuccessor((OWLObjectProperty)op.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind1.asOWLAPIObject(m_dataFactory), (OWLNamedIndividual)ind2.asOWLAPIObject(m_dataFactory));
            if (result[0])
            	return new double[] { cost+COST_LOOKUP, 1 };
            else if (result[1])
        	    return new double[] { cost+((double)0.5*COST_ENTAILMENT*m_instanceStatistics.getObjectPropertyHierarchyDepth()), 1*POSSIBLE_INSTANCE_SUCCESS };
            else return new double[] {cost+COST_LOOKUP, 0 }; 
	    //}
	}		
	
	protected double[] getSameIndividualCost(Individual ind1, Individual ind2, Set<Variable> boundVar) {
		double cost=0;
		if (ind1.isVariable() && ind2.isVariable()) {
    		if (!boundVar.contains(ind1) && !boundVar.contains(ind2)) { //SameIndividual(?x ?y)
    			double[] costMatrix={0.0,0.0};
    			for (Individual ind : m_graph.getIndividualsInSignature()){ 
	    			int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind.asOWLAPIObject(m_dataFactory));
	                costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
	                costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
	            }
	    		return costMatrix;    			
    		}	    
    		else if (boundVar.contains(ind1) && !boundVar.contains(ind2)){//SameIndividual(?x :a) <- SameIndividual(?x ?y)
    			double[] costMatrix={0.0,0.0};
			    for (Individual ind : m_graph.getIndividualsInSignature()){ 
			    	int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind.asOWLAPIObject(m_dataFactory));
                    costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
                    costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
                }
			    costMatrix[0]=costMatrix[0]/(double)m_indCount;
			    costMatrix[1]=costMatrix[1]/(double)m_indCount;
    		    return costMatrix;
    		}    
    		else if (!boundVar.contains(ind1) && boundVar.contains(ind2)){//SameIndividual(:a ?x) <- SameIndividual(?x ?y)
    			double[] costMatrix={0.0,0.0};
    			for (Individual ind : m_graph.getIndividualsInSignature()){ 
    				int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind.asOWLAPIObject(m_dataFactory));
    			    costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
                    costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
                } 
		        costMatrix[0]=costMatrix[0]/(double)m_indCount;
		        costMatrix[1]=costMatrix[1]/(double)m_indCount;
		        return costMatrix;
    		}
		    else { //SameIndividual(?a :b)
		    	double[] costMatrix={0.0,0.0};
			    for (Individual ind : m_graph.getIndividualsInSignature()){ 
			    	int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind.asOWLAPIObject(m_dataFactory));
			        costMatrix[0]+=estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth();
                    costMatrix[1]+=estimate[0]+POSSIBLE_INSTANCE_SUCCESS*estimate[1];
                } 
	            costMatrix[0]=costMatrix[0]/(double)(m_indCount*m_indCount);
	            costMatrix[1]=costMatrix[1]/(double)(m_indCount*m_indCount);
	            return costMatrix;
		    }
		}	
    	else if (!ind1.isVariable() && ind2.isVariable()) {
	    	if (!boundVar.contains(ind2)){ //SameIndividual(:a ?x)
	    		int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind1.asOWLAPIObject(m_dataFactory));
    		    return new double[] { cost+estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), estimate[0]+(POSSIBLE_INSTANCE_SUCCESS*estimate[1])};
	    	}
    		else {//SameIndividual(:a :b) <- SameIndividual(:a ?x)
    			int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind1.asOWLAPIObject(m_dataFactory));
		        return new double[] { cost+(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), (estimate[0]/(double)m_indCount)+(POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount))};
    		}
    	}
    	else if (ind1.isVariable() && !ind2.isVariable()) {
	    	if (!boundVar.contains(ind1)) { //SameIndividual(?x :a)
	    		int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind2.asOWLAPIObject(m_dataFactory));
    		    return new double[] { cost+estimate[0]*COST_LOOKUP+estimate[1]*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), estimate[0]+(POSSIBLE_INSTANCE_SUCCESS*estimate[1])};
	    	}
    		else { //SameIndividual(:a :b) <- SameIndividual(?x :a)
	    		int[] estimate=m_instanceStatistics.getNumberOfSameIndividuals((OWLIndividual)ind2.asOWLAPIObject(m_dataFactory));
		        return new double[] { cost+(estimate[0]/(double)m_indCount)*COST_LOOKUP+(estimate[1]/(double)m_indCount)*COST_ENTAILMENT*0.5*m_instanceStatistics.getClassHierarchyDepth(), (estimate[0]/(double)m_indCount)+(POSSIBLE_INSTANCE_SUCCESS*(estimate[1]/(double)m_indCount))};	    
    	    }
    	}	
	    else {//SameIndividual(:a :b)
	    	boolean[] result=m_instanceStatistics.isKnownOrPossibleSameIndividual((OWLIndividual)ind1.asOWLAPIObject(m_dataFactory),(OWLIndividual)ind2.asOWLAPIObject(m_dataFactory)); 
            if (result[0])
            	return new double[] { cost+COST_LOOKUP, 1 };
            else if (result[1])
            	return new double[] { cost+((double)0.5*COST_ENTAILMENT*m_instanceStatistics.getObjectPropertyHierarchyDepth()), 1*POSSIBLE_INSTANCE_SUCCESS };
            else return new double[] {cost+COST_LOOKUP, 0 };
	    }
    }
}
