# Permission at node level in a given network

 Tags: basic, permission


## Check the initial nodes present in permission node list
* Ensure permission node list has "7" nodes in "Node1" when network started
* Ensure node "127.0.0.1:21001" has status "Approved"
* Ensure node "127.0.0.1:21002" has status "Approved"
* Ensure node "127.0.0.1:21003" has status "Approved"
* Ensure node "127.0.0.1:21004" has status "Approved"
* Ensure node "127.0.0.1:21005" has status "Approved"
* Ensure node "127.0.0.1:21006" has status "Approved"
* Ensure node "127.0.0.1:21000" has status "Approved"


## Check voters list management
* Add account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" as voter from "Node1"
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" is present in voter list from "Node1"
* Remove account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" as voter from "Node1"
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" is not present in voter list from "Node1"


## Check node deactivation
* Add account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" as voter from "Node1"
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" is present in voter list from "Node1"
* Propose node deactivation for nodeId "enode://239c1f044a2b03b6c4713109af036b775c5418fe4ca63b04b1ce00124af00ddab7cc088fc46020cdc783b6207efe624551be4c06a994993d8d70f684688fb7cf@127.0.0.1:21006?discport=0&raftport=50447" from "Node1"
* Ensure node "127.0.0.1:21006" has status "PendingDeactivation"
* Approve node deactivation for nodeId "enode://239c1f044a2b03b6c4713109af036b775c5418fe4ca63b04b1ce00124af00ddab7cc088fc46020cdc783b6207efe624551be4c06a994993d8d70f684688fb7cf@127.0.0.1:21006?discport=0&raftport=50447" from "Node1"
* Ensure node "127.0.0.1:21006" has status "Deactivated"
* Wait for "1" Seconds
* Save current blocknumber from "Node7"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c2"
* Ensure current blocknumber from "Node7" has not changed

