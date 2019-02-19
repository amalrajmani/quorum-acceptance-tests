package com.quorum.gauge.services;

import com.quorum.gauge.common.QuorumNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.quorum.methods.response.ExecStatusInfo;
import org.web3j.quorum.methods.response.PermissionAccountList;
import org.web3j.quorum.methods.response.PermissionNodeList;
import org.web3j.quorum.methods.response.PermissionVoterList;
import rx.Observable;

import java.math.BigInteger;

@Service
public class PermissionService extends AbstractService {

    @Autowired
    AccountService accountService;

    public Observable<PermissionAccountList> getPermissionAccountList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumGetPermissionAccountList().observable();
    }

    public Observable<PermissionNodeList> getPermissionNodeList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumGetPermissionNodeList().observable();
    }

    public Observable<PermissionVoterList> getPermissionNodeVoterList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumGetPermissionVoterList().observable();
    }


    public Observable<ExecStatusInfo> setAccountPermission(QuorumNode node, String fromAccount, String addAccount, int access) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumSetAccountPermission(addAccount, access, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }

    public Observable<ExecStatusInfo> addAccountToVoterList(QuorumNode node, String address) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return client.quorumAddPermissionVoter(address, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();

    }

    public Observable<ExecStatusInfo> removeAccountFromVoterList(QuorumNode node, String address) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return client.quorumRemovePermissionVoter(address, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();

    }

    public Observable<ExecStatusInfo> proposeNodeDeactivation(QuorumNode node, String nodeId) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return client.quorumProposePermissionNodeDeactivation(nodeId, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }

    public Observable<ExecStatusInfo> approveNodeDeactivation(QuorumNode node, String nodeId) {
        Quorum client = connectionFactory().getConnection(node);
        String fromAccount = accountService.getDefaultAccountAddress(node).toBlocking().first();
        return client.quorumApprovePermissionNodeDeactivation(nodeId, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }
}
