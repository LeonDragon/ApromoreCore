package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.List;
import javax.xml.bind.JAXBElement;

// Local packages
import org.apromore.canoniser.bpmn.cpf.*;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.*;
import org.omg.spec.bpmn._20100524.model.*;

/**
 * This class fakes the common superclass that {@link TProcess} and {@link TSubProcess} should've had.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class ProcessWrapper {

    private final String id;
    private final List<JAXBElement<? extends TArtifact>> artifact;
    private final List<JAXBElement<? extends TFlowElement>> flowElement;
    private final List<TLaneSet> laneSet;

    // Constructors

    /**
     * Wrap a {@link TProcess}.
     *
     * @param process  wrapped instance
     */
    public ProcessWrapper(final TProcess process) {
        id          = process.getId();
        artifact    = process.getArtifact();
        flowElement = process.getFlowElement();
        laneSet     = process.getLaneSet();
    }

    /**
     * Wrap a {@link TSubProcess}.
     *
     * @param subprocess  wrapped instance
     * @param processId  identifier to be used for the implicit process within the subprocess
     */
    public ProcessWrapper(final TSubProcess subprocess, final String processId) {
        id          = processId;
        artifact    = subprocess.getArtifact();
        flowElement = subprocess.getFlowElement();
        laneSet     = subprocess.getLaneSet();
    }

    // Accessor methods

    /** @return <code>id</code> property */
    public String getId() { return id; }

    /** @return <code>artifact</code> property */
    public List<JAXBElement<? extends TArtifact>> getArtifact() { return artifact; }

    /** @return <code>flowElement</code> property */
    public List<JAXBElement<? extends TFlowElement>> getFlowElement() { return flowElement; }

    /** @return <code>laneSet</code> property */
    public List<TLaneSet> getLaneSet() { return laneSet; }

    // Constructor methods used by subclasses

    /**
     * Add the lanes, nodes and so forth to a {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the {@link TProcess} or {@link TSubProcess} to be populated
     * @param net  the CPF net which the <code>process</code> corresponds to
     * @param initializer  BPMN document construction state
     * @throws CanoniserException if the child elements can't be added
     */
    // TODO - EITHER - make this an instance method of ProcessWrapper, replacing the "process" parameter
    //      - OR     - make this an instance method of Initializer, replacing the "initializer" parameter
    public static void populateProcess(final ProcessWrapper process,
                                       final NetType net,
                                       final Initializer initializer) throws CanoniserException {

        // Add the CPF ResourceType lattice as a BPMN Lane hierarchy
        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : initializer.getResourceTypes()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().isEmpty()) {
                 laneSet.getLane().add(new BpmnLane(cpfResourceType, initializer));
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            process.getLaneSet().add(laneSet);
        }

        // Add the CPF Edges as BPMN SequenceFlows
        for (EdgeType edge : net.getEdge()) {
            TSequenceFlow sequenceFlow = new BpmnSequenceFlow((CpfEdgeType) edge, initializer);
            process.getFlowElement().add(initializer.getFactory().createSequenceFlow(sequenceFlow));
        }

        // Add the CPF Objects as BPMN DataObjects
        for (ObjectType object : net.getObject()) {
            CpfObjectType cpfObject = (CpfObjectType) object;

            if (cpfObject.getDataStore() != null) {
                process.getFlowElement().add(initializer.getFactory().createDataStoreReference(new BpmnDataStoreReference(cpfObject, initializer)));
            } else {
                process.getFlowElement().add(initializer.getFactory().createDataObject(new BpmnDataObject(cpfObject, initializer)));
            }
        }

        // Add the CPF Nodes as BPMN FlowNodes
        for (NodeType node : net.getNode()) {
            JAXBElement<? extends TFlowNode> flowNode = ((CpfNodeType) node).toBpmn(initializer);
            process.getFlowElement().add(flowNode);

            if (node instanceof WorkType) {

                // Populate the lane flowNodeRefs
                for (ResourceTypeRefType resourceTypeRef : ((WorkType) node).getResourceTypeRef()) {
                    TLane lane = (TLane) initializer.findElement(resourceTypeRef.getResourceTypeId());

                    // Create a fake BPMN element with the same ID(!) as the lane element
                    TTask fake = initializer.getFactory().createTTask();
                    fake.setId(flowNode.getValue().getId());
                    JAXBElement<TFlowNode> jFake = initializer.getFactory().createFlowNode(fake);

                    lane.getFlowNodeRef().add((JAXBElement) jFake);
                }
            }
        }
    }
}