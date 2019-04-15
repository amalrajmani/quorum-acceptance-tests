# Permission for a given network

 Tags: permission


## Set up config
* network admin org is "NWADMIN"
* network admin role is "NWADMIN"
* org admin role is "OADMIN"



## Check the initial status of network after boot up
* get netowrk details from "Node1"
* check org "NWADMIN" is "Approved" with no parent, level "1" and empty sub orgs
* check org "NWADMIN" has "Node1" with status "Approved"
* check org "NWADMIN" has "Node2" with status "Approved"
* check org "NWADMIN" has "Node3" with status "Approved"
* check org "NWADMIN" has "Node4" with status "Approved"
* check org "NWADMIN" has role "NWADMIN" with access "FullAccess" and permission to vote and is active
* check "Node1"'s default account is from org "NWADMIN" and has role "NWADMIN" and is org admin and is active


## Check the org management
* from "Node1" add new org "JPM" with enode "enode://3701f007bfa4cb26512d7df18e6bbd202e8484a6e11d387af6e482b525fa25542d46ff9c99db87bd419b980c24a086117a397f6d8f88e74351b41693880ea0cb@127.0.0.1:21004?discport=0&raftport=50405" and "Node2"'s default account
* check org "JPM" is "Proposed" with no parent, level "1" and empty sub orgs
* from "Node1" approve new org "JPM" with enode "enode://3701f007bfa4cb26512d7df18e6bbd202e8484a6e11d387af6e482b525fa25542d46ff9c99db87bd419b980c24a086117a397f6d8f88e74351b41693880ea0cb@127.0.0.1:21004?discport=0&raftport=50405" and "Node2"'s default account
* check org "JPM" is "Approved" with no parent, level "1" and empty sub orgs
* check org "JPM" has enode "enode://3701f007bfa4cb26512d7df18e6bbd202e8484a6e11d387af6e482b525fa25542d46ff9c99db87bd419b980c24a086117a397f6d8f88e74351b41693880ea0cb@127.0.0.1:21004?discport=0&raftport=50405" with status "Approved"
* check org "JPM" has role "OADMIN" with access "FullAccess" and permission to vote and is active
* check "Node2"'s default account is from org "JPM" and has role "OADMIN" and is org admin and is active


## Check the sub org management - update org status and node status
* from "Node2" add new sub org "CCB" under org "JPM"
* check org "JPM.CCB" is "Approved" with parent "JPM", level "2" and empty sub orgs
* check org "JPM" is "Approved" with no parent, level "1" and sub orgs "JPM.CCB"
* from "Node2" under org "JPM.CCB" add enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50406"
* check org "JPM.CCB" has enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50406" with status "Approved"
* from "Node2" add new sub org "GTI" under org "JPM"
* check org "JPM.GTI" is "Approved" with parent "JPM", level "2" and empty sub orgs
* check org "JPM" is "Approved" with no parent, level "1" and sub orgs "JPM.CCB,JPM.GTI"
* from "Node2" under org "JPM.GTI" add enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407"
* check org "JPM.GTI" has enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Approved"
* from "Node2" update status of org "JPM.GTI"'s enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Deactivated"
* check org "JPM.GTI" has enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Deactivated"
* from "Node2" update status of org "JPM.GTI"'s enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Activated"
* check org "JPM.GTI" has enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Activated"
* from "Node2" update status of org "JPM.GTI"'s enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Blacklisted"
* check org "JPM.GTI" has enode "enode://eacaa74c4b0e7a9e12d2fe5fee6595eda841d6d992c35dbbcc50fcee4aa86dfbbdeff7dc7e72c2305d5a62257f82737a8cffc80474c15c611c037f52db1a3a7b@127.0.0.1:21005?discport=0&raftport=50407" with status "Blacklisted"
* from "Node1" update org "JPM.GTI"'s status to "PendingSuspension"
* check org "JPM.GTI"'s status is "PendingSuspension"
* from "Node1" approve org "JPM.GTI"'s status "PendingSuspension"
* check org "JPM.GTI"'s status is "Suspended"
* from "Node1" update org "JPM.GTI"'s status to "RevokeSuspension"
* check org "JPM.GTI"'s status is "RevokeSuspension"
* from "Node1" approve org "JPM.GTI"'s status "RevokeSuspension"
* check org "JPM.GTI"'s status is "Approved"


