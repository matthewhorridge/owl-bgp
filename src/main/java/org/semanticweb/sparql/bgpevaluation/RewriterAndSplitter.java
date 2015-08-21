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


package org.semanticweb.sparql.bgpevaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_AsymmetricObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ClassAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DataPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DataPropertyDomain;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DataPropertyRange;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DifferentIndividuals;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_DisjointClasses;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_EquivalentClasses;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_FunctionalDataProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_FunctionalObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_InverseFunctionalObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_InverseObjectProperties;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_IrreflexiveObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_NegativeDataPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyAssertion;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyDomain;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ObjectPropertyRange;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_ReflexiveObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SameIndividual;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SubClassOf;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SubObjectPropertyOf;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_SymmetricObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QO_TransitiveObjectProperty;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitor;
import org.semanticweb.sparql.owlbgp.model.Import;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.AnnotationPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.AsymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.axioms.ClassAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.DataPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.DatatypeDefinition;
import org.semanticweb.sparql.owlbgp.model.axioms.Declaration;
import org.semanticweb.sparql.owlbgp.model.axioms.DifferentIndividuals;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointUnion;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentClasses;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.EquivalentObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalDataProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.FunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.HasKey;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseFunctionalObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.InverseObjectProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.IrreflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeDataPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.NegativeObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyAssertion;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyDomain;
import org.semanticweb.sparql.owlbgp.model.axioms.ObjectPropertyRange;
import org.semanticweb.sparql.owlbgp.model.axioms.ReflexiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.SameIndividual;
import org.semanticweb.sparql.owlbgp.model.axioms.SubAnnotationPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubClassOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubDataPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubObjectPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SymmetricObjectProperty;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataOneOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;

public class RewriterAndSplitter implements AxiomVisitor {
    protected final OWLDataFactory m_dataFactory;
    protected final OWLOntologyGraph m_graph;
    protected final List<Axiom> m_bgpAxioms;
    protected final List<QueryObject<? extends Axiom>> m_rewritten;

    public RewriterAndSplitter(OWLOntologyGraph graph, Set<Axiom> bgpAxioms) {
        m_graph = graph;
        m_dataFactory = graph.getReasoner().getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        m_bgpAxioms = new ArrayList<Axiom>(bgpAxioms);
        m_rewritten = new ArrayList<QueryObject<? extends Axiom>>();
    }

    public Set<List<QueryObject<? extends Axiom>>> rewriteAndSplit() {
        while (!m_bgpAxioms.isEmpty()) {
            Axiom axiom = m_bgpAxioms.remove(m_bgpAxioms.size() - 1);
            axiom.accept(this);
        }
        return computeConnectedComponents();
    }

    protected Set<List<QueryObject<? extends Axiom>>> computeConnectedComponents() {
        Map<Integer, Set<Variable>> toComponentVars = new HashMap<Integer, Set<Variable>>();
        Map<Integer, Set<QueryObject<? extends Axiom>>> toComponent = new HashMap<Integer, Set<QueryObject<? extends Axiom>>>();
        int nextComponentIndex = 0;
        for (QueryObject<? extends Axiom> queryObject : m_rewritten) {
            Set<Variable> vars = queryObject.getAxiomTemplate().getVariablesInSignature();
            int key = -1;
            for (int i = 0; i < nextComponentIndex && key < 0; i++) {
                Set<Variable> componentVars = toComponentVars.get(i);
                if (componentVars != null) {
                    for (Variable var : vars)
                        if (componentVars.contains(var))
                            key = i;
                }
                //else key=1;
            }
            if (key < 0) {
                // new component
                Set<QueryObject<? extends Axiom>> objectsInComponent = new HashSet<QueryObject<? extends Axiom>>();
                objectsInComponent.add(queryObject);
                toComponentVars.put(nextComponentIndex, vars);
                toComponent.put(nextComponentIndex, objectsInComponent);
                nextComponentIndex++;
            }
            else {
                Set<QueryObject<? extends Axiom>> objectsInThisComponent = toComponent.get(key);
                Set<Variable> varsInThisComponent = toComponentVars.get(key);
                if (varsInThisComponent == null) {
                    System.out.println("Ups");
                }
                varsInThisComponent.addAll(vars);
                objectsInThisComponent.add(queryObject);
                for (int i = 0; i < nextComponentIndex; i++) {
                    if (i != key) {
                        Set<Variable> componentVars = toComponentVars.get(i);
                        if (componentVars != null) {
                            for (Variable var : varsInThisComponent) {
                                if (componentVars.contains(var)) {
                                    Set<QueryObject<? extends Axiom>> componentObjects = toComponent.get(i);
                                    objectsInThisComponent.addAll(componentObjects);
                                    toComponentVars.remove(i);
                                    toComponent.remove(i);
                                }
                            }
                        }
                    }
                }
            }
        }
        Set<List<QueryObject<? extends Axiom>>> components = new HashSet<List<QueryObject<? extends Axiom>>>();
        for (int i = 0; i < nextComponentIndex; i++) {
            Set<QueryObject<? extends Axiom>> objectsInThisComponent = toComponent.get(i);
            if (objectsInThisComponent != null)
                components.add(new ArrayList<QueryObject<? extends Axiom>>(objectsInThisComponent));
        }
        return components;
    }

