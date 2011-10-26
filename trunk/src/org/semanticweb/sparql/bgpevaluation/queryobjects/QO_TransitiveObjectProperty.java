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

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.axioms.TransitiveObjectProperty;

public class QO_TransitiveObjectProperty extends QO_ObjectPropertyAxiom<TransitiveObjectProperty> {

    public QO_TransitiveObjectProperty(TransitiveObjectProperty axiomTemplate, OWLOntologyGraph graph) {
        super(axiomTemplate, graph);
    }
    protected OWLAxiom getEntailmentAxiom(OWLObjectPropertyExpression ope) {
        return m_dataFactory.getOWLTransitiveObjectPropertyAxiom(ope);
    }
    public <O> O accept(QueryObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }
}