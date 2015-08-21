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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ClassAndPropertyExpressionVisitorEx;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.*;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
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
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public class QO_SubClassOf extends AbstractQueryObject<SubClassOf> {

    public QO_SubClassOf(SubClassOf axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }

    public boolean isComplex() {
        ClassExpression subClass = m_axiomTemplate.getSubClassExpression();
        ClassExpression superClass = m_axiomTemplate.getSuperClassExpression();
        if (!subClass.isVariable() && !subClass.getVariablesInSignature().isEmpty())
            return true;
        else if (!superClass.isVariable() && !superClass.getVariablesInSignature().isEmpty())
            return true;
        else return false;
    }

    protected List<Atomic[]> addBindings(Atomic[] currentBinding, Map<Variable, Integer> bindingPositions) {
        Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
        // apply bindings that are already computed from previous steps
        for (Variable var : bindingPositions.keySet())
            bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
        try {
            SubClassOf axiom = (SubClassOf) m_axiomTemplate.getBoundVersion(bindingMap);
            ClassExpression subClass = axiom.getSubClassExpression();
            ClassExpression superClass = axiom.getSuperClassExpression();
            if (subClass.isVariable() && superClass.isVariable()) {
                int[] positions = new int[2];
                positions[0] = bindingPositions.get(subClass);
                positions[1] = bindingPositions.get(superClass);
                return computeAllSubClassOfRelations(currentBinding, positions);
            }
            else if (subClass.isVariable() && !superClass.isVariable() && superClass instanceof Clazz /*superClass.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()*/) {
                int position = bindingPositions.get(subClass);
                return computeSubClasses(currentBinding, (OWLClassExpression) superClass.asOWLAPIObject(m_toOWLAPIConverter), position);
            }
            else if (superClass.isVariable() && !subClass.isVariable() && subClass instanceof Clazz/*subClass.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty()*/) {
                int position = bindingPositions.get(superClass);
                return computeSuperClasses(currentBinding, (OWLClassExpression) subClass.asOWLAPIObject(m_toOWLAPIConverter), position);
            }
            else if (subClass.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty() && superClass.getBoundVersion(bindingMap).getVariablesInSignature().isEmpty())
                return checkSubsumption(currentBinding, (OWLClassExpression) subClass.asOWLAPIObject(m_toOWLAPIConverter), (OWLClassExpression) superClass.asOWLAPIObject(m_toOWLAPIConverter));
            else
                return complex(currentBinding, axiom, bindingPositions);

            //@Ilianna: The code below gives incorrect results and is inefficient, e.g., owl:Nothing is missing from the subclasses of a complex class 
            // and finding the subclasses of a complex class does not require an iteration over all classes, as long as the superclass does not contain an 
            // unbound variable, the reasoner can directly compute the subclasses   
//            if ((subClass instanceof Atomic || subClass.isVariable()) && (superClass instanceof Atomic || superClass.isVariable())) {
//            	if (subClass.isVariable() && superClass.isVariable()) {
//            		int[] positions=new int[2];
//                    positions[0]=bindingPositions.get(subClass);
//                    positions[1]=bindingPositions.get(superClass);
//                    return computeAllSubClassOfRelations(currentBinding,positions);
//                } else if (subClass.isVariable() && !superClass.isVariable()) {
//                    int position=bindingPositions.get(subClass);
//                    return computeSubClasses(currentBinding,(OWLClassExpression)superClass.asOWLAPIObject(m_toOWLAPIConverter),position);
//                } else if (!subClass.isVariable() && superClass.isVariable()) {
//                    int position=bindingPositions.get(superClass);
//                    return computeSuperClasses(currentBinding,(OWLClassExpression)subClass.asOWLAPIObject(m_toOWLAPIConverter),position);
//                } else if (!subClass.isVariable() && !superClass.isVariable()) {
//                    return checkSubsumption(currentBinding,(OWLClassExpression)subClass.asOWLAPIObject(m_toOWLAPIConverter),(OWLClassExpression)superClass.asOWLAPIObject(m_toOWLAPIConverter));
//                } else throw new RuntimeException("There is no other case so it shouldn't have arrived here");
//            } 
//            else {
//                return complex(currentBinding,axiom,bindingPositions);
//            }
        } catch (IllegalArgumentException e) {
            // current binding is incompatible will not add new bindings in newBindings
            return new ArrayList<Atomic[]>();
        }
    }

    protected List<Atomic[]> computeAllSubClassOfRelations(Atomic[] currentBinding, int[] bindingPositions) {
        // SubClassOf(?x ?y)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        for (Clazz clazz : m_graph.getClassesInSignature()) {
            OWLClass owlClass = (OWLClass) clazz.asOWLAPIObject(m_toOWLAPIConverter);
            Set<OWLClass> superClasses = m_reasoner.getSuperClasses(owlClass, false).getFlattened();
            superClasses.addAll(m_reasoner.getEquivalentClasses(owlClass).getEntities());
            for (OWLClass cls : superClasses) {
                binding = currentBinding.clone();
                binding[bindingPositions[0]] = clazz;
                binding[bindingPositions[1]] = (Clazz) FromOWLAPIConverter.convert(cls);
                newBindings.add(binding);
            }
        }
        return newBindings;
    }

    protected List<Atomic[]> computeSubClasses(Atomic[] currentBinding, OWLClassExpression superClass, int bindingPosition) {
        // SubClassOf(?x :C)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Set<OWLClass> subs = m_reasoner.getSubClasses(superClass, false).getFlattened();
        subs.addAll(m_reasoner.getEquivalentClasses(superClass).getEntities());
        for (OWLClass sub : subs) {
            binding = currentBinding.clone();
            binding[bindingPosition] = (Clazz) FromOWLAPIConverter.convert(sub);
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected List<Atomic[]> computeSuperClasses(Atomic[] currentBinding, OWLClassExpression subClass, int bindingPosition) {
        // SubClassOf(?x :C)
        Atomic[] binding;
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        Set<OWLClass> sups = m_reasoner.getSuperClasses(subClass, false).getFlattened();
        sups.addAll(m_reasoner.getEquivalentClasses(subClass).getEntities());
        for (OWLClass sup : sups) {
            binding = currentBinding.clone();
            binding[bindingPosition] = (Clazz) FromOWLAPIConverter.convert(sup);
            newBindings.add(binding);
        }
        return newBindings;
    }

    protected List<Atomic[]> checkSubsumption(Atomic[] currentBinding, OWLClassExpression subClass, OWLClassExpression superClass) {
        // SubClassOf(:C :D)
        List<Atomic[]> newBindings = new ArrayList<Atomic[]>();
        if (m_reasoner.isEntailed(m_dataFactory.getOWLSubClassOfAxiom(subClass, superClass)))
            newBindings.add(currentBinding);
        return newBindings;
    }

    protected List<Atomic[]> complex(Atomic[] currentBinding, SubClassOf axiom, Map<Variable, Integer> bindingPositions) {
        ClassExpression subClass = axiom.getSubClassExpression();
        ClassExpression superClass = axiom.getSuperClassExpression();
        Set<Variable> subClassVars = subClass.getVariablesInSignature();
        List<Atomic[]> results = new ArrayList<Atomic[]>();
        List<Variable> vars = new ArrayList<Variable>(axiom.getVariablesInSignature());
        List<Atomic[]> testedBindings = new ArrayList<Atomic[]>();
        List<Atomic[]> newBindingsOut = new ArrayList<Atomic[]>();
        newBindingsOut.add(currentBinding);
        for (Variable var : vars) {
            testedBindings = new ArrayList<Atomic[]>();
            Atomic[] clonedBinding;
            for (Atomic[] binding : newBindingsOut) {
                if (var instanceof ClassVariable) {
                    Integer polarity;
                    if (subClassVars.contains(var)) {
                        NegativePolarityClassVisitor clsVisitor = new NegativePolarityClassVisitor(var);
                        polarity = subClass.accept(clsVisitor);
                    }
                    else {
                        PositivePolarityClassVisitor clsVisitor = new PositivePolarityClassVisitor(var);
                        polarity = superClass.accept(clsVisitor);
                    }
                    if (polarity == 1) {
                        clonedBinding = binding.clone();
                        IRI top = m_reasoner.getTopClassNode().getRepresentativeElement().getIRI();
                        clonedBinding[bindingPositions.get(var)] = Clazz.create(top.toString());
                        testedBindings.add(clonedBinding);

                        OWLClass equivClass = OWLManager.getOWLDataFactory().getOWLThing();
                        for (OWLClass cls : m_reasoner.getEquivalentClasses(equivClass).getEntities()) {
                            if (!cls.equals(equivClass)) {
                                clonedBinding = binding.clone();
                                clonedBinding[bindingPositions.get(var)] = (Clazz) FromOWLAPIConverter.convert(cls);
                                testedBindings.add(clonedBinding);
                            }
                        }
                    }
                    else if (polarity == 2) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = Clazz.NOTHING;
                        testedBindings.add(clonedBinding);

                        OWLClass equivClass = OWLManager.getOWLDataFactory().getOWLNothing();
                        for (OWLClass cls : m_reasoner.getEquivalentClasses(equivClass).getEntities()) {
                            if (!cls.equals(equivClass)) {
                                clonedBinding = binding.clone();
                                clonedBinding[bindingPositions.get(var)] = (Clazz) FromOWLAPIConverter.convert(cls);
                                testedBindings.add(clonedBinding);
                            }
                        }
                    }
                }
                else if (var instanceof ObjectPropertyVariable) {
                    Integer polarity;
                    if (subClassVars.contains(var)) {
                        NegativePolarityPropertyVisitor propVisitor = new NegativePolarityPropertyVisitor(var);
                        polarity = subClass.accept(propVisitor);
                    }
                    else {
                        PositivePolarityPropertyVisitor propVisitor = new PositivePolarityPropertyVisitor(var);
                        polarity = superClass.accept(propVisitor);
                    }
                    if (polarity == 1) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = ObjectProperty.TOP_OBJECT_PROPERTY;
                        testedBindings.add(clonedBinding);

                        OWLObjectProperty equivProp = OWLManager.getOWLDataFactory().getOWLTopObjectProperty();
                        for (OWLObjectPropertyExpression prop : m_reasoner.getEquivalentObjectProperties(equivProp).getEntities()) {
                            if (prop instanceof OWLObjectProperty && !prop.equals(equivProp)) {
                                clonedBinding = binding.clone();
                                clonedBinding[bindingPositions.get(var)] = (ObjectProperty) FromOWLAPIConverter.convert(prop);
                                testedBindings.add(clonedBinding);
                            }
                        }
                    }
                    else if (polarity == 2) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = ObjectProperty.BOTTOM_OBJECT_PROPERTY;
                        testedBindings.add(clonedBinding);

                        OWLObjectProperty equivProp = OWLManager.getOWLDataFactory().getOWLBottomObjectProperty();
                        for (OWLObjectPropertyExpression prop : m_reasoner.getEquivalentObjectProperties(equivProp).getEntities()) {
                            if (prop instanceof OWLObjectProperty && !prop.equals(equivProp)) {
                                clonedBinding = binding.clone();
                                clonedBinding[bindingPositions.get(var)] = (ObjectProperty) FromOWLAPIConverter.convert(prop);
                                testedBindings.add(clonedBinding);
                            }
                        }
                    }
                }
                else if (var instanceof DatatypeVariable) {
                    for (Datatype dt : m_graph.getDatatypesInSignature()) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = dt;
                        testedBindings.add(clonedBinding);
                    }
                }
                else if (var instanceof DataPropertyVariable) {
                    for (DataProperty dp : m_graph.getDataPropertiesInSignature()) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = dp;
                        testedBindings.add(clonedBinding);
                    }
                }
                else if (var instanceof AnnotationPropertyVariable) {
                    for (AnnotationProperty ap : m_graph.getAnnotationPropertiesInSignature()) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = ap;
                        testedBindings.add(clonedBinding);
                    }
                }
                else if (var instanceof IndividualVariable) {
                    for (NamedIndividual ind : m_graph.getIndividualsInSignature()) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = ind;
                        testedBindings.add(clonedBinding);
                    }
                }
                else if (var instanceof LiteralVariable) {
                    for (Literal ind : m_graph.getLiteralsInSignature()) {
                        clonedBinding = binding.clone();
                        clonedBinding[bindingPositions.get(var)] = ind;
                        testedBindings.add(clonedBinding);
                    }
                }
                else
                    throw new IllegalArgumentException("Error: The class assertion axiom template " + axiom + " contains untyped variables. ");
            }
            newBindingsOut = new ArrayList<Atomic[]>();
            newBindingsOut.addAll(testedBindings);
        }
        results.addAll(addEntailedBindings(axiom, newBindingsOut, bindingPositions));
        return results;
    }

    public List<Atomic[]> addEntailedBindings(SubClassOf axiom, List<Atomic[]> bindingList, Map<Variable, Integer> bindingPositions) {
        List<Atomic[]> results = new ArrayList<Atomic[]>();
        Atomic[] clonedBinding;
        List<Atomic[]> returnBindings = new ArrayList<Atomic[]>();
        //int entNo=0;
        List<Atomic[]> notEntailedList = new ArrayList<Atomic[]>();
        while (!bindingList.isEmpty()) {
            Atomic[] currentBinding = bindingList.remove(0);
            returnBindings = new ArrayList<Atomic[]>();
            Map<Variable, Atomic> bindingMap = new HashMap<Variable, Atomic>();
            for (Variable var : bindingPositions.keySet())
                bindingMap.put(var, currentBinding[bindingPositions.get(var)]);
            List<Variable> vars = new ArrayList<Variable>(axiom.getVariablesInSignature());
            ClassExpression subClass = axiom.getSubClassExpression();
            ClassExpression superClass = axiom.getSuperClassExpression();
            if (!isInNotEntailedList(currentBinding, notEntailedList, bindingPositions, vars/*, axiom*/) && !isInList(currentBinding, results, bindingPositions, vars)) {
                //entNo++;
                if (m_reasoner.isEntailed((OWLAxiom) axiom.getBoundVersion(bindingMap, m_dataFactory))) {
                    results.add(currentBinding);
                    List<Atomic[]> testedBindings = new ArrayList<Atomic[]>();
                    Atomic[] binding = currentBinding;
                    for (Variable var : vars) {
                        testedBindings = new ArrayList<Atomic[]>();
                        if (var instanceof ClassVariable) {
                            /*OWLClass equivClass=(OWLClass)bindingMap.get(var).asOWLAPIObject(m_toOWLAPIConverter);
                		   	for (OWLClass cls:m_reasoner.getEquivalentClasses(equivClass).getEntities()){
                				clonedBinding=binding.clone();
                                clonedBinding[bindingPositions.get(var)]=(Clazz)FromOWLAPIConverter.convert(cls);
                                if (!cls.equals(equivClass)){
                                	if (!isInList(clonedBinding, results, bindingPositions, vars))
                                		results.add(clonedBinding);
                                	loopEquivBindings.add(clonedBinding);	
                                }	
                			}*/
                            Integer polarity;
                            if (subClass.getVariablesInSignature().contains(var)) {
                                NegativePolarityClassVisitor clsVisitor = new NegativePolarityClassVisitor(var);
                                polarity = subClass.accept(clsVisitor);
                            }
                            else {
                                PositivePolarityClassVisitor clsVisitor = new PositivePolarityClassVisitor(var);
                                polarity = superClass.accept(clsVisitor);
                            }
                            if (polarity == 1) {
                                OWLClass currentClass = (OWLClass) binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory);
                                Set<OWLClass> classSet = m_reasoner.getSubClasses(currentClass, true).getFlattened();
                                Set<OWLClass> equivClasses = m_reasoner.getEquivalentClasses(currentClass).getEntities();
                                for (OWLClass cls : equivClasses) {
                                    if (!cls.equals(currentClass))
                                        classSet.add(cls);
                                }
                                Iterator<OWLClass> itr = classSet.iterator();
                                while (itr.hasNext()) {
                                    OWLClass element = itr.next();
                                    clonedBinding = binding.clone();
                                    clonedBinding[bindingPositions.get(var)] = (Clazz) FromOWLAPIConverter.convert(element);
                                    testedBindings.add(clonedBinding);
                                }
                            }
                            else if (polarity == 2) {
                                OWLClass currentClass = (OWLClass) binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory);
                                Set<OWLClass> classSet = m_reasoner.getSuperClasses(currentClass, true).getFlattened();
                                Set<OWLClass> equivClasses = m_reasoner.getEquivalentClasses(currentClass).getEntities();
                                for (OWLClass cls : equivClasses) {
                                    if (!cls.equals(currentClass))
                                        classSet.add(cls);
                                }
                                Iterator<OWLClass> itr = classSet.iterator();
                                while (itr.hasNext()) {
                                    OWLClass element = itr.next();
                                    clonedBinding = binding.clone();
                                    clonedBinding[bindingPositions.get(var)] = (Clazz) FromOWLAPIConverter.convert(element);
                                    testedBindings.add(clonedBinding);
                                }
                            }
                        }
                        else if (var instanceof ObjectPropertyVariable) {
                            Integer polarity;
                            if (subClass.getVariablesInSignature().contains(var)) {
                                NegativePolarityPropertyVisitor propVisitor = new NegativePolarityPropertyVisitor(var);
                                polarity = subClass.accept(propVisitor);
                            }
                            else {
                                PositivePolarityPropertyVisitor propVisitor = new PositivePolarityPropertyVisitor(var);
                                polarity = superClass.accept(propVisitor);
                            }
                            if (polarity == 1) {
                                OWLObjectProperty currentProperty = (OWLObjectProperty) binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory);
                                Set<OWLObjectPropertyExpression> propertySet = m_reasoner.getSubObjectProperties(currentProperty, true).getFlattened();
                                Set<OWLObjectPropertyExpression> equivProperties = m_reasoner.getEquivalentObjectProperties(currentProperty).getEntities();
                                for (OWLObjectPropertyExpression prop : equivProperties) {
                                    if (!prop.equals(currentProperty))
                                        propertySet.add(prop);
                                }
                                for (OWLObjectPropertyExpression propexpr : propertySet) {
                                    if (propexpr instanceof OWLObjectProperty) {
                                        clonedBinding = binding.clone();
                                        clonedBinding[bindingPositions.get(var)] = (ObjectProperty) FromOWLAPIConverter.convert((OWLObjectProperty) propexpr);
                                        testedBindings.add(clonedBinding);
                                    }
                                }
                            }
                            else if (polarity == 2) {
                                OWLObjectProperty currentProperty = (OWLObjectProperty) binding[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory);
                                Set<OWLObjectPropertyExpression> propertySet = m_reasoner.getSuperObjectProperties(currentProperty, true).getFlattened();
                                Set<OWLObjectPropertyExpression> equivProperties = m_reasoner.getEquivalentObjectProperties(currentProperty).getEntities();
                                for (OWLObjectPropertyExpression prop : equivProperties) {
                                    if (!prop.equals(currentProperty))
                                        propertySet.add(prop);
                                }
                                for (OWLObjectPropertyExpression propexpr : propertySet) {
                                    if (propexpr instanceof OWLObjectProperty) {
                                        clonedBinding = binding.clone();
                                        clonedBinding[bindingPositions.get(var)] = (ObjectProperty) FromOWLAPIConverter.convert((OWLObjectProperty) propexpr);
                                        testedBindings.add(clonedBinding);
                                    }
                                }
                            }
                        }
                        returnBindings.addAll(testedBindings);
                    }
                    testedBindings = new ArrayList<Atomic[]>();
                    bindingList.addAll(returnBindings);
                }
                else {
                    notEntailedList.add(currentBinding);
                }
            }
        }
        //System.out.println("EntailmentChecksNo=  "+entNo);
        return results;
    }

    public boolean isInList(Atomic[] binding, List<Atomic[]> list, Map<Variable, Integer> bindingPositions, List<Variable> vars) {
        for (int i = 0; i < list.size(); i++) {
            Atomic[] bind = list.get(i);
            if (isTheSameAssignment(bind, binding, bindingPositions))
                return true;
        }
        return false;
    }

    public boolean isInNotEntailedList(Atomic[] binding, List<Atomic[]> list, Map<Variable, Integer> bindingPositions, List<Variable> vars/*, SubClassOf axiom*/) {
        for (int i = 0; i < list.size(); i++) {
            Atomic[] bind = list.get(i);
            if (isTheSameAssignment(bind, binding, bindingPositions))
                return true;
    		/*int flag=0;
    		//int flag2=0;
    		ClassExpression subClass=axiom.getSubClassExpression();
        	ClassExpression superClass=axiom.getSuperClassExpression();
    		for (Variable var:vars) {
    	        if (bind[bindingPositions.get(var)]!=binding[bindingPositions.get(var)]) {
    	            //flag1++;
    	        	if (var instanceof ClassVariable) {
    	        		boolean polarity;
            			if (subClass.getVariablesInSignature().contains(var)){
                			NegativePolarityPropertyVisitor clsVisitor=new NegativePolarityPropertyVisitor(var);
                		    subClass.accept(clsVisitor);
                		    polarity=clsVisitor.getVarPolarity();
                		}    
                		else {
                    	    PositivePolarityClassVisitor clsVisitor=new PositivePolarityClassVisitor(var);
                    	    superClass.accept(clsVisitor);
                    		polarity=clsVisitor.getVarPolarity();    
                		}    
            			
                        if (polarity==Boolean.TRUE){
    	        		    OWLClass currentClass = (OWLClass)bind[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
    	        		    Set<OWLClass> owlclassSet=m_reasoner.getSubClasses(currentClass, false).getFlattened();
    	        		    //owlclassSet.addAll(m_reasoner.getEquivalentClasses(currentClass).getEntities());
    	        		    Set<Clazz> classSet=new HashSet<Clazz>();
    	                    for (OWLClass cls:owlclassSet) 
    	                        classSet.add((Clazz)FromOWLAPIConverter.convert(cls));
    	                    if (!classSet.contains((Clazz)binding[bindingPositions.get(var)])){
    	                        flag=1;
    	                        //break;
    	                    }
                        }
                        else {
                        	OWLClass currentClass = (OWLClass)bind[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
        	        		Set<OWLClass> owlclassSet=m_reasoner.getSuperClasses(currentClass, false).getFlattened();
        	        		//owlclassSet.addAll(m_reasoner.getEquivalentClasses(currentClass).getEntities());
        	        		Set<Clazz> classSet=new HashSet<Clazz>();
        	                for (OWLClass cls:owlclassSet) 
        	                    classSet.add((Clazz)FromOWLAPIConverter.convert(cls));
        	                if (!classSet.contains((Clazz)binding[bindingPositions.get(var)])){
        	                    flag=1;
        	                    //break;
        	                }
                        }
    	            }
    	            else if (var instanceof ObjectPropertyVariable) {
    	                boolean polarity;
                		if (subClass.getVariablesInSignature().contains(var)){
                    		NegativePolarityPropertyVisitor clsVisitor=new NegativePolarityPropertyVisitor(var);
                    		subClass.accept(clsVisitor);
                    		polarity=clsVisitor.getVarPolarity();
                        }    
                    	else {
                        	PositivePolarityPropertyVisitor clsVisitor=new PositivePolarityPropertyVisitor(var);
                        	superClass.accept(clsVisitor);
                        	polarity=clsVisitor.getVarPolarity();    
                    	}    
                		if (polarity==Boolean.TRUE){
    	            	    OWLObjectProperty currentProperty = (OWLObjectProperty)bind[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
    	        		    Set<OWLObjectPropertyExpression> owlpropertySet=m_reasoner.getSubObjectProperties(currentProperty, false).getFlattened();
    	        		    //owlpropertySet.addAll(m_reasoner.getEquivalentObjectProperties(currentProperty).getEntities());
    	        		    Set<ObjectProperty> propertySet=new HashSet<ObjectProperty>();
    	                    for (OWLObjectPropertyExpression prop:owlpropertySet){
    	                	    if (prop instanceof OWLObjectProperty)
    	            	            propertySet.add((ObjectProperty)FromOWLAPIConverter.convert(prop));
    	                    }
    	                    if (!propertySet.contains((ObjectProperty)binding[bindingPositions.get(var)])){
    	                        flag=1;
    	                        //break;
    	                    }
                		}
                		else {
                			OWLObjectProperty currentProperty = (OWLObjectProperty)bind[bindingPositions.get(var)].asOWLAPIObject(m_dataFactory); 
    	        		    Set<OWLObjectPropertyExpression> owlpropertySet=m_reasoner.getSuperObjectProperties(currentProperty, false).getFlattened();
    	        		    //owlpropertySet.addAll(m_reasoner.getEquivalentObjectProperties(currentProperty).getEntities());
    	        		    Set<ObjectProperty> propertySet=new HashSet<ObjectProperty>();
    	                    for (OWLObjectPropertyExpression prop:owlpropertySet){
    	                	    if (prop instanceof OWLObjectProperty)
    	            	            propertySet.add((ObjectProperty)FromOWLAPIConverter.convert(prop));
    	                    }
    	                    if (!propertySet.contains((ObjectProperty)binding[bindingPositions.get(var)])){
    	                        flag=1;
    	                        //break;
    	                    }
                		}
    	            }
    	        }	
    		}
    		if (flag==0)
    			return true;*/
        }
        return false;
    }

    public <O> O accept(DynamicQueryObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound) {
        return visitor.visit(this, bound);
    }

    //polarities: notDefined=0; positive=1; negative=2, both=3
    protected class PositivePolarityClassVisitor implements ClassAndPropertyExpressionVisitorEx<Integer> {

        protected Variable var;

        public PositivePolarityClassVisitor(Variable queryVar) {
            var = queryVar;
        }

        public Integer visit(ClassVariable object) {
            if (var.getVariable().equals(object.getVariable()))
                return 1;
            else return 0;
        }

        public Integer visit(Clazz object) {
            return 0;
        }

        public Integer visit(ObjectIntersectionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectUnionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
                else ;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectComplementOf object) {
            ClassExpression expr = object.getComplementedClassExpression();
            NegativePolarityClassVisitor npv = new NegativePolarityClassVisitor(var);
            return expr.accept(npv);
        }

        public Integer visit(ObjectOneOf object) {
            return 0;
        }

        public Integer visit(ObjectSomeValuesFrom object) {
            return object.getClassExpression().accept(this);
        }

        public Integer visit(ObjectAllValuesFrom object) {
            return object.getClassExpression().accept(this);
        }

        public Integer visit(ObjectHasValue object) {
            return 0;
        }

        public Integer visit(ObjectHasSelf object) {
            return 0;
        }

        public Integer visit(ObjectMinCardinality object) {
            return object.getClassExpression().accept(this);
        }

        public Integer visit(ObjectMaxCardinality object) {
            ClassExpression expr = object.getClassExpression();
            NegativePolarityClassVisitor npv = new NegativePolarityClassVisitor(var);
            return expr.accept(npv);
        }

        public Integer visit(ObjectExactCardinality object) {
            ClassExpression clsexpr = object.getClassExpression();
            if (clsexpr.getVariablesInSignature().contains(var.getVariable()))
                return 3;
            else return 0;
        }

        public Integer visit(DataSomeValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataAllValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataHasValue desc) {
            return 0;
        }

        public Integer visit(DataMinCardinality desc) {
            return 0;
        }

        public Integer visit(DataMaxCardinality desc) {
            return 0;
        }

        public Integer visit(DataExactCardinality desc) {
            return 0;
        }

        public Integer visit(ObjectProperty objectProperty) {
            return 0;
        }

        public Integer visit(ObjectInverseOf objectproperty) {
            return 0;
        }

        public Integer visit(ObjectPropertyChain objectProperty) {
            return 0;
        }

        public Integer visit(ObjectPropertyVariable objectPropertyVariable) {
            return 0;
        }
    }

    protected class NegativePolarityClassVisitor implements ClassAndPropertyExpressionVisitorEx<Integer> {
        protected Variable var;

        public NegativePolarityClassVisitor(Variable queryVar) {
            var = queryVar;
        }

        public Integer visit(ClassVariable object) {
            if (var.getVariable().equals(object.getVariable()))
                return 2;
            else return 0;
        }

        public Integer visit(Clazz object) {
            return 0;
        }

        public Integer visit(ObjectIntersectionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectUnionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
                else ;
            }
            if (pos != 0 && neg != 0) {
                return 3;
            }
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectComplementOf object) {
            ClassExpression expr = object.getComplementedClassExpression();
            PositivePolarityClassVisitor npv = new PositivePolarityClassVisitor(var);
            return expr.accept(npv);
        }

        public Integer visit(ObjectOneOf object) {
            return 0;
        }

        public Integer visit(ObjectSomeValuesFrom object) {
            return object.getClassExpression().accept(this);
        }

        public Integer visit(ObjectAllValuesFrom object) {
            return object.getClassExpression().accept(this);
        }

        public Integer visit(ObjectHasValue object) {
            return 0;
        }

        public Integer visit(ObjectHasSelf object) {
            return 0;
        }

        public Integer visit(ObjectMinCardinality object) {
            return object.getClassExpression().accept(this);
        }

        public Integer visit(ObjectMaxCardinality object) {
            ClassExpression expr = object.getClassExpression();
            PositivePolarityClassVisitor npv = new PositivePolarityClassVisitor(var);
            return expr.accept(npv);
        }

        public Integer visit(ObjectExactCardinality object) {
            ClassExpression clsexpr = object.getClassExpression();
            if (clsexpr.getVariablesInSignature().contains(var.getVariable()))
                return 3;
            else return 0;
        }

        public Integer visit(DataSomeValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataAllValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataHasValue desc) {
            return 0;
        }

        public Integer visit(DataMinCardinality desc) {
            return 0;
        }

        public Integer visit(DataMaxCardinality desc) {
            return 0;
        }

        public Integer visit(DataExactCardinality desc) {
            return 0;
        }

        public Integer visit(ObjectProperty objectProperty) {
            return 0;
        }

        public Integer visit(ObjectInverseOf objectproperty) {
            return 0;
        }

        public Integer visit(ObjectPropertyChain objectProperty) {
            return 0;
        }

        public Integer visit(ObjectPropertyVariable objectPropertyVariable) {
            return 0;
        }
    }

    protected class PositivePolarityPropertyVisitor implements ClassAndPropertyExpressionVisitorEx<Integer> {

        protected Variable var;

        public PositivePolarityPropertyVisitor(Variable queryVar) {
            var = queryVar;
        }

        public Integer visit(ObjectProperty object) {
            return 0;
        }

        public Integer visit(ObjectInverseOf object) {
            return object.getInvertedObjectProperty().accept(this);
        }

        public Integer visit(ObjectPropertyChain object) {
            return 0;
        }

        public Integer visit(ObjectPropertyVariable object) {
            if (var.getVariable().equals(object.getVariable()))
                return 1;
            else return 0;
        }

        public Integer visit(ClassVariable object) {
            return 0;
        }

        public Integer visit(Clazz object) {
            return 0;
        }

        public Integer visit(ObjectIntersectionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
                else ;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectUnionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
                else ;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectComplementOf object) {
            ClassExpression expr = object.getComplementedClassExpression();
            NegativePolarityPropertyVisitor npv = new NegativePolarityPropertyVisitor(var);
            return expr.accept(npv);
        }

        public Integer visit(ObjectOneOf object) {
            return 0;
        }

        public Integer visit(ObjectSomeValuesFrom object) {
            int polcl = object.getClassExpression().accept(this);
            int polprop = object.getObjectPropertyExpression().accept(this);
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            else if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectAllValuesFrom object) {
            int polcl = object.getClassExpression().accept(this);
            NegativePolarityPropertyVisitor npv1 = new NegativePolarityPropertyVisitor(var);
            int polprop = object.getObjectPropertyExpression().accept(npv1);
            if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectHasValue object) {
            return 0;
        }

        public Integer visit(ObjectHasSelf object) {
            return 0;
        }

        public Integer visit(ObjectMinCardinality object) {
            int polcl = object.getClassExpression().accept(this);
            int polprop = object.getObjectPropertyExpression().accept(this);
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            else if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectMaxCardinality object) {
            NegativePolarityPropertyVisitor npv = new NegativePolarityPropertyVisitor(var);
            int polcl = object.getClassExpression().accept(npv);
            NegativePolarityPropertyVisitor npv1 = new NegativePolarityPropertyVisitor(var);
            int polprop = object.getObjectPropertyExpression().accept(npv1);
            if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            else if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectExactCardinality object) {
            if (object.getClassExpression().getVariablesInSignature().contains(var.getVariable()) || object.getObjectPropertyExpression().getVariablesInSignature().contains(var.getVariable()))
                return 3;
            else return 0;
        }

        public Integer visit(DataSomeValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataAllValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataHasValue desc) {
            return 0;
        }

        public Integer visit(DataMinCardinality desc) {
            return 0;
        }

        public Integer visit(DataMaxCardinality desc) {
            return 0;
        }

        public Integer visit(DataExactCardinality desc) {
            return 0;
        }
    }

    protected class NegativePolarityPropertyVisitor implements ClassAndPropertyExpressionVisitorEx<Integer> {
        protected Variable var;

        public NegativePolarityPropertyVisitor(Variable queryVar) {
            var = queryVar;
        }

        public Integer visit(ObjectProperty object) {
            return 0;
        }

        public Integer visit(ObjectInverseOf object) {
            return object.getInvertedObjectProperty().accept(this);
        }

        public Integer visit(ObjectPropertyChain object) {
            return 0;
        }

        public Integer visit(ObjectPropertyVariable object) {
            if (var.getVariable().equals(object.getVariable()))
                return 2;
            else return 0;
        }

        public Integer visit(ClassVariable object) {
            return 0;
        }

        public Integer visit(Clazz object) {
            return 0;
        }

        public Integer visit(ObjectIntersectionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectUnionOf object) {
            int i = 0;
            int clsexprsize = object.getClassExpressions().size();
            int polarity[] = new int[clsexprsize];
            for (ClassExpression desc : object.getClassExpressions()) {
                polarity[i] = desc.accept(this);
                i++;
            }
            int pos = 0, neg = 0;
            for (int k = 0; k < clsexprsize; k++) {
                if (polarity[k] == 1)
                    pos++;
                else if (polarity[k] == 2)
                    neg++;
                else ;
            }
            if (pos != 0 && neg != 0)
                return 3;
            else if (pos != 0)
                return 1;
            else if (neg != 0)
                return 2;
            else return 0;
        }

        public Integer visit(ObjectComplementOf object) {
            ClassExpression expr = object.getComplementedClassExpression();
            PositivePolarityPropertyVisitor npv = new PositivePolarityPropertyVisitor(var);
            return expr.accept(npv);
        }

        public Integer visit(ObjectOneOf object) {
            return 0;
        }

        public Integer visit(ObjectSomeValuesFrom object) {
            int polcl = object.getClassExpression().accept(this);
            int polprop = object.getObjectPropertyExpression().accept(this);
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            else if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectAllValuesFrom object) {
            int polcl = object.getClassExpression().accept(this);
            PositivePolarityPropertyVisitor npv1 = new PositivePolarityPropertyVisitor(var);
            int polprop = object.getObjectPropertyExpression().accept(npv1);
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectHasValue object) {
            return 0;
        }

        public Integer visit(ObjectHasSelf object) {
            return 0;
        }

        public Integer visit(ObjectMinCardinality object) {
            int polcl = object.getClassExpression().accept(this);
            int polprop = object.getObjectPropertyExpression().accept(this);
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 3)
                return 3;
            else if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectMaxCardinality object) {
            NegativePolarityPropertyVisitor npv = new NegativePolarityPropertyVisitor(var);
            int polcl = object.getClassExpression().accept(npv);
            NegativePolarityPropertyVisitor npv1 = new NegativePolarityPropertyVisitor(var);
            int polprop = object.getObjectPropertyExpression().accept(npv1);
            if (polprop == 2 && (polcl == 0 || polcl == 2))
                return 2;
            else if (polprop == 2 && polcl == 1)
                return 3;
            else if (polprop == 2 && polcl == 3)
                return 3;
            else if (polprop == 0 && polcl == 1)
                return 1;
            else if (polprop == 0 && polcl == 2)
                return 2;
            else if (polprop == 0 && polcl == 1)
                return 3;
            if (polprop == 1 && (polcl == 0 || polcl == 1))
                return 1;
            else if (polprop == 1 && polcl == 2)
                return 3;
            else if (polprop == 1 && polcl == 3)
                return 3;
            else return 0;
        }

        public Integer visit(ObjectExactCardinality object) {
            if (object.getClassExpression().getVariablesInSignature().contains(var.getVariable()) || object.getObjectPropertyExpression().getVariablesInSignature().contains(var.getVariable()))
                return 3;
            else return 0;
        }

        public Integer visit(DataSomeValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataAllValuesFrom desc) {
            return 0;
        }

        public Integer visit(DataHasValue desc) {
            return 0;
        }

        public Integer visit(DataMinCardinality desc) {
            return 0;
        }

        public Integer visit(DataMaxCardinality desc) {
            return 0;
        }

        public Integer visit(DataExactCardinality desc) {
            return 0;
        }
    }

}
