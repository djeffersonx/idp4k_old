-----------------
# idp4k

###### _Idempotence for kotlin._

-----------------

This is a sort set projects to easily reach idempotent processes in kotlin.

### idp4k-core

This project defines the core of the idp4k project provinding the `LockManager` interface and creating the `IdempotenceManager` implementation that has the responsibility to manage the process of idempotence using the `LockManager` to decide treatment to provide to the actual execution.

### idp4k-spring

Provides a Spring auto-configuration.

### idp4k-aop

...

### idp4k-test

...

### idp4k-postgresql

...







References:

    https://github.com/alturkovic/distributed-lock
