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

    static {
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
        //logger.info("acctlist {}", pn);
        PermissionRoleList roleList = permissionService.getPermissionRoleList(node).toBlocking().first();
        List<PermissionRoleList.PermissionRoleInfo> pr = roleList.getPermissionRoleList();
        c = 0;
        for (PermissionRoleList.PermissionRoleInfo o : pr) {
            ++c;
            logger.info("{} role -> {}", c, o);
        }
        DataStoreFactory.getSpecDataStore().put("permRoleList", pr);
        //logger.info("acctlist {}", pr);
    }

    @Step("check org <org> is <status> with parent <porg>, level <level> and empty sub orgs")
    public void checkSubOrgExists(String org, String status, String porg, int level) throws Exception {
        List<PermissionOrgList.PermissionOrgInfo> permOrgList = (ArrayList<PermissionOrgList.PermissionOrgInfo>) DataStoreFactory.getSpecDataStore().get("permOrgList");
        assertThat(permOrgList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionOrgList.PermissionOrgInfo i : permOrgList) {
            ++c;
            logger.debug("{} org id: {} parent id: {} level: {}", c, i.getFullOrgId(), i.getParentOrgId(), i.getLevel());
            if (i.getFullOrgId().equals(org) && i.getParentOrgId().equals(porg)
                && i.getLevel() == level && (i.getSubOrgList() == null || i.getSubOrgList().size() == 0) &&
                i.getStatus() == orgStatusMap.get(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> is <status> with no parent, level <level> and sub orgs <slist>")
    public void checkOrgWithChildrenExists(String org, String status, int level, String slist) throws Exception {
        List<PermissionOrgList.PermissionOrgInfo> permOrgList = (ArrayList<PermissionOrgList.PermissionOrgInfo>) DataStoreFactory.getSpecDataStore().get("permOrgList");
        assertThat(permOrgList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionOrgList.PermissionOrgInfo i : permOrgList) {
            ++c;
            logger.debug("{} org id: {} parent id: {} level: {}", c, i.getFullOrgId(), i.getParentOrgId(), i.getLevel());
            if (i.getFullOrgId().equals(org) && (i.getParentOrgId() == null || i.getParentOrgId().equals(""))
                && i.getLevel() == level && (i.getSubOrgList().size() > 0 && i.getSubOrgList().containsAll(Arrays.asList(slist.split(",")))) &&
                i.getStatus() == orgStatusMap.get(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> is <status> with no parent, level <level> and empty sub orgs")
    public void checkOrgExists(String org, String status, int level) throws Exception {
        List<PermissionOrgList.PermissionOrgInfo> permOrgList = (ArrayList<PermissionOrgList.PermissionOrgInfo>) DataStoreFactory.getSpecDataStore().get("permOrgList");
        assertThat(permOrgList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionOrgList.PermissionOrgInfo i : permOrgList) {
            ++c;
            logger.debug("{} org id: {} parent id: {} level: {}", c, i.getFullOrgId(), i.getParentOrgId(), i.getLevel());
            if (i.getFullOrgId().equals(org) && (i.getParentOrgId() == null || i.getParentOrgId().equals(""))
                && i.getLevel() == level && (i.getSubOrgList() == null || i.getSubOrgList().size() == 0) &&
                i.getStatus() == orgStatusMap.get(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> has <node> with status <status>")
    public void checkNodeExists(String org, QuorumNode node, String status) throws Exception {
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

    //check org "NWADMIN" has role "NWADMIN" with access "FullAccess"
    @Step("check org <org> has role <role> with access <access> and permission to vote and is active")
    public void checkRoleExists(String org, String role, String access) throws Exception {
        List<PermissionRoleList.PermissionRoleInfo> permRoleList = (ArrayList<PermissionRoleList.PermissionRoleInfo>) DataStoreFactory.getSpecDataStore().get("permRoleList");
        assertThat(permRoleList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionRoleList.PermissionRoleInfo i : permRoleList) {
            ++c;
            if (i.getOrgId().equals(org) && i.getRoleId().equals(role) && i.getAccess() == accountAccessMap.get(access) && i.isActive() && i.isVoter()) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("check org <org> has role <role> with access <access> and no permission to vote and is active")
    public void checkRoleExists1(String org, String role, String access) throws Exception {
        List<PermissionRoleList.PermissionRoleInfo> permRoleList = (ArrayList<PermissionRoleList.PermissionRoleInfo>) DataStoreFactory.getSpecDataStore().get("permRoleList");
        assertThat(permRoleList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionRoleList.PermissionRoleInfo i : permRoleList) {
            ++c;
            if (i.getOrgId().equals(org) && i.getRoleId().equals(role) && i.getAccess() == accountAccessMap.get(access) && i.isActive() && !i.isVoter()) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }


    @Step("check org <org> has role <role> with access <access> and no permission to vote and is not active")
    public void checkRoleExists2(String org, String role, String access) throws Exception {
        List<PermissionRoleList.PermissionRoleInfo> permRoleList = (ArrayList<PermissionRoleList.PermissionRoleInfo>) DataStoreFactory.getSpecDataStore().get("permRoleList");
        assertThat(permRoleList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionRoleList.PermissionRoleInfo i : permRoleList) {
            ++c;
            if (i.getOrgId().equals(org) && i.getRoleId().equals(role) && i.getAccess() == accountAccessMap.get(access) && !i.isActive() && !i.isVoter()) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }


    @Step("check <node>'s default account is from org <org> and has role <role> and is org admin and is active")
    public void checkAccountExists(QuorumNode node, String org, String role) throws Exception {
        String defaultAcct = accountService.getDefaultAccountAddress(node).toBlocking().first();
        List<PermissionAccountList.PermissionAccountInfo> pacctList = (ArrayList<PermissionAccountList.PermissionAccountInfo>) DataStoreFactory.getSpecDataStore().get("permAcctList");
        assertThat(pacctList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionAccountList.PermissionAccountInfo i : pacctList) {
            ++c;
            logger.debug("{} address: {} role: {} org: {}", c, i.getAcctId(), i.getRoleId(), i.getOrgId());
            if (i.getAcctId().equalsIgnoreCase(defaultAcct) && i.getOrgId().equals(org) &&
                i.getRoleId().equals(role) && i.isOrgAdmin() && i.getStatus() == acctStatusMap.get("Active")) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("check <node>'s default account is from org <org> and has role <role> and is not org admin and is active")
    public void checkAccountExists1(QuorumNode node, String org, String role) throws Exception {
        String defaultAcct = accountService.getDefaultAccountAddress(node).toBlocking().first();
        List<PermissionAccountList.PermissionAccountInfo> pacctList = (ArrayList<PermissionAccountList.PermissionAccountInfo>) DataStoreFactory.getSpecDataStore().get("permAcctList");
        assertThat(pacctList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionAccountList.PermissionAccountInfo i : pacctList) {
            ++c;
            logger.debug("{} address: {} role: {} org: {}", c, i.getAcctId(), i.getRoleId(), i.getOrgId());
            if (i.getAcctId().equalsIgnoreCase(defaultAcct) && i.getOrgId().equals(org) &&
                i.getRoleId().equals(role) && !i.isOrgAdmin() && i.getStatus() == acctStatusMap.get("Active")) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }

    @Step("from <fromNode> add new org <org> with enode <enode> and <acctNode>'s default account")
    public void addOrg(QuorumNode fromNode, String org, String enode, QuorumNode acctNode) {
        String defAcct = accountService.getDefaultAccountAddress(acctNode).toBlocking().first();
        ExecStatus status = permissionService.addOrg(fromNode, org, enode, defAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }


    @Step("from <fromNode> assign org <org> role <role> to <acctNode>'s default account")
    public void assignAccountRole(QuorumNode fromNode, String org, String role, QuorumNode acctNode) {
        logger.info("org {} role {}", org, role);
        String defAcct = accountService.getDefaultAccountAddress(acctNode).toBlocking().first();
        logger.info("org {} role {} defacct {}", org, role, defAcct);
        ExecStatus status = permissionService.assignAccountRole(fromNode, defAcct, org, role).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }


    @Step("check org <org>'s status is <status>")
    public void checkOrgStatusExists(String org, String status) throws Exception {
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

    @Step("from <fromNode> update org <org>'s status to <status>")
    public void updateOrgStatus(QuorumNode fromNode, String org, String status) {
        ExecStatus estatus = permissionService.updateOrgStatus(fromNode, org, orgStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(estatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> approve org <org>'s status <status>")
    public void approveOrgStatus(QuorumNode fromNode, String org, String status) {
        ExecStatus estatus = permissionService.approveOrgStatus(fromNode, org, orgStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(estatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> under org <org> add enode <enode>")
    public void addNodeToOrg(QuorumNode fromNode, String org, String enode) {
        ExecStatus status = permissionService.addNodeToOrg(fromNode, org, enode).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> update status of org <org>'s enode <enode> with status <status>")
    public void updateNodeStatus(QuorumNode fromNode, String org, String enode, String status) {
        ExecStatus exstatus = permissionService.updateNode(fromNode, org, enode, nodeStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(exstatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> update status of org <org>'s node <enode> with status <status>")
    public void updateNodeStatus1(QuorumNode fromNode, String org, QuorumNode node, String status) {
        String enodeId = getEnodeId(node);
        String fullEnodeId = getFullEnode(enodeId, node);
        ExecStatus exstatus = permissionService.updateNode(fromNode, org, fullEnodeId, nodeStatusMap.get(status)).toBlocking().first().getExecStatus();
        assertThat(exstatus.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> add new role <role> under org <org> with access <access> and no voting")
    public void addNewRoleToOrg(QuorumNode fromNode, String role, String org, String access) {
        ExecStatus status = permissionService.addNewRole(fromNode, org, role, accountAccessMap.get(access), false).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> remove role <role> from org <org>")
    public void removeRoleToOrg(QuorumNode fromNode, String role, String org) {
        ExecStatus status = permissionService.removeRole(fromNode, org, role).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> add new sub org <sorg> under org <porg>")
    public void addSubOrg(QuorumNode fromNode, String sorg, String porg) {
        ExecStatus status = permissionService.addSubOrg(fromNode, porg, sorg, "", "0x0000000000000000000000000000000000000000").toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("from <fromNode> approve new org <org> with enode <enode> and <acctNode>'s default account")
    public void approveOrg(QuorumNode fromNode, String org, String enode, QuorumNode acctNode) {
        String defAcct = accountService.getDefaultAccountAddress(acctNode).toBlocking().first();
        ExecStatus status = permissionService.approveOrg(fromNode, org, enode, defAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
        waitForSomeSeconds(1);
        getNetworkDetails(fromNode);
    }

    @Step("check org <org> has enode <enodeid> with status <status>")
    public void checkEnodeExists(String org, String enode, String status) throws Exception {
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

    /*@Step("check org <org> has enode <enodeid> with status <status>")
    public void checkOrgHasEnodeExists(String org, String enode, String status) throws Exception {
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
    }*/


    //TODO remove old api's below after final review

    @Step("Ensure permission account list has <initialAccountCount> accounts in <node>")
    public void checkPermissionAccountCount(int initialAccountCount, QuorumNode node) {
        PermissionAccountList pacct = getPermissionAccountList(node);
        int accountListSize = pacct.getPermissionAccountList().size();
        logger.debug("account list size:{}", accountListSize);
        assertThat(initialAccountCount).isEqualTo(accountListSize);
        DataStoreFactory.getSpecDataStore().put("permAcctList", pacct.getPermissionAccountList());
    }

    public PermissionAccountList getPermissionAccountList(QuorumNode node) {
        PermissionAccountList pacct = permissionService.getPermissionAccountList(node).toBlocking().first();
        return pacct;
    }


    @Step("Check <node>'s default account has permission <access>")
    public void checkAccountExistsWithPerm(QuorumNode node, String access) throws Exception {
        String defaultAcct = accountService.getDefaultAccountAddress(node).toBlocking().first();
        List<PermissionAccountList.PermissionAccountInfo> pacctList = getPermissionAccountList(QuorumNode.Node1).getPermissionAccountList();
        assertThat(pacctList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionAccountList.PermissionAccountInfo i : pacctList) {
            ++c;
            /*logger.debug("{} address: {} access: {}", c, i.getAddress(), i.getAccess());
            if (i.getAddress().equalsIgnoreCase(defaultAcct) && i.getAccess().equalsIgnoreCase(access)) {
                isPresent = true;
                break;
            }*/
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


    @Step("Ensure permission node list has <initialNodeCount> nodes in <node> when network started")
    public void checkPermissionNodeCount(int initialNodeCount, QuorumNode node) {
        List<PermissionNodeList.PermissionNodeInfo> nodeList = permissionService.getPermissionNodeList(node).toBlocking().first().getPermissionNodeList();
        int nodeListSize = nodeList.size();
        logger.debug("node list size:{}", nodeListSize);

        if (initialNodeCount >= 0)
            assertThat(initialNodeCount).isEqualTo(nodeListSize);
        DataStoreFactory.getSpecDataStore().put("permNodeList", nodeList);
    }

    @Step("Set account <addAccount> with permission <access> from <node> as account <fromAccount>")
    public void setAccountPermission(String addAccount, String access, QuorumNode node, String fromAccount) {
        ExecStatus status = permissionService.setAccountPermission(node, fromAccount, addAccount, accountAccessMap.get(access)).toBlocking().first().getExecStatus();
        logger.debug("node list size:{}", status);
        assertThat(status.isStatus()).isTrue();
    }


    @Step("Set <node1>'s default account with permission <access> from <node2>'s default account")
    public void setAccountPermissionWithDefault(QuorumNode node1, String access, QuorumNode node2) {
        String targetDefaultAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        String frmDefaultAcct = accountService.getDefaultAccountAddress(node2).toBlocking().first();

        ExecStatus status = permissionService.setAccountPermission(node2, frmDefaultAcct, targetDefaultAcct, accountAccessMap.get(access)).toBlocking().first().getExecStatus();
        logger.debug("node list size:{}", status);
        assertThat(status.isStatus()).isTrue();
    }


    @Step("Set account <addAccount> with permission <access> from <node> as account <fromAccount> fails with error <error>")
    public void setAccountPermissionFailed(String addAccount, String access, QuorumNode node, String fromAccount, String error) {
        ExecStatus status = permissionService.setAccountPermission(node, fromAccount, addAccount, accountAccessMap.get(access)).toBlocking().first().getExecStatus();
        logger.debug("node list size:{}", status);
        assertThat(status.isStatus()).isFalse();
        assertThat(status.getMsg().contains(error)).isTrue();
    }

    @Step("Set <node1>'s default account with permission <access> from from <node2>'s default account fails with error <error>")
    public void setAccountPermissionFailedWithDefault(QuorumNode node1, String access, QuorumNode node2, String error) {
        String targetDefaultAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        String frmDefaultAcct = accountService.getDefaultAccountAddress(node2).toBlocking().first();
        ExecStatus status = permissionService.setAccountPermission(node2, frmDefaultAcct, targetDefaultAcct, accountAccessMap.get(access)).toBlocking().first().getExecStatus();
        logger.debug("node list size:{}", status);
        assertThat(status.isStatus()).isFalse();
        assertThat(status.getMsg().contains(error)).isTrue();
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


    @Step("Add <node1>'s default account as voter from <node>")
    public void addAccountToVoterList(QuorumNode node1, QuorumNode node) {
        String defAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        ExecStatus status = permissionService.addAccountToVoterList(node, defAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
    }

    @Step("Check <node1>'s default account is present in voter list from <node>")
    public void checkAccountInVoterList(QuorumNode node1, QuorumNode node) {
        String defAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        //List<String> voterList = permissionService.getPermissionNodeVoterList(node).toBlocking().first().getPermissionNodeList();
        boolean found = false;
        /*for (String s : voterList) {
            if (s.equalsIgnoreCase(defAcct)) {
                found = true;
                break;
            }
        }*/
        assertThat(found).isTrue();
    }

    @Step("Check <node1>'s default account is not present in voter list from <node>")
    public void checkAccountNotInVoterList(QuorumNode node1, QuorumNode node) {
        String defAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        //List<String> voterList = permissionService.getPermissionNodeVoterList(node).toBlocking().first().getPermissionNodeList();
        boolean found = false;
        /*if (voterList != null) {
            for (String s : voterList) {
                if (s.equalsIgnoreCase(defAcct)) {
                    found = true;
                    break;
                }
            }
        }*/
        assertThat(found).isFalse();
    }

    @Step("Remove <node1>'s default account as voter from <node>")
    public void removeAccountFromVoter(QuorumNode node1, QuorumNode node) {
        String defAcct = accountService.getDefaultAccountAddress(node1).toBlocking().first();
        ExecStatus status = permissionService.removeAccountFromVoterList(node, defAcct).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();

    }

    public String getEnodeId(QuorumNode node) {
        String enode = permissionService.NodeInfo(node);
        String enodeId = enode.substring(0, enode.indexOf("@")).substring("enode://".length());
        return enodeId;
    }

    @Step("Propose node deactivation for <enode> from <node>")
    public void proposeNodeDeactivation(QuorumNode node1, QuorumNode node) {
        String enodeId = getEnodeId(node1);
        String fullEnodeId = getFullEnode(enodeId, node);
        ExecStatus status = permissionService.proposeNodeDeactivation(node, fullEnodeId).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();

    }

    @Step("Approve node deactivation for <node1> from <node>")
    public void approveNodeDeactivation(QuorumNode node1, QuorumNode node) {
        String enodeId = getEnodeId(node1);
        String fullEnodeId = getFullEnode(enodeId, node);
        ExecStatus status = permissionService.approveNodeDeactivation(node, fullEnodeId).toBlocking().first().getExecStatus();
        assertThat(status.isStatus()).isTrue();
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
