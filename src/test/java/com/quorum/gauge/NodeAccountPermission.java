package com.quorum.gauge;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.core.AbstractSpecImplementation;
import com.quorum.gauge.services.UtilService;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.quorum.methods.response.*;
import org.web3j.tx.Contract;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Service
@SuppressWarnings("unchecked")
public class NodeAccountPermission extends AbstractSpecImplementation {
    private static final Logger logger = LoggerFactory.getLogger(NodeAccountPermission.class);

    @Autowired
    UtilService utilService;

    private static final Map<String, Integer> accountAccessMap = new HashMap<>();
    private static final Map<String, Integer> nodeStatusMap = new HashMap<>();
    private static final Map<String, Integer> orgStatusMap = new HashMap<>();
    private static final Map<String, Integer> acctStatusMap = new HashMap<>();
    private static final Map<String, String> configMap = new HashMap<>();

    static {
        configMap.put("NWADMIN-org", "NWADMIN");
        configMap.put("NWADMIN-role", "NWADMIN");
        configMap.put("OADMIN-role", "OADMIN");

        accountAccessMap.put("ReadOnly", 0);
        accountAccessMap.put("Transact", 1);
        accountAccessMap.put("ContractDeploy", 2);
        accountAccessMap.put("FullAccess", 3);

        nodeStatusMap.put("Pending", 1);
        nodeStatusMap.put("Approved", 2);
        nodeStatusMap.put("Deactivated", 3);
        nodeStatusMap.put("Activated", 4);
        nodeStatusMap.put("Blacklisted", 5);

        acctStatusMap.put("Pending", 1);
        acctStatusMap.put("Active", 2);
        acctStatusMap.put("InActive", 3);
//1- Proposed, 2- Approved, 3- PendingSuspension, 4- Suspended, 5- RevokeSuspension
        orgStatusMap.put("Proposed", 1);
        orgStatusMap.put("Approved", 2);
        orgStatusMap.put("PendingSuspension", 3);
        orgStatusMap.put("Suspended", 4);
        orgStatusMap.put("RevokeSuspension", 5);
    }

    public NodeAccountPermission() {
    }

    @Step("network admin org is <org>")
    public void SetupNetworkAdminOrg(String org) {
        DataStoreFactory.getSpecDataStore().put("NWADMIN-org", org);
    }

    @Step("network admin role is <role>")
    public void SetupNetworkAdminRole(String role) {
        DataStoreFactory.getSpecDataStore().put("NWADMIN-role", role);
    }

    @Step("org admin role is <org>")
    public void SetupOrgAdminRole(String role) {
        DataStoreFactory.getSpecDataStore().put("OADMIN-role", role);
    }

    private String getNetworkAdminOrg(String org) {
        String key = org + "-org";
        if (configMap.get(key) != null) {
            logger.info("AJ-key " + key + " has value");
            return configMap.get(key);
        }
        return org;
    }

    private String getNetworkOrgAdminRole(String role) {
        String key = role + "-role";
        if (configMap.get(key) != null) {
            return configMap.get(key);
        }
        return role;
    }


    @Step("get netowrk details from <node>")
    public void getNetworkDetails(QuorumNode node) {
        PermissionOrgList orgList = permissionService.getPermissionOrgList(node).toBlocking().first();
        int c = 0;
        for (PermissionOrgList.PermissionOrgInfo o : orgList.getPermissionOrgList()) {
            ++c;
            logger.info("{} org -> {}", c, o);
        }

        DataStoreFactory.getSpecDataStore().put("permOrgList", orgList.getPermissionOrgList());

        PermissionAccountList acctList = permissionService.getPermissionAccountList(node).toBlocking().first();
        List<PermissionAccountList.PermissionAccountInfo> pa = acctList.getPermissionAccountList();
        c = 0;
        for (PermissionAccountList.PermissionAccountInfo o : pa) {
            ++c;
            logger.info("{} acct -> {}", c, o);
        }

        DataStoreFactory.getSpecDataStore().put("permAcctList", pa);


        PermissionNodeList nodeList = permissionService.getPermissionNodeList(node).toBlocking().first();
        List<PermissionNodeList.PermissionNodeInfo> pn = nodeList.getPermissionNodeList();
        c = 0;
        for (PermissionNodeList.PermissionNodeInfo o : pn) {
            ++c;
            logger.info("{} node -> {}", c, o);
        }
        DataStoreFactory.getSpecDataStore().put("permNodeList", pn);

        PermissionRoleList roleList = permissionService.getPermissionRoleList(node).toBlocking().first();
        List<PermissionRoleList.PermissionRoleInfo> pr = roleList.getPermissionRoleList();
        c = 0;
        for (PermissionRoleList.PermissionRoleInfo o : pr) {
            ++c;
            logger.info("{} role -> {}", c, o);
        }
        DataStoreFactory.getSpecDataStore().put("permRoleList", pr);
    }


