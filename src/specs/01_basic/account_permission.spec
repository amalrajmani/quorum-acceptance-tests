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
* Check "Node1"'s default account has permission "FullAccess"

## Check account with full access can perform actions as expected
* Check "Node1"'s default account has permission "FullAccess"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* "c1"'s "setc" function execution in "Node1" with value "10"
* "c1"'s "getc" function execution in "Node1" returns "10"
* Set "Node2"'s default account with permission "ReadOnly" from "Node1"'s default account
* Set "Node4"'s default account with permission "Transact" from "Node1"'s default account
* Set "Node3"'s default account with permission "ContractDeploy" from "Node1"'s default account

## Check account with read-only access can perform actions as expected
* Check "Node2"'s default account has permission "ReadOnly"
* Deploy "storec" smart contract with initial value "2" from a default account in "Node1", named this contract as "c2"
* "c2"'s "getc" function execution in "Node2" returns "2"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node2" fails with error "Account not authorized for this operation"
* "c2"'s "setc" function execution in "Node2" with value "7" fails with error "Account not authorized for this operation"


## Check account with transact access can perform actions as expected
* Check "Node4"'s default account has permission "Transact"
* Deploy "storec" smart contract with initial value "3" from a default account in "Node1", named this contract as "c3"
* "c3"'s "getc" function execution in "Node4" returns "3"
* "c3"'s "setc" function execution in "Node4" with value "7"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node4" fails with error "Account not authorized for this operation"


## Check account with contract-deploy access can perform actions as expected
* Check "Node3"'s default account has permission "ContractDeploy"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node3", named this contract as "c4"
* "c4"'s "getc" function execution in "Node3" returns "5"
* "c4"'s "setc" function execution in "Node3" with value "8"

## Check an account can grant either same access or lower access to another account
* Check "Node3"'s default account has permission "ContractDeploy"
* Set "Node3"'s default account with permission "Transact" from "Node1"'s default account
* Set "Node3"'s default account with permission "ContractDeploy" from "Node1"'s default account
* Check "Node2"'s default account has permission "ReadOnly"
* Set "Node3"'s default account with permission "FullAccess" from from "Node2"'s default account fails with error "Account does not have sufficient access for operation"
* Set "Node2"'s default account with permission "Transact" from "Node3"'s default account
