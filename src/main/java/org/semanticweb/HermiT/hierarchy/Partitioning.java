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
package org.semanticweb.HermiT.hierarchy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.model.AtomicConcept;
import org.semanticweb.HermiT.model.AtomicRole;
import org.semanticweb.HermiT.model.Individual;
import org.semanticweb.HermiT.tableau.ExtensionTable;
import org.semanticweb.HermiT.tableau.Node;
import org.semanticweb.HermiT.tableau.NodeType;
import org.semanticweb.HermiT.tableau.ReasoningTaskDescription;
import org.semanticweb.HermiT.tableau.Tableau;


public class Partitioning implements Serializable {
    private static final long serialVersionUID=-2959900333817197464L;

    protected ExtensionTable.Retrieval m_binaryTableSearch1Bound;
    protected ExtensionTable.Retrieval m_ternaryTableSearch12Bound;
    protected ExtensionTable.Retrieval m_ternaryTableSearch012Bound;
    protected ExtensionTable.Retrieval m_ternaryTableSearch1Bound;
    protected ExtensionTable.Retrieval m_ternaryTableSearch2Bound;
    protected final Map<Individual,Node> m_individualToNode;
    protected final Map<Node,Individual> m_nodeToIndividual;
    
    public Partitioning() {
        m_nodeToIndividual=new HashMap<Node,Individual>();
        m_individualToNode=new HashMap<Individual,Node>();
    }
    
    public Map<Integer,Set<Individual>> computePartitions(Reasoner reasoner) {
    	Tableau tableau=reasoner.getTableau();
        for (Individual individual : reasoner.getDLOntology().getAllIndividuals())
            m_individualToNode.put(individual,null);
        tableau.isSatisfiable(true, false, null, null, null, null, m_individualToNode, ReasoningTaskDescription.isABoxSatisfiable());
        for (Individual ind :  m_individualToNode.keySet()) 
        	m_nodeToIndividual.put(m_individualToNode.get(ind), ind);
        
    	Map<Integer,Set<Individual>> m_hashToIndividuals=new HashMap<Integer, Set<Individual>>();
        m_binaryTableSearch1Bound=tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
        Node node=tableau.getFirstTableauNode();
        while (node!=null) {
            if (node.isActive() && node.getNodeType()==NodeType.NAMED_NODE) {
                int hashCode=getLabelHashCode(node);
                Set<Individual> individualsForThatHash=m_hashToIndividuals.get(hashCode);
                if (individualsForThatHash==null) {
                	individualsForThatHash=new HashSet<Individual>();
                    m_hashToIndividuals.put(hashCode, individualsForThatHash);
                }
                individualsForThatHash.add(m_nodeToIndividual.get(node));
            }
            node=node.getNextTableauNode();
        }
        //System.out.println("The partitions are "+m_hashToIndividuals.size());
        // TODO: do something with the partitions
        return m_hashToIndividuals;
    }
    
