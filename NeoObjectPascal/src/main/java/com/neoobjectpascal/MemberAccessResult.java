package com.neoobjectpascal;

class MemberAccessResult {
    final ObjectInstance object;
    final String memberName;

    MemberAccessResult(ObjectInstance object, String memberName) {
        this.object = object;
        this.memberName = memberName;
    }
}
