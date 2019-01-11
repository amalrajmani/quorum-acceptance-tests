# Permission at account level in a given network

accounts by default have read only permission if they have not been granted explicit access via API quorumAcctMgmt.setAccountAccess.
The following access levels are available for accounts.
1. FullAccess - can perform all actions
2. ReadOnly - can only read values by calling contract's getter methods
3. Transact - can transact and read values by calling contract's getter methods but cannot deploy a contract
4. ContractDeploy - can perform all actions

FullAccess and ContractDeploy have the same access level

An account can grant either same access level as its own or lower access level than its own to another account

The initial set of accounts that need access should be initialized via genesis.json config

 Tags: basic, permission

## Check initial account list has right permission
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" has permission "FullAccess"
* Ensure account "0xca843569e3427144cead5e4d5999a3d0ccf92b8e" has permission "FullAccess"
* Ensure account "0x0fbdc686b912d7722dc86510934589e0aaf3b55a" has permission "FullAccess"

## Check account with full access can perform actions as expected
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" has permission "FullAccess"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* "c1"'s "setc" function execution in "Node1" with value "10"
* "c1"'s "getc" function execution in "Node1" returns "10"
* Set account "0x9186eb3d20cbd1f5f992a950d808c4495153abd5" with permission "ReadOnly" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"
* Set account "0x0638e1574728b6d862dd5d3a3e0942c3be47d996" with permission "Transact" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"
* Set account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" with permission "ContractDeploy" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"


## Check account with read-only access can perform actions as expected
* Ensure account "0x9186eb3d20cbd1f5f992a950d808c4495153abd5" has permission "ReadOnly"
* Deploy "storec" smart contract with initial value "2" from a default account in "Node1", named this contract as "c2"
* "c2"'s "getc" function execution in "Node4" returns "2"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node4" fails with error "Account not authorized for this operation"
* "c2"'s "setc" function execution in "Node4" with value "7" fails with error "Account not authorized for this operation"


## Check account with transact access can perform actions as expected
* Ensure account "0x0638e1574728b6d862dd5d3a3e0942c3be47d996" has permission "Transact"
* Deploy "storec" smart contract with initial value "3" from a default account in "Node1", named this contract as "c3"
* "c3"'s "getc" function execution in "Node5" returns "3"
* "c3"'s "setc" function execution in "Node5" with value "7"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node5" fails with error "Account not authorized for this operation"


## Check account with contract-deploy access can perform actions as expected
* Ensure account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" has permission "ContractDeploy"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node6", named this contract as "c4"
* "c4"'s "getc" function execution in "Node6" returns "5"
* "c4"'s "setc" function execution in "Node6" with value "8"

## Check an account can grant either same access or lower access to another account
* Ensure account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" has permission "ContractDeploy"
* Set account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" with permission "Transact" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"
* Set account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" with permission "ContractDeploy" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"
* Ensure account "0x9186eb3d20cbd1f5f992a950d808c4495153abd5" has permission "ReadOnly"
* Set account "0x0638e1574728b6d862dd5d3a3e0942c3be47d996" with permission "FullAccess" from "Node4" as account "0x9186eb3d20cbd1f5f992a950d808c4495153abd5" fails with error "Account does not have sufficient access for operation"
* Set account "0xcc71c7546429a13796cf1bf9228bff213e7ae9cc" with permission "Transact" from "Node5" as account "0x0638e1574728b6d862dd5d3a3e0942c3be47d996"