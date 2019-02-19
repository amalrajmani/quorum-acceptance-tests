package com.quorum.gauge;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.core.AbstractSpecImplementation;
import com.thoughtworks.gauge.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.quorum.methods.response.ExecStatus;
import org.web3j.quorum.methods.response.OrgKeyInfoList;

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

    @Step("Add <node1>'s default account as voter for master org <morgId> from <node>")
    public void addOrgVoterWithDefault(QuorumNode node1, String morgId, QuorumNode node) {
        String voterAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        ExecStatus status = orgKeyMgmtService.addOrgVoter(node, morgId, voterAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }

    @Step("Validate <node1>'s default account is present as voter for master org <morgId> from <node>")
    public void valOrgVoter(QuorumNode node1, String morgId, QuorumNode node) {
        String voterAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        List<String> voterList = orgKeyMgmtService.getOrgVoterList(node, morgId).toBlocking().first().getOrgVoterList();
        boolean recExists = false;
        logger.debug("Voter list size is {}", voterList.size());
        for (String ac : voterList) {
            logger.debug("ac {}", ac);
            if (ac.equalsIgnoreCase(voterAcct)) {
                recExists = true;
                break;
            }
        }
        assertThat(recExists).isTrue();
    }

    @Step("Validate master org <morgId> and sub org <sorgId> are present as a part of the org list at smart contract from <node>")
    public void valMorgSorg(String morgId, String sorgId, QuorumNode node) {
        List<OrgKeyInfoList.OrgKeyInfo> orgList = orgKeyMgmtService.getOrgKeyInfoList(node).toBlocking().first().getOrgKeyInfoList();
        boolean recExists = false;
        logger.debug("Org list size is {}", orgList.size());
        for (OrgKeyInfoList.OrgKeyInfo org : orgList) {
            logger.debug("orgId {}, sorgId {}", org.getMasterOrgId(), org.getSubOrgId());
            if (org.getMasterOrgId().equalsIgnoreCase(morgId) && org.getSubOrgId().equalsIgnoreCase(sorgId)) {
                recExists = true;
                break;
            }
        }
        assertThat(recExists).isTrue();
    }

    @Step("Add key <tmKey> as a key to sub org <sorgId> from <node>")
    public void addOrgKey(String tmKey, String sorgId, QuorumNode node) {
        ExecStatus status = orgKeyMgmtService.addOrgKey(node, sorgId, tmKey).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }

    @Step("Approve pending approval process for sub org <sorgId> from <node>")
    public void approvePendingOp(String sorgId, QuorumNode node) {
        ExecStatus status = orgKeyMgmtService.approvePendingOp(node, sorgId).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }

    @Step("Validate key <tmKey> is present as key for sub org <sorgId> from <node> ")
    public void valKeyPresence(String tmKey, String sorgId, QuorumNode node) {
        List<OrgKeyInfoList.OrgKeyInfo> orgList = orgKeyMgmtService.getOrgKeyInfoList(node).toBlocking().first().getOrgKeyInfoList();
        boolean keyFound = false;
//        logger.info("Org list size is {}", orgList.size());

        for (OrgKeyInfoList.OrgKeyInfo org : orgList) {
//            logger.info("orgId {}, sorgId {}", org.getMasterOrgId(), org.getSubOrgId());
            if (org.getSubOrgId().equalsIgnoreCase(sorgId)) {
                List<String> keys = org.getSubOrgKeyList();

                for (String key : keys) {
                    if (key.equalsIgnoreCase(tmKey)) {
                        keyFound = true;
                        break;
                    }
                }
                break;
            }
        }
        assertThat(keyFound).isTrue();
    }

    @Step("Delete key <tmKey> from sub org <sorgId> from <node>")
    public void removeOrgKey(String tmKey, String sorgId, QuorumNode node) {
        ExecStatus status = orgKeyMgmtService.removeOrgKey(node, sorgId, tmKey).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }

    @Step("Delete voter <node1>'s default account from master org <morgId> from <node>")
    public void removeOrgVoter(QuorumNode node1, String morgId, QuorumNode node) {
        String voterAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        ExecStatus status = orgKeyMgmtService.removeOrgVoter(node, morgId, voterAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }

}
