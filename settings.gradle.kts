rootProject.name = "idp4k-root"

include("idp4k-core")
include("idp4k-spring")
include("idp4k-aop")
include("idp4k-test")

include(":lock-manager:idp4k-lm-postgresql")
include(":response-store:idp4k-rs-postgresql")
