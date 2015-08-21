package org.semanticweb.sparql.bgpevaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.bgpevaluation.monitor.Monitor;
import org.semanticweb.sparql.bgpevaluation.queryobjects.QueryObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.uncommons.maths.combinatorics.PermutationGenerator;

public class PlanChecker extends QueryEvaluator {

    public PlanChecker(OWLOntologyGraph graph, Monitor monitor) {
        super(graph, monitor);
    }

    @Override
    public List<Atomic[]> execute(List<QueryObject<? extends Axiom>> connectedComponent, Map<Variable, Integer> positionInTuple, List<Atomic[]> bindings) {
        Atomic[] initialBinding = bindings.get(0);
        FileWriter fstream;
        try {
            fstream = new FileWriter("output.txt");
        } catch (IOException e) {
            System.err.println("Error: Cannot create file output.txt");
            e.printStackTrace();
            return bindings;
        }
        BufferedWriter out = new BufferedWriter(fstream);
        PermutationGenerator<QueryObject<? extends Axiom>> generator = new PermutationGenerator<QueryObject<? extends Axiom>>(connectedComponent);
        List<QueryObject<? extends Axiom>> cheapestOrder = new ArrayList<QueryObject<? extends Axiom>>();
        long minTime = Long.MAX_VALUE;
        int permNum = 0;
        try {
            while (generator.hasMore()) {//new permutation of query atoms
                bindings.clear();
                bindings.add(initialBinding);
                List<QueryObject<? extends Axiom>> atomList = generator.nextPermutationAsList();
                Set<Variable> boundVar = new HashSet<Variable>();
                boolean firstLoop = true;
                boolean valid = true;
                Set<Variable> varscopy = new HashSet<Variable>();
                for (QueryObject<? extends Axiom> at : atomList) {
                    Set<Variable> vars = at.getAxiomTemplate().getVariablesInSignature();
                    varscopy.addAll(vars);
                    if (!firstLoop)
                        vars.retainAll(boundVar);
                    if (vars.isEmpty()) { // plans where we jump to an unconnected part in the query graph are not considered
                        valid = false;
                        break;
                    }
                    boundVar.addAll(varscopy);
                    firstLoop = false;
                }
                if (valid) {
                    permNum++;
                    System.out.println("permNumber:  " + permNum);
                    long t = System.currentTimeMillis();
                    out.write("Ordering:  ");
                    System.out.println("Ordering:  ");
                    for (QueryObject<? extends Axiom> cheapest : atomList) {
                        out.newLine();
                        out.write(cheapest.getAxiomTemplate().toString() + " ");
                        System.out.println(cheapest.getAxiomTemplate().toString() + " ");
                        if (!bindings.isEmpty())
                            bindings = cheapest.computeBindings(bindings, positionInTuple);
                    }
                    long y = System.currentTimeMillis() - t;
                    if (y < minTime) {
                        minTime = y;
                        cheapestOrder = atomList;
                    }
                    out.newLine();
                    out.write("RunningTime: " + y + " ms.");
                    out.newLine();
                    out.newLine();
                    System.out.println("RunningTime: " + y + " ms.");
                }
            }
            out.newLine();
            out.write("The cheapestOrdering is: ");
            System.out.println("The cheapestOrdering is: ");
            for (QueryObject<? extends Axiom> cheapest : cheapestOrder) {
                out.write(cheapest.getAxiomTemplate().toString() + " ");
                out.newLine();
                System.out.println(cheapest.getAxiomTemplate().toString() + " ");
            }
            out.write("with running time " + minTime + " ms.");
            out.newLine();
            out.newLine();
            System.out.println("with running time " + minTime + " ms.");
            out.write("The number of valid orderings is " + permNum + " .");
            out.close();
            System.out.println("The number of valid orderings is " + permNum + " .");
        } catch (IOException e) {
            System.err.println("Error: Cannot write to file output.txt");
            e.printStackTrace();
        }
        return bindings;
    }

}
