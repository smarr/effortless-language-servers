// CheckStyle: start generated
package com.oracle.truffle.sl.nodes;

import com.oracle.truffle.api.dsl.GeneratedBy;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.InstrumentableNode.WrapperNode;
import com.oracle.truffle.api.nodes.NodeCost;

@GeneratedBy(SLStatementNode.class)
final class SLStatementNodeWrapper extends SLStatementNode implements WrapperNode {

    @Child private SLStatementNode delegateNode;
    @Child private ProbeNode probeNode;

    SLStatementNodeWrapper(SLStatementNode delegateNode, ProbeNode probeNode) {
        this.delegateNode = delegateNode;
        this.probeNode = probeNode;
    }

    @Override
    public SLStatementNode getDelegateNode() {
        return delegateNode;
    }

    @Override
    public ProbeNode getProbeNode() {
        return probeNode;
    }

    @Override
    public NodeCost getCost() {
        return NodeCost.NONE;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        for (;;) {
            boolean wasOnReturnExecuted = false;
            try {
                probeNode.onEnter(frame);
                delegateNode.executeVoid(frame);
                wasOnReturnExecuted = true;
                probeNode.onReturnValue(frame, null);
                break;
            } catch (Throwable t) {
                Object result = probeNode.onReturnExceptionalOrUnwind(frame, t, wasOnReturnExecuted);
                if (result == ProbeNode.UNWIND_ACTION_REENTER) {
                    continue;
                } else if (result != null) {
                    break;
                }
                throw t;
            }
        }
    }

}
