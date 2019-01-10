# Permission at account level in a given network

 Tags: basic, permission

## Account permission - initial account list
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" has permission "FullAccess"
* Ensure account "0xca843569e3427144cead5e4d5999a3d0ccf92b8e" has permission "FullAccess"
* Ensure account "0x0fbdc686b912d7722dc86510934589e0aaf3b55a" has permission "FullAccess"

## Account permission - full access - grant access to other accounts, deploy, transact, read
* Ensure account "0xed9d02e382b34818e88B88a309c7fe71E65f419d" has permission "FullAccess"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* "c1"'s "setc" function execution in "Node1" with value "10"
* "c1"'s "getc" function execution in "Node1" returns "10"
* Add account "0x9186eb3d20cbd1f5f992a950d808c4495153abd5" with permission "1" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"
* Add account "0x0638e1574728b6d862dd5d3a3e0942c3be47d996" with permission "2" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"
* Add account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" with permission "3" from "Node1" as account "0xed9d02e382b34818e88B88a309c7fe71E65f419d"


## Account permission - read only - can only read
* Ensure account "0x9186eb3d20cbd1f5f992a950d808c4495153abd5" has permission "ReadOnly"
* Deploy "storec" smart contract with initial value "2" from a default account in "Node1", named this contract as "c2"
* "c2"'s "getc" function execution in "Node4" returns "2"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node4" fails with error "Account not authorized for this operation"


## Account permission - transact - transact and read only
* Ensure account "0x0638e1574728b6d862dd5d3a3e0942c3be47d996" has permission "Transact"
* Deploy "storec" smart contract with initial value "3" from a default account in "Node1", named this contract as "c3"
* "c3"'s "getc" function execution in "Node5" returns "3"
* "c3"'s "setc" function execution in "Node5" with value "7"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node5" fails with error "Account not authorized for this operation"


## Account permission - contract deploy - deploy, transact and read
* Ensure account "0xae9bc6cd5145e67fbd1887a5145271fd182f0ee7" has permission "ContractDeploy"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node6", named this contract as "c4"
* "c4"'s "getc" function execution in "Node6" returns "5"
* "c4"'s "setc" function execution in "Node6" with value "8"
