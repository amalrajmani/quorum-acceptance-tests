# Org Key management service for the network

 Tags: basic, permission

## Org key management
* Add master org "SBI" from "Node1"
* Add sub org "SBI1" under master org "SBI" from "Node1"
* Wait for "1" Seconds
* Validate master org "SBI" and sub org "SBI1" are present as a part of the org list at smart contract from "Node1"
* Add account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" as voter for master org "SBI" from "Node1"
* Wait for "1" Seconds
* Validate account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" is present as voter for master org "SBI" from "Node1"
* Add key "QfeDAys9MPDs2XHExtc84jKGHxZg/aj52DTh0vtA3Xc=" as a key to sub org "SBI1" from "Node1"
* Wait for "1" Seconds
* Approve pending approval process for sub org "SBI1" from "Node1"
* Wait for "1" Seconds
* Add key "1iTZde/ndBHvzhcl7V68x44Vx7pl8nwx9LqnM/AfJUg=" as a key to sub org "SBI1" from "Node1"
* Wait for "1" Seconds
* Approve pending approval process for sub org "SBI1" from "Node1"
* Wait for "1" Seconds
* Validate key "QfeDAys9MPDs2XHExtc84jKGHxZg/aj52DTh0vtA3Xc=" is present as key for sub org "SBI1" from "Node1"
* Validate key "1iTZde/ndBHvzhcl7V68x44Vx7pl8nwx9LqnM/AfJUg=" is present as key for sub org "SBI1" from "Node1"
* Deploy a simple smart contract with initial value "42" in "Node1"'s default account and it's private for org "SBI1", named this contract as "contract1"
* Transaction Hash is returned for "contract1"
* Transaction Receipt is present in "Node2" for "contract1"
* Transaction Receipt is present in "Node3" for "contract1"
* Delete key "QfeDAys9MPDs2XHExtc84jKGHxZg/aj52DTh0vtA3Xc=" from sub org "SBI1" from "Node1"
* Wait for "1" Seconds
* Approve pending approval process for sub org "SBI1" from "Node1"
* Delete voter "0xed9d02e382b34818e88B88a309c7fe71E65f419d" from master org "SBI" from "Node1"
