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


package  org.semanticweb.sparql.bgpevaluation.queryobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ClassExpressionVisitor;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectComplementOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasSelf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class QO_ClassAssertion extends AbstractQueryObject<ClassAssertion> {

	public QO_ClassAssertion(ClassAssertion axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }
	/*public boolean isComplex() {
		ClassExpression ce=m_axiomTemplate.getClassExpression();
		if (!ce.isVariable() && !ce.getVariablesInSignature().isEmpty())
    		return true;
    	else return false;
    }*/
    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable,Integer> bindingPositions) {
		Map<Variable,Atomic> bindingMap=new HashMap<Variable, Atomic>();
		// apply bindings that are already computed from previous steps
		for (Variable var : bindingPositions.keySet())
		    bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
		try {
    		ClassAssertion instantiated=(ClassAssertion)m_axiomTemplate.getBoundVersion(bindingMap);
            //System.out.println(instantiated);
    		ClassExpression ce=instantiated.getClassExpression();
    		Set<Variable> ceVars=ce.getVariablesInSignature();
    		Individual ind=instantiated.getIndividual();
            //if (ce instanceof Atomic || ce.isVariable()) {
            if (ce.isVariable() && ind.isVariable()) {//?x(?y)
            	int[] positions=new int[2];
                positions[0]=bindingPositions.get(ce);
                positions[1]=bindingPositions.get(ind);
                return computeAllClassAssertions(currentBinding,positions);
            } else if (ce.isVariable() && !ind.isVariable()) {//?x(:a)
                int position=bindingPositions.get(ce);
                return computeTypes(currentBinding,(OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory),position);
            } else if (ceVars.isEmpty() && ind.isVariable()) {//C(?x)
                    int position=bindingPositions.get(ind);
                    return computeInstances(currentBinding,(OWLClassExpression)ce.asOWLAPIObject(m_dataFactory),position);
            } else if (ceVars.isEmpty() && !ind.isVariable()) {//C(:a)
                if (checkType((OWLClassExpression)ce.asOWLAPIObject(m_dataFactory),(OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory)))
                	return Collections.singletonList(currentBinding);
                else 
                    return new ArrayList<Atomic[]>();
            } 
            else {
                return complex(currentBinding, instantiated, bindingPositions);
            }
		} catch (IllegalArgumentException e) {
		    // current binding is incompatible will not add new bindings in newBindings
		    return new ArrayList<Atomic[]>();
		}
	 }
     protected boolean checkType(OWLClassExpression classExpression, OWLNamedIndividual individual) {
        // ClassAssertion(:C :a)
        if (m_reasoner instanceof Reasoner)
            return ((Reasoner)m_reasoner).hasType(individual, classExpression, false);
        else
    	    return m_reasoner.getInstances(classExpression, false).containsEntity(individual); 
    }
	protected List<Atomic[]> computeTypes(Atomic[] currentBinding, OWLNamedIndividual individual, int bindingPosition) {
        // ClassAssertion(?x :a)
        Atomic[] binding;
        List<Atomic[]> newBindings=new ArrayList<Atomic[]>();
        Set<OWLClass> types=m_reasoner.getTypes(individual, false).getFlattened();
        for (OWLClass type : types) {
            binding=currentBinding.clone();
            binding[bindingPosition]=Clazz.create(type.getIRI().toString());
            newBindings.add(binding);
        }
        return newBindings;
    }
    protected List<Atomic[]> computeInstances(Atomic[] currentBinding, OWLClassExpression classExpression, int bindingPosition) {
        // ClassAssertion(:C ?y)
        Atomic[] binding;
        List<Atomic[]> newBindings=new ArrayList<Atomic[]>();
        Set<OWLNamedIndividual> instances=m_reasoner.getInstances(classExpression, false).getFlattened();
        for (OWLNamedIndividual instance : instances) {
            binding=currentBinding.clone();
            binding[bindingPosition]=NamedIndividual.create(instance.getIRI().toString());
            newBindings.add(binding);
        }
        return newBindings;
    }
    protected List<Atomic[]> computeAllClassAssertions(Atomic[] currentBinding, int[] bindingPositions) {
        // ClassAssertion(?x ?y)
        Atomic[] binding;
        List<Atomic[]> newBindings=new ArrayList<Atomic[]>();
        for (OWLClass owlClass : m_reasoner.getRootOntology().getClassesInSignature(true)) {
            NodeSet<OWLNamedIndividual> instances=m_reasoner.getInstances(owlClass, false);
            for (OWLNamedIndividual ind : instances.getFlattened()) {
                binding=currentBinding.clone();
                binding[bindingPositions[0]]=Clazz.create(owlClass.getIRI().toString());
                binding[bindingPositions[1]]=NamedIndividual.create(ind.getIRI().toString());
                newBindings.add(binding);
            }
        }
        return newBindings;
    }
    