    public void visit(Import axiom) {
    }

    public void visit(SubAnnotationPropertyOf axiom) {
    }

    public void visit(AnnotationPropertyDomain axiom) {
    }

    public void visit(AnnotationPropertyRange axiom) {
    }

    public void visit(AnnotationAssertion axiom) {
    }

    public void visit(Declaration axiom) {
    }

    public void visit(SubClassOf axiom) {
        //ClassExpression sub=axiom.getSubClassExpression();
        //ClassExpression sup=axiom.getSuperClassExpression();
        //m_rewritten.add(new QO_SubClassOf(SubClassOf.create(sub, sup), m_graph));
        ClassExpression sub = axiom.getSubClassExpression();
        ClassExpression sup = axiom.getSuperClassExpression();
        Set<ClassExpression> subs;
        Set<ClassExpression> sups;
        if (sup instanceof ObjectIntersectionOf)
            sups = ((ObjectIntersectionOf) sup).getClassExpressions();
        else
            sups = Collections.singleton(sup);
        if (sub instanceof ObjectUnionOf)
            subs = ((ObjectUnionOf) sub).getClassExpressions();
        else
            subs = Collections.singleton(sub);
        for (ClassExpression sub_i : subs)
            for (ClassExpression sup_j : sups)
                if (sub_i.equals(Clazz.THING)) {
                    if (sup_j instanceof ObjectAllValuesFrom) {
                        ObjectAllValuesFrom all = (ObjectAllValuesFrom) sup_j;
                        m_bgpAxioms.add(ObjectPropertyRange.create(all.getObjectPropertyExpression(), all.getClassExpression()));
                    }
                    else if (sup_j instanceof DataAllValuesFrom) {
                        DataAllValuesFrom all = (DataAllValuesFrom) sup_j;
                        m_bgpAxioms.add(DataPropertyRange.create(all.getDataPropertyExpression(), all.getDataRange()));
                    }
                }
                else if (sub_i instanceof ObjectSomeValuesFrom) {
                    ObjectSomeValuesFrom some = (ObjectSomeValuesFrom) sub_i;
                    if (some.getClassExpression().equals(Clazz.THING))
                        m_bgpAxioms.add(ObjectPropertyDomain.create(some.getObjectPropertyExpression(), sup_j));
                    else
                        m_rewritten.add(new QO_SubClassOf(SubClassOf.create(sub_i, sup_j), m_graph));
                }
                else if (sub_i instanceof DataSomeValuesFrom) {
                    DataSomeValuesFrom some = (DataSomeValuesFrom) sup_j;
                    if (some.getDataRange().equals(Datatype.RDFS_LITERAL))
                        m_bgpAxioms.add(DataPropertyDomain.create(some.getDataPropertyExpression(), sup_j));
                    else
                        m_rewritten.add(new QO_SubClassOf(SubClassOf.create(sub_i, sup_j), m_graph));
                }
                else
                    m_rewritten.add(new QO_SubClassOf(SubClassOf.create(sub_i, sup_j), m_graph));
    }

    public void visit(EquivalentClasses axiom) {
        ClassExpression[] classes = new ClassExpression[axiom.getClassExpressions().size()];
        axiom.getClassExpressions().toArray(classes);
        for (int i = 0; i < classes.length; i++)
            for (int j = i + 1; j < classes.length; j++)
                m_rewritten.add(new QO_EquivalentClasses(EquivalentClasses.create(classes[i], classes[j]), m_graph));
    }

