package com.quorum.gauge;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.core.AbstractSpecImplementation;
import com.thoughtworks.gauge.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.quorum.methods.response.ExecStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Service
@SuppressWarnings("unchecked")
public class OrgKeyMgmt extends AbstractSpecImplementation {
    private static final Logger logger = LoggerFactory.getLogger(NodeAccountPermission.class);

    @Step("Add master org <morgId> from <node>")
    public void addMasterOrg(String morgId, QuorumNode node) {
        ExecStatus status = orgKeyMgmtService.addMasterOrg(node, morgId).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }
}
