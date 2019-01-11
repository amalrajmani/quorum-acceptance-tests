package com.quorum.gauge;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.core.AbstractSpecImplementation;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.quorum.methods.response.ExecStatus;
import org.web3j.quorum.methods.response.PermissionAccountList;
import org.web3j.quorum.methods.response.PermissionNodeList;
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

    private static final Map<String, String> accountAccessMap = new HashMap<>();

    static {
        accountAccessMap.put("FullAccess", "0");
        accountAccessMap.put("ReadOnly", "1");
        accountAccessMap.put("Transact", "2");
        accountAccessMap.put("ContractDeploy", "3");

    }

    @Step("Ensure permission account list has <initialAccountCount> accounts in <node>")
    public void checkPermissionAccountCount(int initialAccountCount, QuorumNode node) {
        PermissionAccountList pacct = getPermissionAccountList(node);
        int accountListSize = pacct.getPermissionAccountList().size();
        logger.debug("account list size:{}", accountListSize);

        assertThat(initialAccountCount).isEqualTo(accountListSize);
        int c = 0;
        for (PermissionAccountList.PermissionAccountInfo i : pacct.getPermissionAccountList()) {
            ++c;
            logger.info("{} address: {} access: {}", c, i.getAddress(), i.getAccess());
        }

        DataStoreFactory.getSpecDataStore().put("permAcctList", pacct.getPermissionAccountList());
    }

    public PermissionAccountList getPermissionAccountList(QuorumNode node) {
        PermissionAccountList pacct = permissionService.getPermissionAccountList(node).toBlocking().first();
        return pacct;
    }

    @Step("Ensure account <address> has permission <access>")
    public void checkAccountExists(String address, String access) throws Exception {
        List<PermissionAccountList.PermissionAccountInfo> pacctList = getPermissionAccountList(QuorumNode.Node1).getPermissionAccountList();
        assertThat(pacctList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionAccountList.PermissionAccountInfo i : pacctList) {
            ++c;
            logger.debug("{} address: {} access: {}", c, i.getAddress(), i.getAccess());
            if (i.getAddress().equalsIgnoreCase(address) && i.getAccess().equalsIgnoreCase(access)) {
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
            logger.info("deploy contract failed " + ex.getMessage());
            logger.error("deploy failed", ex);
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

        assertThat(initialNodeCount).isEqualTo(nodeListSize);
        int c = 0;
        for (PermissionNodeList.PermissionNodeInfo i : nodeList) {
            ++c;
            logger.info("{} node: {} status: {}", c, i.getEnodeId(), i.getStatus());
        }
        DataStoreFactory.getSpecDataStore().put("permNodeList", nodeList);
    }

    @Step("Set account <addAccount> with permission <access> from <node> as account <fromAccount>")
    public void setAccountPermission(String addAccount, String access, QuorumNode node, String fromAccount) {
        ExecStatus status = permissionService.setAccountPermission(node, fromAccount, addAccount, accountAccessMap.get(access)).toBlocking().first().getExecStatus();
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

    @Step("Ensure node <node> has status <status>")
    public void checkNodeExists(String node, String status) throws Exception {
        List<PermissionNodeList.PermissionNodeInfo> permNodeList = (ArrayList<PermissionNodeList.PermissionNodeInfo>) DataStoreFactory.getSpecDataStore().get("permNodeList");

        assertThat(permNodeList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionNodeList.PermissionNodeInfo i : permNodeList) {
            ++c;
            logger.debug("{} node: {} status: {}", c, i.getEnodeId(), i.getStatus());
            if (i.getEnodeId().contains(node) && i.getStatus().equals(status)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
    }


}
