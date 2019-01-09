package com.quorum.gauge.services;

import com.quorum.gauge.common.QuorumNode;
import org.springframework.stereotype.Service;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.response.PermissionAccountList;
import org.web3j.quorum.methods.response.PermissionNodeList;
import rx.Observable;

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
}
