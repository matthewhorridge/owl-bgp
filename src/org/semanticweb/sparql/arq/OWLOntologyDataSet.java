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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.DatasetGraph;

public class OWLOntologyDataSet implements Dataset, DatasetGraph {
    protected static final String DEFAULT_IRI_PREFIX="http://sparql.org/defaultOntology";

    protected static int ontologyCounter=0;
    
    protected final OWLOntologyGraph defaultGraph;
    protected final Map<String,OWLOntologyGraph> namedGraphURIsToGraphs;
    
    public OWLOntologyDataSet(File defaultGraphFile) throws OWLOntologyCreationException {
        this(defaultGraphFile,null);
    } 
    public OWLOntologyDataSet(File defaultGraphFile, List<File> namedGraphFiles) throws OWLOntologyCreationException {
        OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
        OWLOntology defaultOntology=manager.loadOntologyFromOntologyDocument(defaultGraphFile);
        defaultGraph=new OWLOntologyGraph(defaultOntology);
        namedGraphURIsToGraphs=new HashMap<String,OWLOntologyGraph>();
        if (namedGraphFiles!=null) {
            for (File namedGraphFile : namedGraphFiles) {
                OWLOntology o=manager.loadOntologyFromOntologyDocument(namedGraphFile);
                namedGraphURIsToGraphs.put(o.getOntologyID().getOntologyIRI().toString(), new OWLOntologyGraph(o));
            }
        }
    }
    public OWLOntologyDataSet(OWLOntologyGraph defaultGraph, Map<String,OWLOntologyGraph> namedGraphURIsToGraphs) {
        this.defaultGraph=defaultGraph;
        if (namedGraphURIsToGraphs!=null)
            this.namedGraphURIsToGraphs=namedGraphURIsToGraphs;
        else 
            this.namedGraphURIsToGraphs=new HashMap<String, OWLOntologyGraph>();
    } 
    public OWLOntologyDataSet(OWLOntology defaultOntology, Map<String,OWLOntology> namedGraphURIsToOntologies) throws OWLOntologyCreationException {
        this.defaultGraph=new OWLOntologyGraph(defaultOntology);
        namedGraphURIsToGraphs=new HashMap<String,OWLOntologyGraph>();
        if (namedGraphURIsToOntologies!=null)
            for (String oName : namedGraphURIsToOntologies.keySet())
                namedGraphURIsToGraphs.put(oName, new OWLOntologyGraph(namedGraphURIsToOntologies.get(oName)));
    } 
    public OWLOntologyDataSet(String defaultGraphURI) throws OWLOntologyCreationException {
        this(defaultGraphURI,null);
    } 
    public OWLOntologyDataSet(String defaultGraphURI, List<String> namedGraphURIs) throws OWLOntologyCreationException {
        OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
        OWLOntology defaultOntology=manager.loadOntology(IRI.create(defaultGraphURI));
        defaultGraph=new OWLOntologyGraph(defaultOntology);
        namedGraphURIsToGraphs=new HashMap<String,OWLOntologyGraph>();
        if (namedGraphURIs!=null) {
            for (String namedGraphURI : namedGraphURIs) {
                OWLOntology o=manager.loadOntology(IRI.create(namedGraphURI));
                namedGraphURIsToGraphs.put(namedGraphURI, new OWLOntologyGraph(o));
            }
        }
    }
	public OWLOntologyDataSet(List<String> graphURIs, List<String> namedGraphURIs) throws OWLOntologyCreationException {
	    OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
        ontologyCounter++;
        OWLOntology ontology=manager.createOntology(IRI.create(DEFAULT_IRI_PREFIX+ontologyCounter+"/"));
        for (String uri : graphURIs) {
            OWLOntology o=manager.loadOntology(IRI.create(uri));
            manager.addAxioms(ontology, o.getLogicalAxioms());
            manager.removeOntology(o);
        }
        defaultGraph=new OWLOntologyGraph(ontology);
        namedGraphURIsToGraphs=new HashMap<String,OWLOntologyGraph>();
        if (namedGraphURIs!=null)
            for (String namedGraphURI : namedGraphURIs) {
                OWLOntology o=manager.loadOntology(IRI.create(namedGraphURI));
                namedGraphURIsToGraphs.put(namedGraphURI, new OWLOntologyGraph(o));
            }
	} 
    public OWLOntologyDataSet(List<String> graphURIs, Map<String,OWLOntologyGraph> namedGraphURIsToGraphs) throws OWLOntologyCreationException {
        OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
        ontologyCounter++;
        OWLOntology ontology=manager.createOntology(IRI.create(DEFAULT_IRI_PREFIX+ontologyCounter+"/"));
        for (String uri : graphURIs) {
            OWLOntology o=manager.loadOntology(IRI.create(uri));
            manager.addAxioms(ontology, o.getLogicalAxioms());
            manager.removeOntology(o);
        }
        defaultGraph=new OWLOntologyGraph(ontology);
        if (namedGraphURIsToGraphs!=null)
            this.namedGraphURIsToGraphs=namedGraphURIsToGraphs;
        else
            this.namedGraphURIsToGraphs=new HashMap<String, OWLOntologyGraph>();
    } 
    public Map<String,OWLOntologyGraph> getNamedGraphURIsToGraphs() {
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
        final Iterator<String> internalGraphNameIterator=namedGraphURIsToGraphs.keySet().iterator();
        Iterator<Node> graphNameIterator=new Iterator<Node>() {
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
    public int size() {
        return namedGraphURIsToGraphs.keySet().size()+1;
    }
}