    public Map<Integer,Set<List<Individual>>> computePropertyPartitions(Reasoner reasoner) {
    	Tableau tableau=reasoner.getTableau();
        for (Individual individual : reasoner.getDLOntology().getAllIndividuals())
            m_individualToNode.put(individual,null);
        tableau.isSatisfiable(true, false, null, null, null, null, m_individualToNode, ReasoningTaskDescription.isABoxSatisfiable());
        for (Individual ind :  m_individualToNode.keySet()) 
        	m_nodeToIndividual.put(m_individualToNode.get(ind), ind);
        
    	Map<Integer,Set<List<Individual>>> m_hashToIndividualsPairs=new HashMap<Integer, Set<List<Individual>>>();
    	// true means bound, so we have to specify the value for this column/array position before opening the retrieval
        m_ternaryTableSearch012Bound=tableau.getExtensionManager().getTernaryExtensionTable().createRetrieval(new boolean[] { false,false,false },ExtensionTable.View.TOTAL);
        m_ternaryTableSearch12Bound=tableau.getExtensionManager().getTernaryExtensionTable().createRetrieval(new boolean[] { false,true,true },ExtensionTable.View.TOTAL); 
        m_binaryTableSearch1Bound=tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
        
        //m_ternaryTableSearch012Bound.getBindingsBuffer();
        m_ternaryTableSearch012Bound.open();
        Object[] tupleBuffer=m_ternaryTableSearch012Bound.getTupleBuffer();
        while (!m_ternaryTableSearch012Bound.afterLast()) {
        	 //Object role=tupleBuffer[0];
        	 Node node1=(Node)tupleBuffer[1];
        	 Node node2=(Node)tupleBuffer[2];
        	 //if (ind1 instanceof Individual && ind2 instanceof Individual) {
        		 //Node node1=m_individualToNode.get(ind1);
        	     //Node node2=m_individualToNode.get(ind2);
                 if (node1.isActive() && node2.isActive() && node1.getNodeType()==NodeType.NAMED_NODE && node2.getNodeType()==NodeType.NAMED_NODE) {
                	 int hashCode=getPropsHashCode(node1, node2)+getLabelHashCode(node1)+getLabelHashCode(node2); 
                	 Set<List<Individual>> indPairsForThatHash = m_hashToIndividualsPairs.get(hashCode);
            	     if (indPairsForThatHash==null) {
            	        indPairsForThatHash=new HashSet<List<Individual>>();
            	        m_hashToIndividualsPairs.put(hashCode, indPairsForThatHash);
                     }
            	     List<Individual> indList=new ArrayList<Individual>();
            	     indList.add(m_nodeToIndividual.get(node1));
            	     indList.add(m_nodeToIndividual.get(node2));
            	     indPairsForThatHash.add(indList);
                 }
             //}
             m_ternaryTableSearch012Bound.next();
        }
        
        /*Node node1=tableau.getFirstTableauNode();
        
        while (node1!=null) {
        	Node node2=tableau.getFirstTableauNode();
        	while (node2!=null) {
        		if (node1.isActive() && node2.isActive() && node1.getNodeType()==NodeType.NAMED_NODE && node2.getNodeType()==NodeType.NAMED_NODE) {       			
        			int hashCode=getPropsHashCode(node1, node2)+getLabelHashCode(node1)+getLabelHashCode(node2);
        			Set<List<Individual>> indPairsForThatHash = m_hashToIndividualsPairs.get(hashCode);
           	        if (indPairsForThatHash==null) {
           	        	indPairsForThatHash=new HashSet<List<Individual>>();
           	            m_hashToIndividualsPairs.put(hashCode, indPairsForThatHash);
                    }
           	        List<Individual> indList=new ArrayList<Individual>();
           	        indList.add(m_nodeToIndividual.get(node1));
           	        indList.add(m_nodeToIndividual.get(node2));
           	        indPairsForThatHash.add(indList);
                }
        		node2=node2.getNextTableauNode();
        	}
        	node1=node1.getNextTableauNode();
        }*/	
        // TODO: do something with the partitions
        int hashSize=0;
        for (int k:m_hashToIndividualsPairs.keySet()) {
			Set<List<Individual>> indSet=m_hashToIndividualsPairs.get(k);
			hashSize=hashSize+indSet.size();
        }
        return m_hashToIndividualsPairs;
    }
    
    public Map<Integer,Set<Individual>> computesucIndPartitions(Reasoner reasoner) {
    	Tableau tableau=reasoner.getTableau();
        for (Individual individual : reasoner.getDLOntology().getAllIndividuals())
            m_individualToNode.put(individual,null);
        tableau.isSatisfiable(true, false, null, null, null, null, m_individualToNode, ReasoningTaskDescription.isABoxSatisfiable());
        for (Individual ind :  m_individualToNode.keySet()) 
        	m_nodeToIndividual.put(m_individualToNode.get(ind), ind);
        
    	Map<Integer,Set<Individual>> m_hashToFirstIndividualOfPair=new HashMap<Integer, Set<Individual>>();
    	// true means bound, so we have to specify the value for this column/array position before opening the retrieval
        m_ternaryTableSearch1Bound=tableau.getExtensionManager().getTernaryExtensionTable().createRetrieval(new boolean[] { false,true,false },ExtensionTable.View.TOTAL);
        m_binaryTableSearch1Bound=tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
        Node node=tableau.getFirstTableauNode();
        while (node!=null) {
        	if (node.isActive() && node.getNodeType()==NodeType.NAMED_NODE) {
        		int hashCode=getIndsSucHashCode(node)+getLabelHashCode(node);
        	    Set<Individual> indPairFirstIndForThatHash = m_hashToFirstIndividualOfPair.get(hashCode);
           	    if (indPairFirstIndForThatHash==null) {
           	        indPairFirstIndForThatHash=new HashSet<Individual>();
           	        m_hashToFirstIndividualOfPair.put(hashCode, indPairFirstIndForThatHash);
                }
           	    indPairFirstIndForThatHash.add(m_nodeToIndividual.get(node));
            }
        	node=node.getNextTableauNode();
        }
        // TODO: do something with the partitions
        return m_hashToFirstIndividualOfPair;
    }
    
