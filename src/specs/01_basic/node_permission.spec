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


## Check add / remove voters works as expected
* Add account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" to voter list from "Node1"
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" is present in voter list from "Node1"
* Remove account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" to voter list from "Node1"
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" is not present in voter list from "Node1"
* Add account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" to voter list from "Node1"
* Propose node deactivation for nodeId "enodeid" from "Node1"
* Approve node deactivation for nodeId "enodeid" from "Node1"
*
