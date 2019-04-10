package com.quorum.gauge.services;

import com.quorum.gauge.common.QuorumNode;
import com.quorum.gauge.ext.NodeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.response.*;
import rx.Observable;

@Service
public class PermissionService extends AbstractService {

    @Autowired
    AccountService accountService;

    public String NodeInfo(QuorumNode node) {

        org.web3j.protocol.core.Request<?, NodeInfo> nodeInfoRequest = new org.web3j.protocol.core.Request<>(
                "admin_nodeInfo",
                null,
                connectionFactory().getWeb3jService(node),
                NodeInfo.class
        );
        NodeInfo nodeInfo = nodeInfoRequest.observable().toBlocking().first();
        return nodeInfo.getEnode();
    }

    public Observable<PermissionAccountList> getPermissionAccountList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumPermissionGetAccountList().observable();
    }

    public Observable<PermissionNodeList> getPermissionNodeList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumPermissionGetNodeList().observable();
    }

    public Observable<PermissionRoleList> getPermissionRoleList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumPermissionGetRoleList().observable();
    }

    public Observable<PermissionOrgList> getPermissionOrgList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumPermissionGetOrgList().observable();
    }



    public Observable<ExecStatusInfo> setAccountPermission(QuorumNode node, String fromAccount, String addAccount, int access) {
        Quorum client = connectionFactory().getConnection(node);
        return null;//client.quorumSetAccountPermission(addAccount, access, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }

    public Observable<ExecStatusInfo> addAccountToVoterList(QuorumNode node, String address) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return null;//client.quorumAddPermissionVoter(address, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();

    }

    public Observable<ExecStatusInfo> removeAccountFromVoterList(QuorumNode node, String address) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return null;//client.quorumRemovePermissionVoter(address, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();

    }

    public Observable<ExecStatusInfo> proposeNodeDeactivation(QuorumNode node, String nodeId) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return null;//client.quorumProposePermissionNodeDeactivation(nodeId, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }

    public Observable<ExecStatusInfo> approveNodeDeactivation(QuorumNode node, String nodeId) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return null;//client.quorumApprovePermissionNodeDeactivation(nodeId, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }
}