    @Step("check org <org> is <status> with no parent, level <level> and sub orgs <slist>")
    public void checkOrgExists1(String org, String status, int level, String slist) throws Exception {
        boolean isPresent = orgExists("TYPE1", org, "", status, level, slist);
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> is <status> with parent <porg>, level <level> and empty sub orgs")
    public void checkOrgExists2(String org, String status, String porg, int level) throws Exception {
        boolean isPresent = orgExists("TYPE2", org, porg, status, level, "");
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> is <status> with no parent, level <level> and empty sub orgs")
    public void checkOrgExists3(String org, String status, int level) throws Exception {
        boolean isPresent = orgExists("TYPE3", org, "", status, level, "");
        assertThat(isPresent).isTrue();
    }

    private boolean orgExists(String checkType, String org, String porg, String status, int level, String slist) {
        org = getNetworkAdminOrg(org);
        porg = getNetworkAdminOrg(porg);
        List<PermissionOrgList.PermissionOrgInfo> permOrgList = (ArrayList<PermissionOrgList.PermissionOrgInfo>) DataStoreFactory.getSpecDataStore().get("permOrgList");
        assertThat(permOrgList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        l1:
        for (PermissionOrgList.PermissionOrgInfo i : permOrgList) {
            ++c;
            logger.debug("{} org id: {} parent id: {} level: {}", c, i.getFullOrgId(), i.getParentOrgId(), i.getLevel());
            switch (checkType) {
                case "TYPE1":
                    if (i.getFullOrgId().equals(org) && (i.getParentOrgId() == null || i.getParentOrgId().equals(""))
                        && i.getLevel() == level && (i.getSubOrgList().size() > 0 && i.getSubOrgList().containsAll(Arrays.asList(slist.split(",")))) &&
                        i.getStatus() == orgStatusMap.get(status)) {
                        isPresent = true;
                        break l1;
                    }
                    break;
                case "TYPE2":
                    if (i.getFullOrgId().equals(org) && i.getParentOrgId().equals(porg)
                        && i.getLevel() == level && (i.getSubOrgList() == null || i.getSubOrgList().size() == 0) &&
                        i.getStatus() == orgStatusMap.get(status)) {
                        isPresent = true;
                        break l1;
                    }
                    break;
                case "TYPE3":
                    if (i.getFullOrgId().equals(org) && (i.getParentOrgId() == null || i.getParentOrgId().equals(""))
                        && i.getLevel() == level && (i.getSubOrgList() == null || i.getSubOrgList().size() == 0) &&
                        i.getStatus() == orgStatusMap.get(status)) {
                        isPresent = true;
                        break l1;
                    }
                    break;
            }
        }
        return isPresent;
    }

    @Step("check org <org> has <node> with status <status>")
    public void checkNodeExists(String org, QuorumNode node, String status) throws Exception {
        org = getNetworkAdminOrg(org);
        String enodeId = getEnodeId(node);
        List<PermissionNodeList.PermissionNodeInfo> permNodeList = (ArrayList<PermissionNodeList.PermissionNodeInfo>) DataStoreFactory.getSpecDataStore().get("permNodeList");
        assertThat(permNodeList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionNodeList.PermissionNodeInfo i : permNodeList) {
            ++c;
            logger.debug("{} node: {} status: {}", c, i.getUrl(), i.getStatus());
            if (i.getOrgId().equals(org) && i.getUrl().contains(enodeId) && i.getStatus() == nodeStatusMap.get(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> has role <role> with access <access> and permission to vote and is active")
    public void checkRoleExists1(String org, String role, String access) throws Exception {
        boolean isPresent = roleExists("TYPE1", org, role, access);
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> has role <role> with access <access> and no permission to vote and is active")
    public void checkRoleExists2(String org, String role, String access) throws Exception {
        boolean isPresent = roleExists("TYPE2", org, role, access);
        assertThat(isPresent).isTrue();
    }


    @Step("check org <org> has role <role> with access <access> and no permission to vote and is not active")
    public void checkRoleExists3(String org, String role, String access) throws Exception {
        boolean isPresent = roleExists("TYPE3", org, role, access);
        assertThat(isPresent).isTrue();
    }

    private boolean roleExists(String checkType, String org, String role, String access) {
        org = getNetworkAdminOrg(org);
        role = getNetworkOrgAdminRole(role);
        List<PermissionRoleList.PermissionRoleInfo> permRoleList = (ArrayList<PermissionRoleList.PermissionRoleInfo>) DataStoreFactory.getSpecDataStore().get("permRoleList");
        assertThat(permRoleList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        l1:
        for (PermissionRoleList.PermissionRoleInfo i : permRoleList) {
            ++c;
            switch (checkType) {
                case "TYPE1":
                    if (i.getOrgId().equals(org) && i.getRoleId().equals(role) && i.getAccess() == accountAccessMap.get(access) && i.isActive() && i.isVoter()) {
                        isPresent = true;
                        break l1;
                    }
                    break;
                case "TYPE2":
                    if (i.getOrgId().equals(org) && i.getRoleId().equals(role) && i.getAccess() == accountAccessMap.get(access) && i.isActive() && !i.isVoter()) {
                        isPresent = true;
                        break l1;
                    }
                    break;
                case "TYPE3":
                    if (i.getOrgId().equals(org) && i.getRoleId().equals(role) && i.getAccess() == accountAccessMap.get(access) && !i.isActive() && !i.isVoter()) {
                        isPresent = true;
                        break l1;
                    }
                    break;
            }

        }
        return isPresent;
    }


    @Step("check <node>'s default account is from org <org> and has role <role> and is org admin and is active")
    public void checkAccountExists1(QuorumNode node, String org, String role) throws Exception {
        boolean isPresent = accountExists(node, org, role, true);
        assertThat(isPresent).isTrue();
    }

    @Step("check <node>'s default account is from org <org> and has role <role> and is not org admin and is active")
    public void checkAccountExists2(QuorumNode node, String org, String role) throws Exception {
        boolean isPresent = accountExists(node, org, role, false);
        assertThat(isPresent).isTrue();
    }

    private boolean accountExists(QuorumNode node, String org, String role, boolean orgAdmin) {
        org = getNetworkAdminOrg(org);
        role = getNetworkOrgAdminRole(role);
        String defaultAcct = accountService.getDefaultAccountAddress(node).toBlocking().first();
        List<PermissionAccountList.PermissionAccountInfo> pacctList = (ArrayList<PermissionAccountList.PermissionAccountInfo>) DataStoreFactory.getSpecDataStore().get("permAcctList");
        assertThat(pacctList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionAccountList.PermissionAccountInfo i : pacctList) {
            ++c;
            logger.debug("{} address: {} role: {} org: {}", c, i.getAcctId(), i.getRoleId(), i.getOrgId());
            if (orgAdmin) {
                if (i.getAcctId().equalsIgnoreCase(defaultAcct) && i.getOrgId().equals(org) &&
                    i.getRoleId().equals(role) && i.isOrgAdmin() && i.getStatus() == acctStatusMap.get("Active")) {
                    isPresent = true;
                    break;
                }
            } else {
                if (i.getAcctId().equalsIgnoreCase(defaultAcct) && i.getOrgId().equals(org) &&
                    i.getRoleId().equals(role) && !i.isOrgAdmin() && i.getStatus() == acctStatusMap.get("Active")) {
                    isPresent = true;
                    break;
                }
            }
        }
        return isPresent;
    }

    @Step("from <fromNode> add new org <org> with enode <enode> and <acctNode>'s default account")
    public void addOrg(QuorumNode fromNode, String org, String enode, QuorumNode acctNode) {
        org = getNetworkAdminOrg(org);
        String defAcct = accountService.getDefaultAccountAddress(acctNode).toBlocking().first();
        ExecStatus status = permissionService.addOrg(fromNode, org, enode, defAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }


    @Step("from <fromNode> approve new org <org> with enode <enode> and <acctNode>'s default account")
    public void approveOrg(QuorumNode fromNode, String org, String enode, QuorumNode acctNode) {
        org = getNetworkAdminOrg(org);
        String defAcct = accountService.getDefaultAccountAddress(acctNode).toBlocking().first();
        ExecStatus status = permissionService.approveOrg(fromNode, org, enode, defAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }


    @Step("from <fromNode> update org <org>'s status to <status>")
    public void updateOrgStatus(QuorumNode fromNode, String org, String status) {
        org = getNetworkAdminOrg(org);
        ExecStatus estatus = permissionService.updateOrgStatus(fromNode, org, orgStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(estatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> approve org <org>'s status <status>")
    public void approveOrgStatus(QuorumNode fromNode, String org, String status) {
        org = getNetworkAdminOrg(org);
        ExecStatus estatus = permissionService.approveOrgStatus(fromNode, org, orgStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(estatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> add new sub org <sorg> under org <porg>")
    public void addSubOrg(QuorumNode fromNode, String sorg, String porg) {
        porg = getNetworkAdminOrg(porg);
        ExecStatus status = permissionService.addSubOrg(fromNode, porg, sorg, "", "0x0000000000000000000000000000000000000000").toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("check org <org>'s status is <status>")
    public void checkOrgStatusExists(String org, String status) throws Exception {
        org = getNetworkAdminOrg(org);
        List<PermissionOrgList.PermissionOrgInfo> orgList = (ArrayList<PermissionOrgList.PermissionOrgInfo>) DataStoreFactory.getSpecDataStore().get("permOrgList");
        assertThat(orgList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionOrgList.PermissionOrgInfo i : orgList) {
            ++c;
            logger.debug("{} org: {} status: {}", c, i.getFullOrgId(), i.getStatus());
            if (i.getFullOrgId().equalsIgnoreCase(org) && i.getStatus() == orgStatusMap.get(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("from <fromNode> assign org <org> role <role> to <acctNode>'s default account")
    public void assignAccountRole(QuorumNode fromNode, String org, String role, QuorumNode acctNode) {
        org = getNetworkAdminOrg(org);
        role = getNetworkOrgAdminRole(role);
        logger.info("org {} role {}", org, role);
        String defAcct = accountService.getDefaultAccountAddress(acctNode).toBlocking().first();
        logger.info("org {} role {} defacct {}", org, role, defAcct);
        ExecStatus status = permissionService.assignAccountRole(fromNode, defAcct, org, role).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> under org <org> add enode <enode>")
    public void addNodeToOrg(QuorumNode fromNode, String org, String enode) {
        org = getNetworkAdminOrg(org);
        ExecStatus status = permissionService.addNodeToOrg(fromNode, org, enode).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> update status of org <org>'s enode <enode> with status <status>")
    public void updateNodeStatus1(QuorumNode fromNode, String org, String enode, String status) {
        updateOrgNodeStatus(fromNode, org, status, enode);
    }

    @Step("from <fromNode> update status of org <org>'s node <node> with status <status>")
    public void updateNodeStatus2(QuorumNode fromNode, String org, QuorumNode node, String status) {
        String enodeId = getEnodeId(node);
        String fullEnodeId = getFullEnode(enodeId, node);
        updateOrgNodeStatus(fromNode, org, status, fullEnodeId);
    }

    private void updateOrgNodeStatus(QuorumNode fromNode, String org, String status, String fullEnodeId) {
        org = getNetworkAdminOrg(org);
        ExecStatus exstatus = permissionService.updateNode(fromNode, org, fullEnodeId, nodeStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(exstatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> add new role <role> under org <org> with access <access> and no voting")
    public void addNewRoleToOrg(QuorumNode fromNode, String role, String org, String access) {
        org = getNetworkAdminOrg(org);
        role = getNetworkOrgAdminRole(role);
        ExecStatus status = permissionService.addNewRole(fromNode, org, role, accountAccessMap.get(access), false).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> remove role <role> from org <org>")
    public void removeRoleFromOrg(QuorumNode fromNode, String role, String org) {
        org = getNetworkAdminOrg(org);
        role = getNetworkOrgAdminRole(role);
        ExecStatus status = permissionService.removeRole(fromNode, org, role).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("check org <org> has enode <enodeid> with status <status>")
    public void checkEnodeExists(String org, String enode, String status) throws Exception {
        org = getNetworkAdminOrg(org);
        List<PermissionNodeList.PermissionNodeInfo> nodeList = (ArrayList<PermissionNodeList.PermissionNodeInfo>) DataStoreFactory.getSpecDataStore().get("permNodeList");
        assertThat(nodeList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionNodeList.PermissionNodeInfo i : nodeList) {
            ++c;
            logger.debug("{} org: {} enode: {} status: {}", c, i.getOrgId(), i.getUrl(), i.getStatus());
            if (i.getOrgId().equalsIgnoreCase(org) && i.getUrl().equals(enode) &&
                i.getStatus() == nodeStatusMap.get(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }


    @Step("Deploy <contractName> smart contract with initial value <initialValue> from a default account in <node> fails with error <error>")
    public void setupStorecAsPublicDependentContract(String contractName, int initialValue, QuorumNode node, String error) {
        Contract c = null;
        String exMsg = "";
        try {
            c = contractService.createGenericStoreContract(node, contractName, initialValue, null, false, null).toBlocking().first();

        } catch (Exception ex) {
            exMsg = ex.getMessage();
            logger.debug("deploy contract failed " + ex.getMessage());
        }
        Assertions.assertThat(c).isNull();
        Assertions.assertThat(exMsg.contains(error)).isTrue();
    }

    @Step("<contractNameKey>'s <methodName> function execution in <node> with value <value> fails with error <error>")
    public void setStoreContractValueFails(String contractNameKey, String methodName, QuorumNode node, int value, String error) {
        String exMsg = "";
        Contract c = mustHaveValue(DataStoreFactory.getSpecDataStore(), contractNameKey, Contract.class);
        String contractName = mustHaveValue(DataStoreFactory.getSpecDataStore(), contractNameKey + "Type", String.class);
        logger.debug("{} contract address is:{}, {} {}", contractNameKey, c.getContractAddress(), methodName, value);
        try {
            TransactionReceipt tr = contractService.setGenericStoreContractSetValue(node, c.getContractAddress(), contractName, methodName, value, false, null).toBlocking().first();
            logger.debug("{} {} {}, txHash = {}", contractNameKey, contractName, methodName, tr.getTransactionHash());
        } catch (Exception ex) {
            exMsg = ex.getMessage();
            logger.debug("setc for contract failed " + ex.getMessage());
        }
        Assertions.assertThat(exMsg.contains(error)).isTrue();
    }



    public String getFullEnode(String enode, QuorumNode n1) {
        List<PermissionNodeList.PermissionNodeInfo> permNodeList = permissionService.getPermissionNodeList(n1).toBlocking().first().getPermissionNodeList();
        assertThat(permNodeList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionNodeList.PermissionNodeInfo i : permNodeList) {
            ++c;
            if (i.getUrl().contains(enode)) {
                return i.getUrl();
            }
        }
        return null;
    }


    public String getEnodeId(QuorumNode node) {
        String enode = permissionService.NodeInfo(node);
        String enodeId = enode.substring(0, enode.indexOf("@")).substring("enode://".length());
        return enodeId;
    }


    @Step("Save current blocknumber from <node>")
    public void saveCurrentBlockNumber(QuorumNode node) {
        EthBlockNumber blkNumber = utilService.getCurrentBlockNumberFrom(node).toBlocking().first();
        DataStoreFactory.getSpecDataStore().put(node.name() + "blockNumber", blkNumber);
        logger.debug("current block number from {} is {}", node.name(), blkNumber.getBlockNumber().intValue());
        assertThat(blkNumber.getBlockNumber().intValue()).isNotEqualTo(0);
    }

    @Step("Ensure current blocknumber from <node> has not changed")
    public void checkBlockNumberHasNotChanged(QuorumNode node) {
        EthBlockNumber oldBlkNumber = (EthBlockNumber) DataStoreFactory.getSpecDataStore().get(node.name() + "blockNumber");
        EthBlockNumber newBlkNumber = utilService.getCurrentBlockNumberFrom(node).toBlocking().first();
        logger.debug("block number old:{} new:{}", oldBlkNumber.getBlockNumber().intValue(), newBlkNumber.getBlockNumber().intValue());
        assertThat(newBlkNumber.getBlockNumber().intValue()).isEqualTo(oldBlkNumber.getBlockNumber().intValue());
    }

    @Step("Wait for <seconds> Seconds")
    public void waitForSomeSeconds(int seconds) {

        try {
            logger.debug("wating for {} seconds", seconds);
            Thread.sleep(seconds * 1000);
            logger.debug("wait is over");
        } catch (InterruptedException e) {

        }
    }

}
