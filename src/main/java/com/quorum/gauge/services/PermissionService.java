package com.quorum.gauge.services;

import com.quorum.gauge.common.QuorumNode;
import org.springframework.stereotype.Service;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.quorum.methods.response.ExecStatusInfo;
import org.web3j.quorum.methods.response.PermissionAccountList;
import org.web3j.quorum.methods.response.PermissionNodeList;
import rx.Observable;

import java.math.BigInteger;

@Service
public class PermissionService extends AbstractService {

    public Observable<PermissionAccountList> getPermissionAccountList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumGetPermissionAccountList().observable();
    }

    public Observable<PermissionNodeList> getPermissionNodeList(QuorumNode node) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumGetPermissionNodeList().observable();
    }

    public Observable<ExecStatusInfo> setAccountPermission(QuorumNode node, String fromAccount, String addAccount, String access) {
        Quorum client = connectionFactory().getConnection(node);
        return client.quorumSetAccountPermission(addAccount, access, new PrivateTransaction(fromAccount, null, DEFAULT_GAS_LIMIT, null, BigInteger.ZERO, null, null, null)).observable();
    }
}
