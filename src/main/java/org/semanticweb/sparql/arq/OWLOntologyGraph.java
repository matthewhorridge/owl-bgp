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

package org.semanticweb.sparql.arq;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.OWLBGPHermiT;
import org.semanticweb.HermiT.monitor.CountingMonitor;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.sparql.bgpevaluation.Skolemizer;
import org.semanticweb.sparql.owlbgp.model.FromOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class OWLOntologyGraph implements Graph {

    protected final ToOWLAPIConverter m_toOWLAPIConverter;
    protected final Set<String> m_skolemConstants;
    protected final Set<Clazz> m_classes;
    protected final Set<Datatype> m_datatypes;
    protected final Set<ObjectProperty> m_objectProperties;
    protected final Set<DataProperty> m_dataProperties;
    protected final Set<AnnotationProperty> m_annotationProperties;
    protected final Set<NamedIndividual> m_individuals;
    protected final Set<Literal> m_literals;
    protected final Set<ObjectProperty> m_knownFunctionalObjectProperties;
    protected final Set<ObjectProperty> m_toTestFunctionalObjectProperties;
    protected final OWLReasoner m_reasoner;
    protected final CountingMonitor m_countingMonitor;

    public OWLOntologyGraph(final OWLOntology ontology) {
        m_toOWLAPIConverter = new ToOWLAPIConverter(ontology.getOWLOntologyManager().getOWLDataFactory());
        Skolemizer skolemizer = new Skolemizer();
        OWLOntology skolomized = skolemizer.skolemize(ontology);
        m_skolemConstants = skolemizer.getSkolems();
        m_literals = skolemizer.getLiterals();
        m_classes = new HashSet<Clazz>();
        for (OWLClass cls : skolomized.getClassesInSignature(true)) {
            if (cls.isOWLThing())
                m_classes.add(Clazz.THING);
            else if (cls.isOWLNothing())
                m_classes.add(Clazz.NOTHING);
            else
                m_classes.add(Clazz.create(cls.getIRI().toQuotedString()));
        }
        m_datatypes = new HashSet<Datatype>();
        for (OWLDatatype dt : skolomized.getDatatypesInSignature(true))
            if (!dt.isBuiltIn())
                m_datatypes.add(Datatype.create(dt.toString()));
        m_objectProperties = new HashSet<ObjectProperty>();
        for (OWLObjectProperty op : skolomized.getObjectPropertiesInSignature(true))
            if (op.isOWLTopObjectProperty())
                m_objectProperties.add(ObjectProperty.TOP_OBJECT_PROPERTY);
            else if (op.isOWLBottomObjectProperty())
                m_objectProperties.add(ObjectProperty.BOTTOM_OBJECT_PROPERTY);
            else
                m_objectProperties.add(ObjectProperty.create(op.toString()));
        m_dataProperties = new HashSet<DataProperty>();
        for (OWLDataProperty dp : skolomized.getDataPropertiesInSignature(true))
            if (dp.isOWLTopDataProperty())
                m_dataProperties.add(DataProperty.TOP_DATA_PROPERTY);
            else if (dp.isOWLBottomDataProperty())
                m_dataProperties.add(DataProperty.BOTTOM_DATA_PROPERTY);
            else
                m_dataProperties.add(DataProperty.create(dp.toString()));
        m_annotationProperties = new HashSet<AnnotationProperty>();
        for (OWLAnnotationProperty ap : skolomized.getAnnotationPropertiesInSignature())
            m_annotationProperties.add(AnnotationProperty.create(ap.toString()));
        m_individuals = new HashSet<NamedIndividual>();
        for (OWLNamedIndividual ind : skolomized.getIndividualsInSignature(true))
            m_individuals.add(NamedIndividual.create(ind.toString()));
        m_knownFunctionalObjectProperties = skolemizer.getToldFunctionalObjectProperties();
        Set<ObjectProperty> toTestFunctionalObjectProperties = new HashSet<ObjectProperty>(m_objectProperties);
        toTestFunctionalObjectProperties.removeAll(m_knownFunctionalObjectProperties);
        m_toTestFunctionalObjectProperties = toTestFunctionalObjectProperties;
        m_countingMonitor = new CountingMonitor();
        Configuration c = new Configuration();
        c.monitor = m_countingMonitor;
        c.ignoreUnsupportedDatatypes = true;
        m_reasoner = new OWLBGPHermiT(c, skolomized);
        //m_reasoner=new PelletReasonerFactory().createReasoner(skolomized);
    }

    public Set<String> getSkolemConstants() {
        return this.m_skolemConstants;
    }

    public OWLReasoner getReasoner() {
        return this.m_reasoner;
    }

    public void precompute(InferenceType... inferenceTypes) {
        m_reasoner.precomputeInferences(inferenceTypes);
        if (m_reasoner.isPrecomputed(InferenceType.OBJECT_PROPERTY_HIERARCHY)) {
            Set<ObjectProperty> inferredFunctional = new HashSet<ObjectProperty>();
            for (ObjectProperty op : m_knownFunctionalObjectProperties) {
                OWLObjectProperty owlOp = (OWLObjectProperty) op.asOWLAPIObject(m_toOWLAPIConverter);
                for (OWLObjectPropertyExpression ope : m_reasoner.getSubObjectProperties(owlOp, false).getFlattened())
                    if (!ope.isAnonymous())
                        inferredFunctional.add((ObjectProperty) FromOWLAPIConverter.convert(ope));
                for (OWLObjectPropertyExpression ope : m_reasoner.getEquivalentObjectProperties(owlOp).getEntities())
                    if (!ope.isAnonymous())
                        inferredFunctional.add((ObjectProperty) FromOWLAPIConverter.convert(ope));
            }
            m_knownFunctionalObjectProperties.addAll(inferredFunctional);
        }
    }

    public OWLOntology getOntology() {
        return m_reasoner.getRootOntology();
    }

    public Set<Clazz> getClassesInSignature() {
        return m_classes;
    }

    public Set<Datatype> getDatatypesInSignature() {
        return m_datatypes;
    }

    public Set<ObjectProperty> getObjectPropertiesInSignature() {
        return m_objectProperties;
    }

    public Set<ObjectProperty> getKnownFunctionalObjectProperties() {
        return m_knownFunctionalObjectProperties;
    }

    public Set<ObjectProperty> getToTestFunctionalObjectProperties() {
        return m_toTestFunctionalObjectProperties;
    }

    public Set<DataProperty> getDataPropertiesInSignature() {
        return m_dataProperties;
    }

    public Set<AnnotationProperty> getAnnotationPropertiesInSignature() {
        return m_annotationProperties;
    }

    public Set<NamedIndividual> getIndividualsInSignature() {
        return m_individuals;
    }

    public Set<Literal> getLiteralsInSignature() {
        return m_literals;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean contains(Triple t) {
        return false;
    }

    @Override
    public boolean contains(Node s, Node p, Node o) {
        return false;
    }

    @Override
    public void delete(Triple t) throws DeleteDeniedException {
        throw new DeleteDeniedException("HermiT does not accept deletion of triples.");
    }

    @Override
    public boolean dependsOn(Graph other) {
        return false;
    }

    @Override
    public ExtendedIterator<Triple> find(TripleMatch m) {
        return null;
    }

    @Override
    public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
        return null;
    }

    @Override
    public BulkUpdateHandler getBulkUpdateHandler() {
        return null;
    }

    @Override
    public Capabilities getCapabilities() {
        return null;
    }

    @Override
    public GraphEventManager getEventManager() {
        return null;
    }

    @Override
    public PrefixMapping getPrefixMapping() {
        return null;
    }

    @Override
    public GraphStatisticsHandler getStatisticsHandler() {
        return null;
    }

    @Override
    public TransactionHandler getTransactionHandler() {
        return null;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean isIsomorphicWith(Graph g) {
        return false;
    }

    @Override
    public int size() {
        return m_reasoner.getRootOntology().getAxiomCount();
    }

    @Override
    public void add(Triple t) throws AddDeniedException {
        throw new AddDeniedException("HermiT does not accept additions to graphs.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Clearing graphs is not yet supported.");
    }

    @Override
    public void remove(Node s, Node p, Node o) {
        throw new UnsupportedOperationException("Removing triples from OWLONtologyGraph instances is not supported. ");
    }
}
