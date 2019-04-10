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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Service
@SuppressWarnings("unchecked")
public class NodeAccountPermission extends AbstractSpecImplementation {
    private static final Logger logger = LoggerFactory.getLogger(NodeAccountPermission.class);

    @Autowired
    UtilService utilService;

    private static final Map<String, Integer> accountAccessMap = new HashMap<>();

    static {
        accountAccessMap.put("ReadOnly", 0);
        accountAccessMap.put("Transact", 1);
        accountAccessMap.put("ContractDeploy", 2);
        accountAccessMap.put("FullAccess", 3);
    }


    @Step("get org list from <node>")
    public void checkGetOrgList(QuorumNode node) {
        PermissionOrgList orgList = permissionService.getPermissionOrgList(node).toBlocking().first();
        int c = 0;
        for (PermissionOrgList.PermissionOrgInfo o : orgList.getPermissionOrgList()) {
            ++c;
            logger.info("{} org -> {}", c, o);
        }

        PermissionAccountList acctList = permissionService.getPermissionAccountList(node).toBlocking().first();
        List<PermissionAccountList.PermissionAccountInfo> pa = acctList.getPermissionAccountList();
        logger.info("acctlist {}", pa);
        PermissionNodeList nodeList = permissionService.getPermissionNodeList(node).toBlocking().first();
        List<PermissionNodeList.PermissionNodeInfo> pn = nodeList.getPermissionNodeList();
        logger.info("acctlist {}", pn);
        PermissionRoleList roleList = permissionService.getPermissionRoleList(node).toBlocking().first();
        List<PermissionRoleList.PermissionRoleInfo> pr = roleList.getPermissionRoleList();
        logger.info("acctlist {}", pr);
    }

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

    @Step("Check <node> has status <status>")
    public void checkNodeExists(QuorumNode node, String status) throws Exception {
        String enodeId = getEnodeId(node);
        checkPermissionNodeCount(-1, QuorumNode.Node1);
        List<PermissionNodeList.PermissionNodeInfo> permNodeList = (ArrayList<PermissionNodeList.PermissionNodeInfo>) DataStoreFactory.getSpecDataStore().get("permNodeList");
        assertThat(permNodeList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionNodeList.PermissionNodeInfo i : permNodeList) {
            ++c;
            logger.debug("{} node: {} status: {}", c, i.getUrl(), i.getStatus());
            if (i.getUrl().contains(enodeId) && i.getStatus() == Integer.parseInt(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
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
