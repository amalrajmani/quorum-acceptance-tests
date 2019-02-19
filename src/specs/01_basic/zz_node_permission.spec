# Permission at node level in a given network

 Tags: basic, permission


## Check the initial nodes present in permission node list
* Ensure permission node list has "4" nodes in "Node1" when network started
* Check "Node1" has status "Approved"
* Check "Node2" has status "Approved"
* Check "Node3" has status "Approved"
* Check "Node4" has status "Approved"


## Check voters list management
* Add "Node1"'s default account as voter from "Node1"
* Check "Node1"'s default account is present in voter list from "Node1"
* Remove "Node1"'s default account as voter from "Node1"
* Check "Node1"'s default account is not present in voter list from "Node1"


## Check node deactivation
* Add "Node1"'s default account as voter from "Node1"
* Check "Node1"'s default account is present in voter list from "Node1"
* Propose node deactivation for "Node4" from "Node1"
* Check "Node4" has status "PendingDeactivation"
* Approve node deactivation for "Node4" from "Node1"
* Check "Node4" has status "Deactivated"
* Wait for "1" Seconds
* Save current blocknumber from "Node4"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c2"
* Ensure current blocknumber from "Node4" has not changed

