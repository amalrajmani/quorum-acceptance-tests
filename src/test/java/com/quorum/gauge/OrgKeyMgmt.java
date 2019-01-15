package com.quorum.gauge;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.core.AbstractSpecImplementation;
import com.thoughtworks.gauge.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.quorum.methods.response.ExecStatus;
import org.web3j.quorum.methods.response.OrgVoterList;

import java.util.List;

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
    @Step("Add sub org <sorgId> under master org <morgId> from <node>")
    public void addSubOrg(String sorgId, String morgId, QuorumNode node) {
        ExecStatus status = orgKeyMgmtService.addSubOrg(node, sorgId, morgId).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }
    @Step("Add account <voterAcct> as voter for master org <morgId> from <node>")
    public void addOrgVoter(String voterAcct, String morgId, QuorumNode node) {
        ExecStatus status = orgKeyMgmtService.addOrgVoter(node, morgId, voterAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }
    @Step("Validate account <voterAcct> is present as voter for master org <morgId> from <node>")
    public void valOrgVoter(String voterAcct, String morgId, QuorumNode node) {
        List<String> voterList = orgKeyMgmtService.getOrgVoterList(node, morgId).toBlocking().first().getOrgVoterList();
        boolean recExists = false;
        logger.info("Voter list size is {}", voterList.size());
        for (String ac: voterList) {
            logger.info("ac {}", ac);
            if (ac.equalsIgnoreCase(voterAcct)){
                recExists = true;
                break;
            }
        }
        assertThat(recExists).isTrue();
    }
}