    public void visit(DisjointClasses axiom) {
        ClassExpression[] classes = new ClassExpression[axiom.getClassExpressions().size()];
        axiom.getClassExpressions().toArray(classes);
        for (int i = 0; i < classes.length; i++)
            for (int j = i + 1; j < classes.length; j++)
                m_rewritten.add(new QO_DisjointClasses(DisjointClasses.create(classes[i], classes[j]), m_graph));
        //throw new UnsupportedOperationException("Disjoint classes axioms are not yet supported in the query pattern. ");
    }

    public void visit(DisjointUnion axiom) {
        ClassExpression c = (ClassExpression) axiom.getClazz();
        ClassExpression[] classes = new ClassExpression[axiom.getClassExpressions().size()];
        axiom.getClassExpressions().toArray(classes);
        // 1. add C implies CE1 or ... or CEn
        m_rewritten.add(new QO_SubClassOf(SubClassOf.create(c, ObjectUnionOf.create(classes)), m_graph));
        // 2. add CEi implies C
        for (int i = 0; i < classes.length; i++)
            m_rewritten.add(new QO_SubClassOf(SubClassOf.create(classes[i], c), m_graph));
        // 3. add DisjointClasses(CEi CEj)
        for (int i = 0; i < classes.length; i++)
            for (int j = i + 1; j < classes.length; j++)
                ; //m_rewritten.add(DisjointClasses.create(classes[i], classes[j]));
        throw new UnsupportedOperationException("Disjoint union axioms are not yet supported in the query pattern. ");
    }

    public void visit(SubObjectPropertyOf axiom) {
        m_rewritten.add(new QO_SubObjectPropertyOf(axiom, m_graph));
        //throw new UnsupportedOperationException("Sub-object property axioms are not yet supported in the query pattern. ");
    }

    public void visit(EquivalentObjectProperties axiom) {
        ObjectPropertyExpression[] opes = new ObjectPropertyExpression[axiom.getObjectPropertyExpressions().size()];
        axiom.getObjectPropertyExpressions().toArray(opes);
        for (int i = 0; i < opes.length; i++)
            for (int j = i + 1; j < opes.length; j++)
                ;//m_rewritten.add(new EquivalentObjectProperties(EquivalentObjectProperties.create(opes[i], opes[j]), m_graph));
        throw new UnsupportedOperationException("Equivalent object property axioms are not yet supported in the query pattern. ");
    }

    public void visit(DisjointObjectProperties axiom) {
        ObjectPropertyExpression[] opes = new ObjectPropertyExpression[axiom.getObjectPropertyExpressions().size()];
        axiom.getObjectPropertyExpressions().toArray(opes);
        for (int i = 0; i < opes.length; i++)
            for (int j = i + 1; j < opes.length; j++)
                ;//m_rewritten.add(new DisjointObjectProperties(DisjointObjectProperties.create(opes[i], opes[j]), m_graph));
        throw new UnsupportedOperationException("Disjoint object property axioms are not yet supported in the query pattern. ");
    }

    public void visit(InverseObjectProperties axiom) {
        ObjectPropertyExpression[] opes = new ObjectPropertyExpression[axiom.getObjectPropertyExpressions().size()];
        axiom.getObjectPropertyExpressions().toArray(opes);
        for (int i = 0; i < opes.length; i++)
            for (int j = i + 1; j < opes.length; j++)
                m_rewritten.add(new QO_InverseObjectProperties(InverseObjectProperties.create(opes[i], opes[j]), m_graph));
        //throw new UnsupportedOperationException("Inverse object property axioms are not yet supported in the query pattern. ");
    }

    public void visit(ObjectPropertyDomain axiom) {
        m_rewritten.add(new QO_ObjectPropertyDomain(axiom, m_graph));
    }

    public void visit(ObjectPropertyRange axiom) {
        m_rewritten.add(new QO_ObjectPropertyRange(axiom, m_graph));
    }

    public void visit(FunctionalObjectProperty axiom) {
        m_rewritten.add(new QO_FunctionalObjectProperty(axiom, m_graph));
    }

    public void visit(InverseFunctionalObjectProperty axiom) {
        m_rewritten.add(new QO_InverseFunctionalObjectProperty(axiom, m_graph));
    }

