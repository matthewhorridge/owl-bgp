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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.LabelExistsException;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.util.Context;

public class OWLOntologyDataSet implements Dataset, DatasetGraph {
    protected static final String DEFAULT_IRI_PREFIX = "http://sparql.org/defaultOntology";

    protected static int ontologyCounter = 0;

    protected OWLOntologyGraph defaultGraph;
    protected final Map<String, OWLOntologyGraph> namedGraphURIsToGraphs;

    public OWLOntologyDataSet(File defaultGraphFile) throws OWLOntologyCreationException {
        this(defaultGraphFile, null);
    }

    public OWLOntologyDataSet(File defaultGraphFile, List<File> namedGraphFiles) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology defaultOntology = manager.loadOntologyFromOntologyDocument(defaultGraphFile);
        defaultGraph = new OWLOntologyGraph(defaultOntology);
        namedGraphURIsToGraphs = new HashMap<String, OWLOntologyGraph>();
        if (namedGraphFiles != null) {
            for (File namedGraphFile : namedGraphFiles) {
                OWLOntology o = manager.loadOntologyFromOntologyDocument(namedGraphFile);
                namedGraphURIsToGraphs.put(o.getOntologyID().getOntologyIRI().toString(), new OWLOntologyGraph(o));
            }
        }
    }

    public OWLOntologyDataSet(OWLOntologyGraph defaultGraph, Map<String, OWLOntologyGraph> namedGraphURIsToGraphs) {
        this.defaultGraph = defaultGraph;
        if (namedGraphURIsToGraphs != null)
            this.namedGraphURIsToGraphs = namedGraphURIsToGraphs;
        else
            this.namedGraphURIsToGraphs = new HashMap<String, OWLOntologyGraph>();
    }

    public OWLOntologyDataSet(OWLOntology defaultOntology, Map<String, OWLOntology> namedGraphURIsToOntologies) throws OWLOntologyCreationException {
        this.defaultGraph = new OWLOntologyGraph(defaultOntology);
        namedGraphURIsToGraphs = new HashMap<String, OWLOntologyGraph>();
        if (namedGraphURIsToOntologies != null)
            for (String oName : namedGraphURIsToOntologies.keySet())
                namedGraphURIsToGraphs.put(oName, new OWLOntologyGraph(namedGraphURIsToOntologies.get(oName)));
    }

    public OWLOntologyDataSet(String defaultGraphURI) throws OWLOntologyCreationException {
        this(defaultGraphURI, null);
    }

    public OWLOntologyDataSet(String defaultGraphURI, List<String> namedGraphURIs) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology defaultOntology = manager.loadOntology(IRI.create(defaultGraphURI));
        defaultGraph = new OWLOntologyGraph(defaultOntology);
        namedGraphURIsToGraphs = new HashMap<String, OWLOntologyGraph>();
        if (namedGraphURIs != null) {
            for (String namedGraphURI : namedGraphURIs) {
                OWLOntology o = manager.loadOntology(IRI.create(namedGraphURI));
                namedGraphURIsToGraphs.put(namedGraphURI, new OWLOntologyGraph(o));
            }
        }
    }

    public OWLOntologyDataSet(List<String> graphURIs, List<String> namedGraphURIs) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontologyCounter++;
        OWLOntology ontology = manager.createOntology(IRI.create(DEFAULT_IRI_PREFIX + ontologyCounter + "/"));
        for (String uri : graphURIs) {
            OWLOntology o = manager.loadOntology(IRI.create(uri));
            manager.addAxioms(ontology, o.getAxioms());
            manager.removeOntology(o);
        }
        defaultGraph = new OWLOntologyGraph(ontology);
        namedGraphURIsToGraphs = new HashMap<String, OWLOntologyGraph>();
        if (namedGraphURIs != null)
            for (String namedGraphURI : namedGraphURIs) {
                OWLOntology o = manager.loadOntology(IRI.create(namedGraphURI));
                namedGraphURIsToGraphs.put(namedGraphURI, new OWLOntologyGraph(o));
            }
    }

    public OWLOntologyDataSet(List<String> graphURIs, Map<String, OWLOntologyGraph> namedGraphURIsToGraphs) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontologyCounter++;
        OWLOntology ontology = manager.createOntology(IRI.create(DEFAULT_IRI_PREFIX + ontologyCounter + "/"));
        for (String uri : graphURIs) {
            OWLOntology o = manager.loadOntology(IRI.create(uri));
            manager.addAxioms(ontology, o.getLogicalAxioms());
            manager.removeOntology(o);
        }
        defaultGraph = new OWLOntologyGraph(ontology);
        if (namedGraphURIsToGraphs != null)
            this.namedGraphURIsToGraphs = namedGraphURIsToGraphs;
        else
            this.namedGraphURIsToGraphs = new HashMap<String, OWLOntologyGraph>();
    }

    public Map<String, OWLOntologyGraph> getNamedGraphURIsToGraphs() {
        return namedGraphURIsToGraphs;
    }

    public DatasetGraph asDatasetGraph() {
        return this;
    }

    public OWLOntologyGraph getDefaultGraph() {
        return defaultGraph;
    }

    public OWLOntologyGraph getNamedGraph(String uri) {
        return namedGraphURIsToGraphs.get(uri);
    }

    public void close() {
    }

    public void end() {
    }

    public boolean containsNamedModel(String uri) {
        return namedGraphURIsToGraphs.containsKey(uri);
    }

    public Model getDefaultModel() {
        return null;
    }

    public Lock getLock() {
        return null;
    }

    public Model getNamedModel(String uri) {
        return null;
    }

    public void addNamedModel(String uri, Model model) throws LabelExistsException {
        throw new UnsupportedOperationException("Named models are not supported. ");
    }

    public void removeNamedModel(String uri) {
        throw new UnsupportedOperationException("Named models are not supported. ");
    }

    public void replaceNamedModel(String uri, Model model) {
        throw new UnsupportedOperationException("Named models are not supported. ");
    }

    public Iterator<String> listNames() {
        return namedGraphURIsToGraphs.keySet().iterator();
    }

    public Graph getGraph(Node graphNode) {
        return namedGraphURIsToGraphs.get(graphNode.getURI());
    }

    public boolean containsGraph(Node graphNode) {
        return namedGraphURIsToGraphs.containsKey(graphNode.getURI());
    }

    public Iterator<Node> listGraphNodes() {
        final Iterator<String> internalGraphNameIterator = namedGraphURIsToGraphs.keySet().iterator();
        Iterator<Node> graphNameIterator = new Iterator<Node>() {
            public boolean hasNext() {
                return internalGraphNameIterator.hasNext();
            }

            public Node next() {
                return Node.createURI(internalGraphNameIterator.next());
            }

            public void remove() {
                throw new UnsupportedOperationException("Internal Error: This dataset models do not allow removal from an iterator. ");
            }
        };
        return graphNameIterator;
    }

    public long size() {
        return namedGraphURIsToGraphs.keySet().size() + 1;
    }

    public void setDefaultGraph(Graph g) {
        if (g instanceof OWLOntologyGraph)
            defaultGraph = (OWLOntologyGraph) g;
        else
            throw new IllegalArgumentException("Could not set the default graph because only instances of OWLOntologyGraph are supported. ");
    }

    public void setDefaultModel(Model model) {
        throw new IllegalArgumentException("Setting of default models is not supported. ");
    }

    public void addGraph(Node graphName, Graph graph) {
        if (graph instanceof OWLOntologyGraph)
            namedGraphURIsToGraphs.put(graphName.getURI(), (OWLOntologyGraph) graph);
        else
            throw new IllegalArgumentException("Could not add the named graph because only instances of OWLOntologyGraph are supported. ");
    }

    public void removeGraph(Node graphName) {
        if (graphName.isURI())
            namedGraphURIsToGraphs.remove(graphName.getURI());
        else
            throw new IllegalArgumentException("Could not add the named graph because the node for the graph name has to be a URI node, but graphName has not type URI: " + graphName);
    }

    public void add(Node g, Node s, Node p, Node o) {
        throw new UnsupportedOperationException("The addition of triples and quads is not supported for instances of OWLOntologyGraph. ");
    }

    public void add(Quad quad) {
        throw new UnsupportedOperationException("The addition of triples and quads is not supported for instances of OWLOntologyGraph. ");
    }

    public void delete(Quad quad) {
        throw new UnsupportedOperationException("The removal of triples and quads is not supported for instances of OWLOntologyGraph. ");
    }

    public void delete(Node g, Node s, Node p, Node o) {
        throw new UnsupportedOperationException("The removal of triples and quads is not supported for instances of OWLOntologyGraph. ");
    }

    public void deleteAny(Node g, Node s, Node p, Node o) {
        throw new UnsupportedOperationException("The removal of triples and quads is not supported for instances of OWLOntologyGraph. ");
    }

    public Iterator<Quad> find() {
        throw new UnsupportedOperationException("Finding quads is not supported for instances of OWLOntologyGraph. ");
    }

    public Iterator<Quad> find(Quad quad) {
        throw new UnsupportedOperationException("Finding quads is not supported for instances of OWLOntologyGraph. ");
    }

    public Iterator<Quad> find(Node g, Node s, Node p, Node o) {
        throw new UnsupportedOperationException("Finding quads is not supported for instances of OWLOntologyGraph. ");
    }

    public Iterator<Quad> findNG(Node g, Node s, Node p, Node o) {
        throw new UnsupportedOperationException("Finding quads is not supported for instances of OWLOntologyGraph. ");
    }

    public boolean contains(Node g, Node s, Node p, Node o) {
        return false;
    }

    public boolean contains(Quad quad) {
        return false;
    }

    public boolean isEmpty() {
        return defaultGraph == null && namedGraphURIsToGraphs.isEmpty();
    }

    public Context getContext() {
        return null;
    }

    public boolean supportsTransactions() {
        return false;
    }

    public void begin(ReadWrite readWrite) {
    }

    public void commit() {
    }

    public void abort() {
    }

    public boolean isInTransaction() {
        return false;
    }
}
