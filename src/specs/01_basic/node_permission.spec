# Permission at node level in a given network

 Tags: basic, permission


## Check the initial nodes present in permission node list
* Ensure permission node list has "4" nodes in "Node1" when network started
* Ensure node "127.0.0.1:21000" has status "Approved"
* Ensure node "127.0.0.1:21001" has status "Approved"
* Ensure node "127.0.0.1:21002" has status "Approved"
* Ensure node "127.0.0.1:21003" has status "Approved"


## Check voters list management
* Add "Node1"'s default account as voter from "Node1"
* Check "Node1"'s default account is present in voter list from "Node1"
* Remove "Node1"'s default account as voter from "Node1"
* Check "Node1"'s default account is not present in voter list from "Node1"


## Check node deactivation
* Add "Node1"'s default account as voter from "Node1"
* Check "Node1"'s default account is present in voter list from "Node1"
* Propose node deactivation for nodeId "enode://3d9ca5956b38557aba991e31cf510d4df641dce9cc26bfeb7de082f0c07abb6ede3a58410c8f249dabeecee4ad3979929ac4c7c496ad20b8cfdd061b7401b4f5@127.0.0.1:21003?discport=0&raftport=50404" from "Node1"
* Ensure node "127.0.0.1:21003" has status "PendingDeactivation"
* Approve node deactivation for nodeId "enode://3d9ca5956b38557aba991e31cf510d4df641dce9cc26bfeb7de082f0c07abb6ede3a58410c8f249dabeecee4ad3979929ac4c7c496ad20b8cfdd061b7401b4f5@127.0.0.1:21003?discport=0&raftport=50404" from "Node1"
* Ensure node "127.0.0.1:21003" has status "Deactivated"
* Wait for "1" Seconds
* Save current blocknumber from "Node4"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c2"
* Ensure current blocknumber from "Node4" has not changed

