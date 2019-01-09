package com.quorum.gauge;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.core.AbstractSpecImplementation;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.datastore.DataStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.quorum.methods.response.PermissionAccountList;
import org.web3j.quorum.methods.response.PermissionNodeList;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Service
@SuppressWarnings("unchecked")
public class NodeAccountPermission extends AbstractSpecImplementation {
    private static final Logger logger = LoggerFactory.getLogger(NodeAccountPermission.class);

    @Step("Ensure permission account list has <initialAccountCount> accounts in <node> when network started")
    public void checkPermissionAccountCount(int initialAccountCount, QuorumNode node) throws Exception {
        PermissionAccountList pacct = permissionService.getPermissionAccountList(node).toBlocking().first();
        int accountListSize = pacct.getPermissionAccountList().size();
        logger.debug("account list size:{}", accountListSize);

        assertThat(initialAccountCount).isEqualTo(accountListSize);
        int c = 0;
        for (PermissionAccountList.PermissionAccountInfo i : pacct.getPermissionAccountList()) {
            ++c;
            logger.debug("{} address: {} access: {}", c, i.getAddress(), i.getAccess());
        }

        DataStoreFactory.getSpecDataStore().put("permAcctList", pacct.getPermissionAccountList());
    }

    @Step("Ensure account <address> has permission <access>")
    public void checkAccountExists(String address, String access) throws Exception {
        List<PermissionAccountList.PermissionAccountInfo> pacctList = (ArrayList<PermissionAccountList.PermissionAccountInfo>) DataStoreFactory.getSpecDataStore().get("permAcctList");

        assertThat(pacctList.size()).isNotEqualTo(0);
        int c = 0;
        boolean isPresent = false;
        for (PermissionAccountList.PermissionAccountInfo i : pacctList) {
            ++c;
            logger.debug("{} address: {} access: {}", c, i.getAddress(), i.getAccess());
            if (i.getAddress().equals(address) && i.getAccess().equals(access)) {
                isPresent = true;
                break;
            }
        }
        assertThat(isPresent).isTrue();
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
            logger.debug("{} node: {} status: {}", c, i.getEnodeId(), i.getStatus());
        }

        DataStoreFactory.getSpecDataStore().put("permNodeList", nodeList);
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
