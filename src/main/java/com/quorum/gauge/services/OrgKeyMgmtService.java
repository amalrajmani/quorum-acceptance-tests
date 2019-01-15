package com.quorum.gauge.services;

import com.quorum.gauge.common.QuorumNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.quorum.methods.response.ExecStatusInfo;
import org.web3j.quorum.methods.response.OrgVoterList;
import rx.Observable;

import java.math.BigInteger;

@Service
public class OrgKeyMgmtService extends AbstractService{

    @Autowired
    AccountService accountService;

    public Observable<ExecStatusInfo> addMasterOrg(QuorumNode node, String morgId) {
        String fromAddress = accountService.getDefaultAccountAddress(node).toBlocking().first();
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumAddMasterOrg(morgId,  new PrivateTransaction(fromAddress, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }

    public Observable<ExecStatusInfo> addSubOrg(QuorumNode node, String sorgId, String morgId) {
        String fromAddress = accountService.getDefaultAccountAddress(node).toBlocking().first();
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumAddSubOrg(sorgId, morgId,  new PrivateTransaction(fromAddress, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();

    }

    public Observable<ExecStatusInfo> addOrgVoter(QuorumNode node, String morgId, String voterAcct) {
        String fromAddress = accountService.getDefaultAccountAddress(node).toBlocking().first();
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumAddOrgVoter( morgId, voterAcct, new PrivateTransaction(fromAddress, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }

    public Observable<OrgVoterList> getOrgVoterList(QuorumNode node, String morgId) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumGetOrgVoterList( morgId).observable();
    }
}