    public void visit(ReflexiveObjectProperty axiom) {
        m_rewritten.add(new QO_ReflexiveObjectProperty(axiom, m_graph));
    }

    public void visit(IrreflexiveObjectProperty axiom) {
        m_rewritten.add(new QO_IrreflexiveObjectProperty(axiom, m_graph));
    }

    public void visit(SymmetricObjectProperty axiom) {
        m_rewritten.add(new QO_SymmetricObjectProperty(axiom, m_graph));
    }

    public void visit(AsymmetricObjectProperty axiom) {
        m_rewritten.add(new QO_AsymmetricObjectProperty(axiom, m_graph));
    }

    public void visit(TransitiveObjectProperty axiom) {
        m_rewritten.add(new QO_TransitiveObjectProperty(axiom, m_graph));
    }

    public void visit(SubDataPropertyOf axiom) {
        //m_rewritten.add(new QO_SubDataPropertyOf(axiom));
        throw new UnsupportedOperationException("Sub-data property axioms are not yet supported in the query pattern. ");
    }

    public void visit(EquivalentDataProperties axiom) {
        DataPropertyExpression[] dpes = new DataPropertyExpression[axiom.getDataPropertyExpressions().size()];
        axiom.getDataPropertyExpressions().toArray(dpes);
        for (int i = 0; i < dpes.length; i++)
            for (int j = i + 1; j < dpes.length; j++)
                ;//m_rewritten.add(EquivalentDataProperties.create(opes[i], opes[j]));
        throw new UnsupportedOperationException("Equivalent data property axioms are not yet supported in the query pattern. ");
    }

    public void visit(DisjointDataProperties axiom) {
        DataPropertyExpression[] dpes = new DataPropertyExpression[axiom.getDataPropertyExpressions().size()];
        axiom.getDataPropertyExpressions().toArray(dpes);
        for (int i = 0; i < dpes.length; i++)
            for (int j = i + 1; j < dpes.length; j++)
                ;//m_rewritten.add(DisjointDataProperties.create(dpes[i], dpes[j]));
        throw new UnsupportedOperationException("Disjoint data property axioms are not yet supported in the query pattern. ");
    }

    public void visit(DataPropertyDomain axiom) {
        m_rewritten.add(new QO_DataPropertyDomain(axiom, m_graph));
        //throw new UnsupportedOperationException("Data property domain axioms are not yet supported in the query pattern. ");
    }

    public void visit(DataPropertyRange axiom) {
        m_rewritten.add(new QO_DataPropertyRange(axiom, m_graph));
        //throw new UnsupportedOperationException("Data property range axioms are not yet supported in the query pattern. ");
    }

    public void visit(FunctionalDataProperty axiom) {
        m_rewritten.add(new QO_FunctionalDataProperty(axiom, m_graph));
    }

    public void visit(DatatypeDefinition axiom) {
        ;//m_rewritten.add(axiom);
        throw new UnsupportedOperationException("Datatype definition axioms are not yet supported in the query pattern. ");
    }

    public void visit(HasKey axiom) {
        ;//m_rewritten.add(axiom);
        throw new UnsupportedOperationException("HasKey axioms are not yet supported in the query pattern. ");
    }

    public void visit(SameIndividual axiom) {
        Individual[] inds = new Individual[axiom.getIndividuals().size()];
        axiom.getIndividuals().toArray(inds);
        for (int i = 1; i < inds.length; i++) {
            SameIndividual sameInd = SameIndividual.create(inds[0], inds[i]);
            m_rewritten.add(new QO_SameIndividual(sameInd, m_graph));
        }
    }

    public void visit(DifferentIndividuals axiom) {
        Individual[] inds = new Individual[axiom.getIndividuals().size()];
        axiom.getIndividuals().toArray(inds);
        for (int i = 0; i < inds.length; i++)
            for (int j = i + 1; j < inds.length; j++)
                m_rewritten.add(new QO_DifferentIndividuals(DifferentIndividuals.create(inds[i], inds[j]), m_graph));
        //throw new UnsupportedOperationException("Different individual axioms are not yet supported in the query pattern. ");
    }