## Check role management
* from "Node2" add new role "TRANSACT" under org "JPM.CCB" with access "Transact" and no voting
* check org "JPM.CCB" has role "TRANSACT" with access "Transact" and no permission to vote and is active
* from "Node2" remove role "TRANSACT" from org "JPM.CCB"
* check org "JPM.CCB" has role "TRANSACT" with access "Transact" and no permission to vote and is not active

## Check assiging roles to account
* from "Node2" add new role "ROUSER" under org "JPM.CCB" with access "ReadOnly" and no voting
* check org "JPM.CCB" has role "ROUSER" with access "ReadOnly" and no permission to vote and is active
* from "Node2" add new role "TRANSUSER" under org "JPM.CCB" with access "Transact" and no voting
* check org "JPM.CCB" has role "TRANSUSER" with access "Transact" and no permission to vote and is active
* from "Node2" add new role "CTRUSER" under org "JPM.CCB" with access "ContractDeploy" and no voting
* check org "JPM.CCB" has role "CTRUSER" with access "ContractDeploy" and no permission to vote and is active
* from "Node2" add new role "FULLUSER" under org "JPM.CCB" with access "FullAccess" and no voting
* check org "JPM.CCB" has role "FULLUSER" with access "FullAccess" and no permission to vote and is active
* from "Node2" assign org "JPM.CCB" role "TRANSUSER" to "Node3"'s default account
* check "Node3"'s default account is from org "JPM.CCB" and has role "TRANSUSER" and is not org admin and is active
* Deploy "storec" smart contract with initial value "3" from a default account in "Node1", named this contract as "c3"
* "c3"'s "getc" function execution in "Node3" returns "3"
* "c3"'s "setc" function execution in "Node3" with value "7"
* Deploy "storec" smart contract with initial value "5" from a default account in "Node3" fails with error "Account not authorized for this operation"
* from "Node2" assign org "JPM.CCB" role "CTRUSER" to "Node3"'s default account
* check "Node3"'s default account is from org "JPM.CCB" and has role "CTRUSER" and is not org admin and is active
* Deploy "storec" smart contract with initial value "5" from a default account in "Node3", named this contract as "c4"
* "c4"'s "getc" function execution in "Node3" returns "5"
* "c4"'s "setc" function execution in "Node3" with value "8"
* from "Node2" assign org "JPM.CCB" role "FULLUSER" to "Node3"'s default account
* check "Node3"'s default account is from org "JPM.CCB" and has role "FULLUSER" and is not org admin and is active
* Deploy "storec" smart contract with initial value "5" from a default account in "Node3", named this contract as "c5"
* "c5"'s "getc" function execution in "Node3" returns "5"
* "c5"'s "setc" function execution in "Node3" with value "8"
* from "Node2" assign org "JPM.CCB" role "ROUSER" to "Node3"'s default account
* check "Node3"'s default account is from org "JPM.CCB" and has role "ROUSER" and is not org admin and is active
* Deploy "storec" smart contract with initial value "2" from a default account in "Node1", named this contract as "c2"
* "c2"'s "getc" function execution in "Node3" returns "2"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node3" fails with error "Account not authorized for this operation"
* "c2"'s "setc" function execution in "Node3" with value "7" fails with error "Account not authorized for this operation"

## Check node deactivation
* from "Node1" update status of org "NWADMIN"'s node "Node4" with status "Deactivated"
* check org "NWADMIN" has "Node4" with status "Deactivated"
* Wait for "1" Seconds
* Save current blocknumber from "Node4"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c1"
* Deploy "storec" smart contract with initial value "1" from a default account in "Node1", named this contract as "c2"
* Ensure current blocknumber from "Node4" has not changed
