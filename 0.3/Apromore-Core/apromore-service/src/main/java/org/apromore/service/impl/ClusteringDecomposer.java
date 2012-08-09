package org.apromore.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apromore.common.Constants;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.TreeVisitor;
import org.apromore.service.FragmentService;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.RPSTNodeCopy;
import org.apromore.util.FragmentUtil;
import org.apromore.util.GraphUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.graph.algo.tctree.TCType;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("ClusteringDecomposer")
@Transactional(propagation = Propagation.REQUIRED)
public class ClusteringDecomposer {

    private static final Logger log = LoggerFactory.getLogger(ClusteringDecomposer.class);

    @Autowired @Qualifier("FragmentService")
    private FragmentService fsrv;


    private Map<String, String> fragmentIdMap = new HashMap<String, String>();
    private TreeVisitor visitor = new TreeVisitor();


    public FragmentVersion decompose(CPF graph, List<String> fragmentIds) throws RepositoryException {
        TreeVisitor visitor = new TreeVisitor();
        OperationContext op = new OperationContext();
        op.setGraph(graph);
        op.setTreeVisitor(visitor);

        try {
            RPST rpst = GraphUtil.normalizeGraph(graph);
            RPSTNode rootFragment = rpst.getRoot();
            log.debug("Starting the processing of the root fragment...");
            FragmentVersion rootfv = process(rpst, rootFragment, op, fragmentIds, graph);
            fragmentIds.add(rootfv.getFragmentVersionId());
            return rootfv;

        } catch (Exception e) {
            String msg = "Failed to add root fragment version of the process model.";
            log.error(msg, e);
            throw new RepositoryException(msg, e);
        }
    }

    /**
     * Stores the fragment as it is
     * Removes all child fragments and replace them with their fragment codes
     * Returns the fragment code of the fragment
     * @param rpst
     * @param f
     * @param op
     * @param fragmentIds
     * @return
     * @throws org.apromore.exception.RepositoryException
     */
    public FragmentVersion process(RPST rpst, RPSTNode f, OperationContext op, List<String> fragmentIds, CPF g)
            throws RepositoryException {
        removeRundandantBoundaryConnectors(f, op);

        RPSTNodeCopy fCopy = new RPSTNodeCopy(f);
        fCopy.setReadableNodeType(FragmentUtil.getFragmentType(f));

        TCType fragmentType = f.getType();
        if (fragmentType.equals(TCType.T)) {
            FragmentVersion tempFV = new FragmentVersion();
            tempFV.setFragmentVersionId("");
            return tempFV;
        }

        Collection<RPSTNode> cs = rpst.getChildren(f);

        // child id -> uuid (we use uuid instead of a pocket id here as we are not using pockets in this simple decomposition)
        Map<String, String> childFragmentIds = new HashMap<String, String>();
        for (RPSTNode c : cs) {

            if (c.getType().equals(TCType.T)) {
                continue;
            }

            IVertex cEntry = c.getEntry();
            IVertex cExit = c.getExit();
            Collection<IVertex> cvs = c.getFragment().getVertices();
            for (IVertex cv : cvs) {
                if (!cv.equals(cEntry) && !cv.equals(cExit)) {
                    f.getFragment().removeVertex(cv);
                }
            }
            f.getFragment().removeEdges(c.getFragment().getEdges());

            FragmentVersion childFragment = process(rpst, c, op, fragmentIds, g);

            // if the fragment f has no edges after removing a child content, it is a P around B with only two connectors.
            // its connectors have been removed in the redundant connector removal phase and it is now equivalent to its B child.
            if (f.getFragment().getEdges().isEmpty()) {
                return childFragment;
            }

            fragmentIds.add(childFragment.getFragmentVersionId());
            childFragmentIds.put(UUID.randomUUID().toString(), childFragment.getFragmentVersionId());

            Vertex childFragmentComposite = new Vertex(childFragment.getFragmentVersionId());
            op.getGraph().setVertexProperty(childFragmentComposite.getId(), Constants.TYPE, Constants.FUNCTION);
            f.getFragment().addVertex(childFragmentComposite);

            f.getFragment().addEdge(cEntry, childFragmentComposite);
            f.getFragment().addEdge(childFragmentComposite, cExit);
        }

        Set<Vertex> vertices = new HashSet<Vertex>(f.getFragment().getVertices());
        Set<AbstractDirectedEdge> edges = new HashSet<AbstractDirectedEdge>(f.getFragment().getEdges());

        if (f.getEntry().getName() == null) {
            System.out.println(vertices);
        }

        log.debug("Computing the code for fragment: " + fCopy.getReadableNodeType() + " : " + fCopy.getNumVertices());
        String fragmentCode = "NOT COMPUTED";

        log.debug("Computation of code complete.");

        FragmentVersion fv = null;
        if (fv == null) {
            fv = fsrv.storeFragment(fragmentCode, fCopy, g);
            fsrv.addChildMappings(fv, childFragmentIds);
        }
        return fv;
    }

    private void removeRundandantBoundaryConnectors(RPSTNode f, OperationContext op) {
        if (f.getType().equals(TCType.P)) {
            CPF g = op.getGraph();
            if (Constants.CONNECTOR.equals(g.getVertexProperty(f.getEntry().getId(), Constants.TYPE))) {
                Collection<Vertex> entryPostset = f.getFragment().getDirectSuccessors(f.getEntry());
                if (entryPostset.size() == 1) {
                    f.getFragment().removeVertex(f.getEntry());

                    for (Vertex newEntry : entryPostset) {
                        f.setEntry(newEntry);
                        break;
                    }
                }
            }
            if (Constants.CONNECTOR.equals(g.getVertexProperty(f.getExit().getId(), Constants.TYPE))) {
                Collection<Vertex> exitPreset = f.getFragment().getDirectPredecessors(f.getExit());
                if (exitPreset.size() == 1) {
                    f.getFragment().removeVertex(f.getExit());

                    for (Vertex newExit : exitPreset) {
                        f.setExit(newExit);
                        break;
                    }
                }
            }
        }
    }

    /*
      * Fragment level decomposition is not supported by the simple decomposer.
      */
    public String decomposeFragment(CPF graph, List<String> fragmentIds)
            throws RepositoryException {
        throw new UnsupportedOperationException("Fragment level decomposition is not supported by the simple decomposer.");
    }
}