/*    protected List<Atomic[]> filteringPass(Atomic[] currentBinding, Map<Variable,Integer> bindingPositions){
        Map<Variable,Atomic> bindingMap=new HashMap<Variable, Atomic>();
		// apply bindings that are already computed from previous steps
		for (Variable var : bindingPositions.keySet())
		    bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
		try {
    		ClassAssertion instantiated=(ClassAssertion)m_axiomTemplate.getBoundVersion(bindingMap);
//    		System.out.println(instantiated);
    		ClassExpression ce=instantiated.getClassExpression();
    		Set<Variable> ceVars=ce.getVariablesInSignature();
    		Individual ind=instantiated.getIndividual();
            if (ce.isVariable() && ind.isVariable()) {//?x(?y)
                int[] positions=new int[2];
                positions[0]=bindingPositions.get(ce);
                positions[1]=bindingPositions.get(ind);
                return computeAllClassAssertions(currentBinding,positions);
            } else if (ce.isVariable() && !ind.isVariable()) {//?x(:a)
                int position=bindingPositions.get(ce);
                return computeTypes(currentBinding,(OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory),position);
            } else if (ceVars.isEmpty() && ind.isVariable()) {//C(?x)
                int position=bindingPositions.get(ind);
                return [getKnownInstances(),getPossibleInstances()]; 
                //computeInstances(currentBinding,(OWLClassExpression)ce.asOWLAPIObject(m_dataFactory),position);
            } else if (ceVars.isEmpty() && !ind.isVariable()) {//C(:a)
                if isKnownOrPossibleInstance()
                   return Collections.singletonList(currentBinding);
                //(checkType((OWLClassExpression)ce.asOWLAPIObject(m_dataFactory),(OWLNamedIndividual)ind.asOWLAPIObject(m_dataFactory)))
                //    return Collections.singletonList(currentBinding);
                else 
                    return new ArrayList<Atomic[]>();
            } else {
                return complex(currentBinding, instantiated, bindingPositions);
            }
		} catch (IllegalArgumentException e) {
		    // current binding is incompatible will not add new bindings in newBindings
		    return new ArrayList<Atomic[]>();
		}
    }*/
    
    protected List<Atomic[]> complex(Atomic[] currentBinding, ClassAssertion axiom, Map<Variable,Integer> bindingPositions) {
    	
    	ClassExpression expr=axiom.getClassExpression();   	
    	List<Atomic[]> results=new ArrayList<Atomic[]>();
        List<Variable> vars=new ArrayList<Variable>(axiom.getVariablesInSignature());
        List<Atomic[]> testedBindings=new ArrayList<Atomic[]>();
        List<Atomic[]> newBindingsOut=new ArrayList<Atomic[]>();
        newBindingsOut.add(currentBinding);
     
        for (Variable var : vars) {
            testedBindings=new ArrayList<Atomic[]>();
            Atomic[] clonedBinding;
            for (Atomic[] binding:newBindingsOut) {
            	if (var instanceof ClassVariable) {
            	    boolean polarity;
                    PositivePolarityClassVisitor clsVisitor=new PositivePolarityClassVisitor(var);
                	expr.accept(clsVisitor);
                    polarity=clsVisitor.getVarPolarity();    
            	    
                    if (polarity==Boolean.TRUE){
            		    OWLClass top = m_reasoner.getTopClassNode().getRepresentativeElement();
            		    Set<OWLClass> classSet = m_reasoner.getSubClasses(top, true).getFlattened();
                        Iterator<OWLClass> itr = classSet.iterator();
                        while(itr.hasNext()) {
                            OWLClass element = itr.next();
                            clonedBinding=binding.clone();
                            clonedBinding[bindingPositions.get(var)]=(Clazz)FromOWLAPIConverter.convert(element);
                            testedBindings.add(clonedBinding);
                        } 
                        clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=Clazz.THING;
                        testedBindings.add(clonedBinding);
            		}
            		else {
            		    OWLClass bottom = m_reasoner.getBottomClassNode().getRepresentativeElement();
            		    Set<OWLClass> classSet = m_reasoner.getSuperClasses(bottom, true).getFlattened();
                        Iterator<OWLClass> itr = classSet.iterator(); 
                        while(itr.hasNext()) {
                            OWLClass element = itr.next(); 
                            clonedBinding=binding.clone();
                            clonedBinding[bindingPositions.get(var)]=(Clazz)FromOWLAPIConverter.convert(element);
                            testedBindings.add(clonedBinding);  
                        }
                        clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=Clazz.NOTHING;
                        testedBindings.add(clonedBinding);
            		}
            	}
            	else if (var instanceof ObjectPropertyVariable) {
            		boolean polarity;
            	    PositivePolarityPropertyVisitor clsVisitor=new PositivePolarityPropertyVisitor(var);
                	expr.accept(clsVisitor);
                	polarity=clsVisitor.getVarPolarity();    
  	
            		if (polarity==Boolean.TRUE){
            		    OWLObjectPropertyExpression top = m_reasoner.getTopObjectPropertyNode().getRepresentativeElement();
            		    Set<OWLObjectPropertyExpression> propertySet = m_reasoner.getSubObjectProperties(top, true).getFlattened();
                        for (OWLObjectPropertyExpression propexpr:propertySet) {
                            if (propexpr instanceof OWLObjectProperty) {
                                clonedBinding=binding.clone();
                                clonedBinding[bindingPositions.get(var)]=(ObjectProperty)FromOWLAPIConverter.convert((OWLObjectProperty)propexpr);
                                testedBindings.add(clonedBinding);
                            } 
                        }
                        clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=ObjectProperty.TOP_OBJECT_PROPERTY;
                        testedBindings.add(clonedBinding);
                    }
            		else {
            		    OWLObjectPropertyExpression bottom = m_reasoner.getBottomObjectPropertyNode().getRepresentativeElement();
            		    Set<OWLObjectPropertyExpression> propertySet = m_reasoner.getSuperObjectProperties(bottom, true).getFlattened();
                        for (OWLObjectPropertyExpression propexpr:propertySet) {
                            if (propexpr instanceof OWLObjectProperty) {
                                clonedBinding=binding.clone();
                                clonedBinding[bindingPositions.get(var)]=(ObjectProperty)FromOWLAPIConverter.convert((OWLObjectProperty)propexpr);
                                testedBindings.add(clonedBinding);
                            } 
                        }
                        clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=ObjectProperty.BOTTOM_OBJECT_PROPERTY;
                        testedBindings.add(clonedBinding);
            		}
            		/*for (ObjectProperty prop:m_graph.getObjectPropertiesInSignature()) {
            			clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=prop;
                        testedBindings.add(clonedBinding);
                    }*/ 
                }
            	//}
                else if (var instanceof DatatypeVariable) {
                	for (Datatype dt:m_graph.getDatatypesInSignature()) {
            			clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=dt;
                        testedBindings.add(clonedBinding);
                    } 
                }
                else if (var instanceof DataPropertyVariable) {
                	for (DataProperty dp:m_graph.getDataPropertiesInSignature()) {
            			clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=dp;
                        testedBindings.add(clonedBinding);
                    } 
                }
                else if (var instanceof AnnotationPropertyVariable) {
                	for (AnnotationProperty ap:m_graph.getAnnotationPropertiesInSignature()) {
            			clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=ap;
                        testedBindings.add(clonedBinding);
                    }
                }	
                else if (var instanceof IndividualVariable) {
                	for (NamedIndividual ind:m_graph.getIndividualsInSignature()) {
            			clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=ind;
                        testedBindings.add(clonedBinding);
                    }
                }
                else if (var instanceof LiteralVariable) {
                	for (Literal ind:m_graph.getLiteralsInSignature()) {
            			clonedBinding=binding.clone();
                        clonedBinding[bindingPositions.get(var)]=ind;
                        testedBindings.add(clonedBinding);
                    }
                }	
                else 
                    throw new IllegalArgumentException("Error: The class assertion axiom template "+axiom+" contains untyped variables. ");
            }
        newBindingsOut=new ArrayList<Atomic[]>();
        newBindingsOut.addAll(testedBindings);
        }
        results.addAll(addEntailedBindings(axiom, newBindingsOut, bindingPositions)); 
    return results;
    }   

    public List<Atomic[]> addEntailedBindings(ClassAssertion axiom, List<Atomic[]> bindingList, Map<Variable,Integer> bindingPositions) {
	    List<Atomic[]> results=new ArrayList<Atomic[]>();
        Atomic[] clonedBinding;
	    List<Atomic[]> returnBindings=new ArrayList<Atomic[]>();
	    int entNo=0;
	    while (!bindingList.isEmpty()) {
		    Atomic[] currentBinding=bindingList.remove(0);
		    returnBindings=new ArrayList<Atomic[]>();
		    Map<Variable,Atomic> bindingMap=new HashMap<Variable, Atomic>();
		    for (Variable var : bindingPositions.keySet())
                bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
		    entNo++;
		    if (m_reasoner.isEntailed((OWLAxiom) axiom.getBoundVersion(bindingMap, m_dataFactory))) {
			    results.add(currentBinding);
                List<Variable> vars=new ArrayList<Variable>(axiom.getVariablesInSignature());
                ClassExpression expr=axiom.getClassExpression();
	    	    List<Atomic[]> testedBindings=new ArrayList<Atomic[]>();
	    	    for (Variable var : vars) {
	    		    returnBindings.add(currentBinding);
	    		    for (Atomic[] binding : returnBindings) {
	    			    if (var instanceof ClassVariable) {
            			    boolean polarity;
            			    PositivePolarityClassVisitor clsVisitor=new PositivePolarityClassVisitor(var);
                    	    expr.accept(clsVisitor);
                    		polarity=clsVisitor.getVarPolarity();    
                		        
            			    if (polarity==Boolean.TRUE){
        		                OWLClass currentClass = (OWLClass)binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
        		                Set<OWLClass> classSet = m_reasoner.getSubClasses(currentClass, true).getFlattened();
                                Iterator<OWLClass> itr = classSet.iterator();
                                while(itr.hasNext()) {
                                    OWLClass element = itr.next();
                                    clonedBinding=binding.clone();
                                    clonedBinding[bindingPositions.get(var)]=(Clazz)FromOWLAPIConverter.convert(element);
                                    testedBindings.add(clonedBinding);
                                } 
        		            }
        		            else {
        		                OWLClass currentClass = (OWLClass)binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory);
        		                Set<OWLClass> classSet = m_reasoner.getSuperClasses(currentClass, true).getFlattened();
                                Iterator<OWLClass> itr = classSet.iterator(); 
                                while(itr.hasNext()) {
                                    OWLClass element = itr.next(); 
                                    clonedBinding=binding.clone();
                                    clonedBinding[bindingPositions.get(var)]=(Clazz)FromOWLAPIConverter.convert(element);
                                    testedBindings.add(clonedBinding);
                                } 
                            }
        	            }
	    			    else if (var instanceof ObjectPropertyVariable) {
	    			    	boolean polarity;
                			PositivePolarityPropertyVisitor clsVisitor=new PositivePolarityPropertyVisitor(var);
                        	expr.accept(clsVisitor);
                        	polarity=clsVisitor.getVarPolarity();    
                    	    
                            if (polarity==Boolean.TRUE){
                            	OWLObjectProperty currentProperty = (OWLObjectProperty)binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
            		            Set<OWLObjectPropertyExpression> propertySet = m_reasoner.getSubObjectProperties(currentProperty, true).getFlattened();
                                for (OWLObjectPropertyExpression propexpr:propertySet) {
                                    if (propexpr instanceof OWLObjectProperty) {
                                        clonedBinding=binding.clone();
                                        clonedBinding[bindingPositions.get(var)]=(ObjectProperty)FromOWLAPIConverter.convert((OWLObjectProperty)propexpr);
                                        testedBindings.add(clonedBinding);
                                    } 
                                }   
            		        }
            		        else {
            		        	OWLObjectProperty currentProperty = (OWLObjectProperty)binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
            		            Set<OWLObjectPropertyExpression> propertySet = m_reasoner.getSuperObjectProperties(currentProperty, true).getFlattened();
                                for (OWLObjectPropertyExpression propexpr:propertySet) {
                                    if (propexpr instanceof OWLObjectProperty) {
                                        clonedBinding=binding.clone();
                                        clonedBinding[bindingPositions.get(var)]=(ObjectProperty)FromOWLAPIConverter.convert((OWLObjectProperty)propexpr);
                                        testedBindings.add(clonedBinding);
                                    } 
                                }  
                            }
    	    			}
                   }      
		    	   returnBindings=new ArrayList<Atomic[]>();
		           returnBindings.addAll(testedBindings);
	           }
	           testedBindings=new ArrayList<Atomic[]>();
	           bindingList.addAll(returnBindings);
           }                 
       }
       System.out.println("EntailmentChecksNo=  "+entNo);
       return results; 
    }
    
    public <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound) {
        return visitor.visit(this, bound);
    }

	@Override
	public <O> O accept(DynamicQueryObjectVisitorEx<O> visitor) {
		return visitor.visit(this);
	}
	
	protected class PositivePolarityClassVisitor implements ClassExpressionVisitor {

		protected Variable var;
		protected boolean varPolarity;
		
		public PositivePolarityClassVisitor(Variable queryVar) {
			var=queryVar;
		}
		public Boolean getVarPolarity() {
			return varPolarity;
		}
		
		public void visit(ClassVariable object) {
			if (var.getVariable().equals(object.getVariable()))
					varPolarity=Boolean.TRUE;
		}		
		public void visit(Clazz object) {
			
        }
        public void visit(ObjectIntersectionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectUnionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectComplementOf object) {
        	ClassExpression expr=object.getComplementedClassExpression();
            NegativePolarityClassVisitor npv=new NegativePolarityClassVisitor(var);
        	expr.accept(npv);
        }
        public void visit(ObjectOneOf object) {
            
        }
        public void visit(ObjectSomeValuesFrom object) {
        	object.getClassExpression().accept(this);
        }
        public void visit(ObjectAllValuesFrom object) {
        	object.getClassExpression().accept(this);
        }
        public void visit(ObjectHasValue object) {
            
        }
        public void visit(ObjectHasSelf object) {
            
        }
        public void visit(ObjectMinCardinality object) {
            object.getClassExpression().accept(this);
        }
        public void visit(ObjectMaxCardinality object) {
        	ClassExpression expr=object.getClassExpression();
        	NegativePolarityClassVisitor npv=new NegativePolarityClassVisitor(var);
         	expr.accept(npv);
        }
        public void visit(ObjectExactCardinality object) {
        	ClassExpression expr=object.getClassExpression();
        	NegativePolarityClassVisitor npv=new NegativePolarityClassVisitor(var);
         	expr.accept(npv);
        }
        public void visit(DataSomeValuesFrom desc) {
            
        }
        public void visit(DataAllValuesFrom desc) {
           
        }
        public void visit(DataHasValue desc) {
            
        }
        public void visit(DataMinCardinality desc) {
            
        }
        public void visit(DataMaxCardinality desc) {
            
        }
        public void visit(DataExactCardinality desc) {
            
        }
		public void visit(ObjectProperty objectProperty) {
	
		}
		public void visit(ObjectInverseOf objectproperty) {
			
		}
		public void visit(ObjectPropertyChain objectProperty) {
			
		}
		public void visit(ObjectPropertyVariable objectPropertyVariable) {
		
		}
    }
	
	protected class NegativePolarityClassVisitor implements ClassExpressionVisitor {
		protected Variable var;
		protected boolean varPolarity;
		
		public NegativePolarityClassVisitor(Variable queryVar) {
			var=queryVar;
		}
		public Boolean getVarPolarity() {
			return varPolarity;
		}
		
		public void visit(ClassVariable object) {
			if (var.getVariable().equals(object.getVariable()))
					varPolarity=Boolean.FALSE;
			//return Boolean.TRUE;
		}		
		public void visit(Clazz object) {
			
        }
        public void visit(ObjectIntersectionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectUnionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectComplementOf object) {
        	ClassExpression expr=object.getComplementedClassExpression();
            PositivePolarityClassVisitor npv=new PositivePolarityClassVisitor(var);
        	expr.accept(npv);
        }
        public void visit(ObjectOneOf object) {
            
        }
        public void visit(ObjectSomeValuesFrom object) {
        	object.getClassExpression().accept(this);
        }
        public void visit(ObjectAllValuesFrom object) {
        	object.getClassExpression().accept(this);
        }
        public void visit(ObjectHasValue object) {
            
        }
        public void visit(ObjectHasSelf object) {
            
        }
        public void visit(ObjectMinCardinality object) {
            object.getClassExpression().accept(this);
        }
        public void visit(ObjectMaxCardinality object) {
        	ClassExpression expr=object.getClassExpression();
        	PositivePolarityClassVisitor npv=new PositivePolarityClassVisitor(var);
         	expr.accept(npv);
        }
        public void visit(ObjectExactCardinality object) {
        	ClassExpression expr=object.getClassExpression();
        	PositivePolarityClassVisitor npv=new PositivePolarityClassVisitor(var);
         	expr.accept(npv);
        }
        public void visit(DataSomeValuesFrom desc) {
            
        }
        public void visit(DataAllValuesFrom desc) {
           
        }
        public void visit(DataHasValue desc) {
            
        }
        public void visit(DataMinCardinality desc) {
            
        }
        public void visit(DataMaxCardinality desc) {
            
        }
        public void visit(DataExactCardinality desc) {
            
        }
		public void visit(ObjectProperty objectProperty) {
			
		}
		public void visit(ObjectInverseOf objectproperty) {
			
		}
		public void visit(ObjectPropertyChain objectProperty) {
	
		}
		public void visit(ObjectPropertyVariable objectPropertyVariable) {
			
		}
	}
	
	protected class PositivePolarityPropertyVisitor implements ClassExpressionVisitor {

		protected Variable var;
		protected boolean varPolarity;
		
		public PositivePolarityPropertyVisitor(Variable queryVar) {
			var=queryVar;
		}
		public Boolean getVarPolarity() {
			return varPolarity;
		}
		public void visit(ObjectProperty object) {
			
		} 
        public void visit(ObjectInverseOf object) {
			object.getInvertedObjectProperty().accept(this);
		}
        public void visit(ObjectPropertyChain object) {
        	
		}
		public void visit(ObjectPropertyVariable object) {
			if (var.getVariable().equals(object.getVariable()))
				varPolarity=Boolean.TRUE;
		}
		public void visit(ClassVariable object) {
			
		}		
		public void visit(Clazz object) {
			
        }
        public void visit(ObjectIntersectionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectUnionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectComplementOf object) {
        	ClassExpression expr=object.getComplementedClassExpression();
            NegativePolarityPropertyVisitor npv=new NegativePolarityPropertyVisitor(var);
        	expr.accept(npv);
        }
        public void visit(ObjectOneOf object) {
            
        }
        public void visit(ObjectSomeValuesFrom object) {
        	object.getClassExpression().accept(this);
        	object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectAllValuesFrom object) {
        	object.getClassExpression().accept(this);
        	NegativePolarityPropertyVisitor npv1=new NegativePolarityPropertyVisitor(var);
        	object.getObjectPropertyExpression().accept(npv1);
        }
        public void visit(ObjectHasValue object) {
            object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectHasSelf object) {
            object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectMinCardinality object) {
            object.getClassExpression().accept(this);
            object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectMaxCardinality object) {
        	NegativePolarityPropertyVisitor npv=new NegativePolarityPropertyVisitor(var);
            object.getClassExpression().accept(npv);
        	NegativePolarityPropertyVisitor npv1=new NegativePolarityPropertyVisitor(var);
         	object.getObjectPropertyExpression().accept(npv1);
        }
        public void visit(ObjectExactCardinality object) {
        	NegativePolarityPropertyVisitor npv=new NegativePolarityPropertyVisitor(var);
            object.getClassExpression().accept(npv);
        	NegativePolarityPropertyVisitor npv1=new NegativePolarityPropertyVisitor(var);
         	object.getObjectPropertyExpression().accept(npv1);
        }
        public void visit(DataSomeValuesFrom desc) {
            
        }
        public void visit(DataAllValuesFrom desc) {
           
        }
        public void visit(DataHasValue desc) {
            
        }
        public void visit(DataMinCardinality desc) {
            
        }
        public void visit(DataMaxCardinality desc) {
            
        }
        public void visit(DataExactCardinality desc) {
            
        }
    }
	
	protected class NegativePolarityPropertyVisitor implements ClassExpressionVisitor {
		protected Variable var;
		protected boolean varPolarity;
		
		public NegativePolarityPropertyVisitor(Variable queryVar) {
			var=queryVar;
		}
		public Boolean getVarPolarity() {
			return varPolarity;
		}
		public void visit(ObjectProperty object) {
			
		} 
        public void visit(ObjectInverseOf object) {
			object.getInvertedObjectProperty().accept(this);
		}
        public void visit(ObjectPropertyChain object) {
        	
		}
		public void visit(ObjectPropertyVariable object) {
			if (var.getVariable().equals(object.getVariable()))
				varPolarity=Boolean.FALSE;
		}
		public void visit(ClassVariable object) {
			
		}		
		public void visit(Clazz object) {
			
        }
        public void visit(ObjectIntersectionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectUnionOf object) {
            for (ClassExpression desc : object.getClassExpressions())
                desc.accept(this);
        }
        public void visit(ObjectComplementOf object) {
        	ClassExpression expr=object.getComplementedClassExpression();
            PositivePolarityPropertyVisitor npv=new PositivePolarityPropertyVisitor(var);
        	expr.accept(npv);
        }
        public void visit(ObjectOneOf object) {
            
        }
        public void visit(ObjectSomeValuesFrom object) {
        	object.getClassExpression().accept(this);
        	object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectAllValuesFrom object) {
        	object.getClassExpression().accept(this);
        	PositivePolarityPropertyVisitor npv1=new PositivePolarityPropertyVisitor(var);
        	object.getObjectPropertyExpression().accept(npv1);
        }
        public void visit(ObjectHasValue object) {
            object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectHasSelf object) {
            object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectMinCardinality object) {
            object.getClassExpression().accept(this);
            object.getObjectPropertyExpression().accept(this);
        }
        public void visit(ObjectMaxCardinality object) {
        	PositivePolarityPropertyVisitor npv=new PositivePolarityPropertyVisitor(var);
            object.getClassExpression().accept(npv);
        	PositivePolarityPropertyVisitor npv1=new PositivePolarityPropertyVisitor(var);
         	object.getObjectPropertyExpression().accept(npv1);
        }
        public void visit(ObjectExactCardinality object) {
        	PositivePolarityPropertyVisitor npv=new PositivePolarityPropertyVisitor(var);
            object.getClassExpression().accept(npv);
        	PositivePolarityPropertyVisitor npv1=new PositivePolarityPropertyVisitor(var);
         	object.getObjectPropertyExpression().accept(npv1);
        }
        public void visit(DataSomeValuesFrom desc) {
            
        }
        public void visit(DataAllValuesFrom desc) {
           
        }
        public void visit(DataHasValue desc) {
            
        }
        public void visit(DataMinCardinality desc) {
            
        }
        public void visit(DataMaxCardinality desc) {
            
        }
        public void visit(DataExactCardinality desc) {
            
        }
	}

}