    public Map<Integer,Set<Individual>> computepreIndPartitions(Reasoner reasoner) {
    	Tableau tableau=reasoner.getTableau();
        for (Individual individual : reasoner.getDLOntology().getAllIndividuals())
            m_individualToNode.put(individual,null);
        tableau.isSatisfiable(true, false, null, null, null, null, m_individualToNode, ReasoningTaskDescription.isABoxSatisfiable());
        for (Individual ind :  m_individualToNode.keySet()) 
        	m_nodeToIndividual.put(m_individualToNode.get(ind), ind);
        
    	Map<Integer,Set<Individual>> m_hashToSecondIndividualOfPair=new HashMap<Integer, Set<Individual>>();
    	// true means bound, so we have to specify the value for this column/array position before opening the retrieval
        m_ternaryTableSearch2Bound=tableau.getExtensionManager().getTernaryExtensionTable().createRetrieval(new boolean[] { false,false,true },ExtensionTable.View.TOTAL);
        m_binaryTableSearch1Bound=tableau.getExtensionManager().getBinaryExtensionTable().createRetrieval(new boolean[] { false,true },ExtensionTable.View.TOTAL);
        Node node=tableau.getFirstTableauNode();
        while (node!=null) {
        	if (node.isActive() && node.getNodeType()==NodeType.NAMED_NODE) {
        		int hashCode=getIndsPreHashCode(node)+getLabelHashCode(node);
        	    Set<Individual> indPairSecondIndForThatHash = m_hashToSecondIndividualOfPair.get(hashCode);
           	    if (indPairSecondIndForThatHash==null) {
           	        indPairSecondIndForThatHash=new HashSet<Individual>();
           	        m_hashToSecondIndividualOfPair.put(hashCode, indPairSecondIndForThatHash);
                }
           	    indPairSecondIndForThatHash.add(m_nodeToIndividual.get(node));
            }
        	node=node.getNextTableauNode();
        }
        // TODO: do something with the partitions
        return m_hashToSecondIndividualOfPair;
    }
    
    protected int getIndsPreHashCode(Node node) {
        Set<AtomicRole> label=new HashSet<AtomicRole>();
        m_ternaryTableSearch2Bound.getBindingsBuffer()[2]=node;
        m_ternaryTableSearch2Bound.open();
        Object[] tupleBuffer=m_ternaryTableSearch2Bound.getTupleBuffer();
        while (!m_ternaryTableSearch2Bound.afterLast()) {
            Object role=tupleBuffer[0];
            if (role instanceof AtomicRole) {
                label.add((AtomicRole)role);
            }    
            m_ternaryTableSearch2Bound.next();
        }
        return label.hashCode();
    }
    
    protected int getIndsSucHashCode(Node node) {
        Set<AtomicRole> label=new HashSet<AtomicRole>();
        m_ternaryTableSearch1Bound.getBindingsBuffer()[1]=node;
        m_ternaryTableSearch1Bound.open();
        Object[] tupleBuffer=m_ternaryTableSearch1Bound.getTupleBuffer();
        while (!m_ternaryTableSearch1Bound.afterLast()) {
            Object role=tupleBuffer[0];
            if (role instanceof AtomicRole) {
                label.add((AtomicRole)role);
            }    
            m_ternaryTableSearch1Bound.next();
        }
        return label.hashCode();
    }
    
    protected int getPropsHashCode(Node node1, Node node2) {
        Set<AtomicRole> label=new HashSet<AtomicRole>();
        m_ternaryTableSearch12Bound.getBindingsBuffer()[1]=node1;
        m_ternaryTableSearch12Bound.getBindingsBuffer()[2]=node2;
        m_ternaryTableSearch12Bound.open();
        Object[] tupleBuffer=m_ternaryTableSearch12Bound.getTupleBuffer();
        while (!m_ternaryTableSearch12Bound.afterLast()) {
            Object role=tupleBuffer[0];
            if (role instanceof AtomicRole) {
                label.add((AtomicRole)role);
            }    
            m_ternaryTableSearch12Bound.next();
        }
        return label.hashCode();
    }
    
    protected int getLabelHashCode(Node node) {
        Set<AtomicConcept> label=new HashSet<AtomicConcept>();
        m_binaryTableSearch1Bound.getBindingsBuffer()[1]=node;
        m_binaryTableSearch1Bound.open();
        Object[] tupleBuffer=m_binaryTableSearch1Bound.getTupleBuffer();
        while (!m_binaryTableSearch1Bound.afterLast()) {
            Object concept=tupleBuffer[0];
            if (concept instanceof AtomicConcept)
                label.add((AtomicConcept)concept);
            m_binaryTableSearch1Bound.next();
        }
        return label.hashCode();
    }
}