    public void visit(ClassAssertion axiom) {
        ClassExpression ce = axiom.getClassExpression();
        if (ce instanceof DataHasValue) {
            DataHasValue hasValue = (DataHasValue) ce;
            DataPropertyAssertion dpa = DataPropertyAssertion.create(hasValue.getDataPropertyExpression(), axiom.getIndividual(), hasValue.getLiteral());
            m_rewritten.add(new QO_DataPropertyAssertion(dpa, m_graph));
        }
        else if (ce instanceof DataSomeValuesFrom) {
            DataSomeValuesFrom someValuesFrom = (DataSomeValuesFrom) ce;
            DataRange dr = someValuesFrom.getDataRange();
            if (dr instanceof DataOneOf && ((DataOneOf) dr).getLiterals().size() == 1) {
                DataPropertyAssertion dpa = DataPropertyAssertion.create(someValuesFrom.getDataPropertyExpression(), axiom.getIndividual(), ((DataOneOf) dr).getLiterals().iterator().next());
                m_rewritten.add(new QO_DataPropertyAssertion(dpa, m_graph));
            }
            else
                m_rewritten.add(new QO_ClassAssertion(axiom, m_graph));
        }
        else if (ce instanceof ObjectIntersectionOf) {
            Set<ClassExpression> conjuncts = ((ObjectIntersectionOf) ce).getClassExpressions();
            for (ClassExpression conjunct : conjuncts)
                m_bgpAxioms.add(ClassAssertion.create(conjunct, axiom.getIndividual()));
        }
        else if (ce instanceof ObjectSomeValuesFrom) {
            ObjectSomeValuesFrom someValuesFrom = (ObjectSomeValuesFrom) ce;
            ClassExpression filler = someValuesFrom.getClassExpression();
            if (filler instanceof ObjectOneOf && ((ObjectOneOf) filler).getIndividuals().size() == 1) {
                ObjectPropertyAssertion opa = ObjectPropertyAssertion.create(someValuesFrom.getObjectPropertyExpression(), axiom.getIndividual(), ((ObjectOneOf) filler).getIndividuals().iterator().next());
                m_rewritten.add(new QO_ObjectPropertyAssertion(opa, m_graph));
            }
            else
                m_rewritten.add(new QO_ClassAssertion(axiom, m_graph));
        }
        else if (ce instanceof ObjectHasValue) {
            ObjectHasValue hasValue = (ObjectHasValue) ce;
            ObjectPropertyAssertion opa = ObjectPropertyAssertion.create(hasValue.getObjectPropertyExpression(), axiom.getIndividual(), hasValue.getIndividual());
            m_rewritten.add(new QO_ObjectPropertyAssertion(opa, m_graph));
        }
        else
            m_rewritten.add(new QO_ClassAssertion(axiom, m_graph));
    }

    public void visit(ObjectPropertyAssertion axiom) {
        ObjectPropertyExpression normalized = axiom.getObjectPropertyExpression().getNormalized();
        if (normalized instanceof ObjectInverseOf)
            m_rewritten.add(new QO_ObjectPropertyAssertion(ObjectPropertyAssertion.create(((ObjectInverseOf) normalized).getInvertedObjectProperty(), axiom.getObject(), axiom.getSubject()), m_graph));
        else
            m_rewritten.add(new QO_ObjectPropertyAssertion(ObjectPropertyAssertion.create(normalized, axiom.getSubject(), axiom.getObject()), m_graph));
    }

    public void visit(NegativeObjectPropertyAssertion axiom) {
        ObjectPropertyExpression normalized = axiom.getObjectPropertyExpression().getNormalized();
        if (normalized instanceof ObjectInverseOf)
            m_rewritten.add(new QO_NegativeObjectPropertyAssertion(NegativeObjectPropertyAssertion.create(((ObjectInverseOf) normalized).getInvertedObjectProperty(), axiom.getObject(), axiom.getSubject()), m_graph));
        else
            m_rewritten.add(new QO_NegativeObjectPropertyAssertion(NegativeObjectPropertyAssertion.create(normalized, axiom.getSubject(), axiom.getObject()), m_graph));
    }

    public void visit(DataPropertyAssertion axiom) {
        m_rewritten.add(new QO_DataPropertyAssertion(axiom, m_graph));
    }

    public void visit(NegativeDataPropertyAssertion axiom) {
        m_rewritten.add(new QO_NegativeDataPropertyAssertion(axiom, m_graph));
    }
}
